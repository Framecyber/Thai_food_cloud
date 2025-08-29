package com.example.thaifood.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.sql.DataSource;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.*;

@RestController
@RequestMapping("/api/categories")
@CrossOrigin(origins = "*")
public class CategoryController {

    private static final Logger logger = LoggerFactory.getLogger(CategoryController.class);

    @Autowired
    private DataSource dataSource;

    // Discover columns at runtime to avoid hard-coded column name mismatches
    private List<String> discoverColumns(Connection conn, String tableName) throws SQLException {
        List<String> cols = new ArrayList<>();
        DatabaseMetaData meta = conn.getMetaData();
        try (ResultSet rs = meta.getColumns(null, null, tableName, null)) {
            while (rs.next()) {
                cols.add(rs.getString("COLUMN_NAME"));
            }
        }
        return cols;
    }

    @GetMapping
    public ResponseEntity<?> getAllCategories() {
        List<Map<String, Object>> categories = new ArrayList<>();

        try (Connection conn = dataSource.getConnection()) {
            List<String> cols = discoverColumns(conn, "categories");
            if (cols.isEmpty()) {
                return ResponseEntity.internalServerError().body("No columns found for table 'categories'");
            }

            String sql = "SELECT * FROM categories";
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(sql)) {

                while (rs.next()) {
                    Map<String, Object> row = new LinkedHashMap<>();
                    for (String c : cols) {
                        row.put(c, rs.getObject(c));
                    }
                    categories.add(row);
                    logger.debug("Row: {}", row);
                }
            }

            return ResponseEntity.ok(categories);
        } catch (Exception e) {
            logger.error("Error fetching categories", e);
            return ResponseEntity.internalServerError()
                    .body("Error fetching categories: " + e.getMessage());
        }
    }

    @PostMapping
    public ResponseEntity<?> createCategory(@RequestBody Map<String, Object> categoryRequest) {
        try (Connection conn = dataSource.getConnection()) {
            List<String> cols = discoverColumns(conn, "categories");
            if (cols.isEmpty()) {
                return ResponseEntity.internalServerError().body("No columns found for table 'categories'");
            }

            // Determine required name column(s)
            boolean hasCategoryName = cols.stream().anyMatch(c -> c.equalsIgnoreCase("category_name"));
            boolean hasName = cols.stream().anyMatch(c -> c.equalsIgnoreCase("name"));

            String providedName = (String) categoryRequest.getOrDefault("category_name", categoryRequest.get("name"));
            if ((hasCategoryName || hasName) && (providedName == null || providedName.toString().trim().isEmpty())) {
                return ResponseEntity.badRequest().body("category_name (or name) is required");
            }

            // Build insert columns and values dynamically, but ensure we set both name columns if present
            List<String> insertCols = new ArrayList<>();
            List<Object> insertVals = new ArrayList<>();

            // If table has category_name, set it
            if (hasCategoryName) {
                insertCols.add("category_name");
                insertVals.add(providedName);
            }
            // If table has name, set it too (duplicate) to satisfy NOT NULL
            if (hasName) {
                insertCols.add("name");
                insertVals.add(providedName);
            }
            // description
            if (cols.stream().anyMatch(c -> c.equalsIgnoreCase("description"))) {
                insertCols.add("description");
                insertVals.add(categoryRequest.getOrDefault("description", null));
            }
            // created_at
            if (cols.stream().anyMatch(c -> c.equalsIgnoreCase("created_at"))) {
                insertCols.add("created_at");
                insertVals.add(Timestamp.valueOf(LocalDateTime.now()));
            }

            if (insertCols.isEmpty()) {
                return ResponseEntity.badRequest().body("No supported columns to insert");
            }

            StringJoiner sjCols = new StringJoiner(", ");
            StringJoiner sjParams = new StringJoiner(", ");
            for (int i = 0; i < insertCols.size(); i++) {
                sjCols.add(insertCols.get(i));
                sjParams.add("?");
            }

            String insertSql = "INSERT INTO categories (" + sjCols.toString() + ") VALUES (" + sjParams.toString() + ")";
            logger.debug("Insert SQL: {}", insertSql);

            try (PreparedStatement pstmt = conn.prepareStatement(insertSql, Statement.RETURN_GENERATED_KEYS)) {
                for (int i = 0; i < insertVals.size(); i++) {
                    pstmt.setObject(i + 1, insertVals.get(i));
                }
                int affected = pstmt.executeUpdate();
                if (affected == 0) {
                    throw new SQLException("Insert affected 0 rows");
                }

                Map<String, Object> created = new LinkedHashMap<>();
                try (ResultSet gk = pstmt.getGeneratedKeys()) {
                    if (gk.next()) {
                        // try to find the generated PK column name from metadata; otherwise, attempt common names
                        ResultSetMetaData gkmd = gk.getMetaData();
                        String pkCol = null;
                        for (int i = 1; i <= gkmd.getColumnCount(); i++) {
                            pkCol = gkmd.getColumnName(i);
                            break;
                        }
                        Object pkVal = gk.getObject(1);

                        // fetch the created row using the PK if we can identify it
                        String fetchSql = null;
                        if (cols.stream().anyMatch(c -> c.equalsIgnoreCase("category_id"))) {
                            fetchSql = "SELECT * FROM categories WHERE category_id = ?";
                        } else if (cols.stream().anyMatch(c -> c.equalsIgnoreCase("id"))) {
                            fetchSql = "SELECT * FROM categories WHERE id = ?";
                        } else if (pkCol != null) {
                            fetchSql = "SELECT * FROM categories WHERE " + pkCol + " = ?";
                        }

                        if (fetchSql != null) {
                            try (PreparedStatement fetch = conn.prepareStatement(fetchSql)) {
                                fetch.setObject(1, pkVal);
                                try (ResultSet rs = fetch.executeQuery()) {
                                    ResultSetMetaData md = rs.getMetaData();
                                    int cc = md.getColumnCount();
                                    if (rs.next()) {
                                        for (int i = 1; i <= cc; i++) {
                                            created.put(md.getColumnName(i), rs.getObject(i));
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                if (created.isEmpty()) {
                    // fallback: return success with the provided values
                    for (int i = 0; i < insertCols.size(); i++) {
                        created.put(insertCols.get(i), insertVals.get(i));
                    }
                }

                return ResponseEntity.ok(created);
            }

        } catch (Exception e) {
            logger.error("Error creating category", e);
            return ResponseEntity.internalServerError().body("Error creating category: " + e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getCategoryById(@PathVariable Object id) {
        try (Connection conn = dataSource.getConnection()) {
            // try several PK column names
            String[] candidates = new String[]{"category_id", "id", "categoryId"};
            String foundCol = null;
            List<String> cols = discoverColumns(conn, "categories");
            for (String c : candidates) {
                if (cols.stream().anyMatch(x -> x.equalsIgnoreCase(c))) {
                    foundCol = cols.stream().filter(x -> x.equalsIgnoreCase(c)).findFirst().get();
                    break;
                }
            }
            if (foundCol == null) {
                return ResponseEntity.internalServerError().body("No PK column found for categories");
            }

            String sql = "SELECT * FROM categories WHERE " + foundCol + " = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setObject(1, id);
                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        Map<String, Object> row = new LinkedHashMap<>();
                        ResultSetMetaData md = rs.getMetaData();
                        for (int i = 1; i <= md.getColumnCount(); i++) {
                            row.put(md.getColumnName(i), rs.getObject(i));
                        }
                        return ResponseEntity.ok(row);
                    }
                }
            }

            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            logger.error("Error fetching category", e);
            return ResponseEntity.internalServerError().body("Error fetching category: " + e.getMessage());
        }
    }
}
