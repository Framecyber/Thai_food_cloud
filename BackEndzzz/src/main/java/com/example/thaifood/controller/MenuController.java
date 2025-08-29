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
@RequestMapping("/api/menus")
@CrossOrigin(origins = {"http://localhost:4200",
                        "http://bucketfontporpear.s3-website-us-east-1.amazonaws.com",
                        "https://d3lf594kh0iern.cloudfront.net"})
public class MenuController {

    private static final Logger logger = LoggerFactory.getLogger(MenuController.class);

    @Autowired
    private DataSource dataSource;

    // ใช้ค้นหา column ของ table menus อัตโนมัติ
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

    // GET /api/menus
    @GetMapping
    public ResponseEntity<?> getAllMenus() {
        List<Map<String, Object>> menus = new ArrayList<>();
        try (Connection conn = dataSource.getConnection()) {
            List<String> cols = discoverColumns(conn, "menu_items");
            if (cols.isEmpty()) {
                return ResponseEntity.internalServerError().body("No columns found for table 'menus'");
            }

            String sql = "SELECT * FROM menu_items";
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(sql)) {
                while (rs.next()) {
                    Map<String, Object> row = new LinkedHashMap<>();
                    for (String c : cols) {
                        row.put(c, rs.getObject(c));
                    }
                    menus.add(row);
                }
            }
            return ResponseEntity.ok(menus);
        } catch (Exception e) {
            logger.error("Error fetching menus", e);
            return ResponseEntity.internalServerError().body("Error fetching menus: " + e.getMessage());
        }
    }

    // POST /api/menus
    @PostMapping
    public ResponseEntity<?> createMenu(@RequestBody Map<String, Object> menuRequest) {
        try (Connection conn = dataSource.getConnection()) {
            List<String> cols = discoverColumns(conn, "menu_items");
            if (cols.isEmpty()) {
                return ResponseEntity.internalServerError().body("No columns found for table 'menus'");
            }

            boolean hasMenuName = cols.stream().anyMatch(c -> c.equalsIgnoreCase("item_name"));
            boolean hasName = cols.stream().anyMatch(c -> c.equalsIgnoreCase("name"));

            String providedName = (String) menuRequest.getOrDefault("item_name", menuRequest.get("name"));
            if ((hasMenuName || hasName) && (providedName == null || providedName.toString().trim().isEmpty())) {
                return ResponseEntity.badRequest().body("item_name (or name) is required");
            }

            List<String> insertCols = new ArrayList<>();
            List<Object> insertVals = new ArrayList<>();

            if (hasMenuName) {
                insertCols.add("item_name");
                insertVals.add(providedName);
            }
            if (hasName) {
                insertCols.add("name");
                insertVals.add(providedName);
            }
            if (cols.stream().anyMatch(c -> c.equalsIgnoreCase("description"))) {
                insertCols.add("description");
                insertVals.add(menuRequest.getOrDefault("description", null));
            }
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

            String insertSql = "INSERT INTO menu_items (" + sjCols + ") VALUES (" + sjParams + ")";
            logger.debug("Insert SQL: {}", insertSql);

            Map<String, Object> created = new LinkedHashMap<>();
            try (PreparedStatement pstmt = conn.prepareStatement(insertSql, Statement.RETURN_GENERATED_KEYS)) {
                for (int i = 0; i < insertVals.size(); i++) {
                    pstmt.setObject(i + 1, insertVals.get(i));
                }
                int affected = pstmt.executeUpdate();
                if (affected == 0) {
                    throw new SQLException("Insert affected 0 rows");
                }

                try (ResultSet gk = pstmt.getGeneratedKeys()) {
                    if (gk.next()) {
                        Object pkVal = gk.getObject(1);

                        String fetchSql = null;
                        if (cols.stream().anyMatch(c -> c.equalsIgnoreCase("id_item"))) {
                            fetchSql = "SELECT * FROM menu_items WHERE id_item = ?";
                        } else if (cols.stream().anyMatch(c -> c.equalsIgnoreCase("id"))) {
                            fetchSql = "SELECT * FROM menu_items WHERE id = ?";
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
            }

            return ResponseEntity.ok(created);
        } catch (Exception e) {
            logger.error("Error creating menu", e);
            return ResponseEntity.internalServerError().body("Error creating menu: " + e.getMessage());
        }
    }

    // GET /api/menus/{id}
    @GetMapping("/{id}")
    public ResponseEntity<?> getMenuById(@PathVariable Object id) {
        try (Connection conn = dataSource.getConnection()) {
            String[] candidates = new String[]{"id_item", "id", "Iditem"};
            String foundCol = null;
            List<String> cols = discoverColumns(conn, "menu_items");
            for (String c : candidates) {
                if (cols.stream().anyMatch(x -> x.equalsIgnoreCase(c))) {
                    foundCol = cols.stream().filter(x -> x.equalsIgnoreCase(c)).findFirst().get();
                    break;
                }
            }
            if (foundCol == null) {
                return ResponseEntity.internalServerError().body("No PK column found for menus");
            }

            String sql = "SELECT * FROM menu_items WHERE " + foundCol + " = ?";
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
            logger.error("Error fetching menu", e);
            return ResponseEntity.internalServerError().body("Error fetching menu: " + e.getMessage());
        }
    }
}
