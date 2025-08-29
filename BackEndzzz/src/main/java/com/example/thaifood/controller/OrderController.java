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
@RequestMapping("/api/orders")
@CrossOrigin(origins = "*")
public class OrderController {

    private static final Logger logger = LoggerFactory.getLogger(OrderController.class);

    @Autowired
    private DataSource dataSource;

    // ค้นหา column อัตโนมัติ
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

    // GET /api/orders
    @GetMapping
    public ResponseEntity<?> getAllOrders() {
        List<Map<String, Object>> orders = new ArrayList<>();
        try (Connection conn = dataSource.getConnection()) {
            List<String> cols = discoverColumns(conn, "orders");
            if (cols.isEmpty()) {
                return ResponseEntity.internalServerError().body("No columns found for table 'orders'");
            }

            String sql = "SELECT * FROM orders";
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(sql)) {
                while (rs.next()) {
                    Map<String, Object> row = new LinkedHashMap<>();
                    for (String c : cols) {
                        row.put(c, rs.getObject(c));
                    }
                    orders.add(row);
                }
            }
            return ResponseEntity.ok(orders);
        } catch (Exception e) {
            logger.error("Error fetching orders", e);
            return ResponseEntity.internalServerError().body("Error fetching orders: " + e.getMessage());
        }
    }

    // POST /api/orders
    @PostMapping
    public ResponseEntity<?> createOrder(@RequestBody Map<String, Object> orderRequest) {
        try (Connection conn = dataSource.getConnection()) {
            List<String> cols = discoverColumns(conn, "orders");
            if (cols.isEmpty()) {
                return ResponseEntity.internalServerError().body("No columns found for table 'orders'");
            }

            List<String> insertCols = new ArrayList<>();
            List<Object> insertVals = new ArrayList<>();

            if (cols.contains("customer_id")) {
                insertCols.add("customer_id");
                insertVals.add(orderRequest.get("customer_id"));
            }
            if (cols.contains("order_date")) {
                insertCols.add("order_date");
                Object od = orderRequest.getOrDefault("order_date", Timestamp.valueOf(LocalDateTime.now()));
                insertVals.add(od instanceof String ? Timestamp.valueOf((String) od) : od);
            }
            if (cols.contains("status")) {
                insertCols.add("status");
                insertVals.add(orderRequest.getOrDefault("status", "pending"));
            }
            if (cols.contains("amount")) {
                insertCols.add("amount");
                insertVals.add(orderRequest.get("amount"));
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

            String insertSql = "INSERT INTO orders (" + sjCols + ") VALUES (" + sjParams + ")";
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
                        if (cols.stream().anyMatch(c -> c.equalsIgnoreCase("id"))) {
                            fetchSql = "SELECT * FROM orders WHERE id = ?";
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
            logger.error("Error creating order", e);
            return ResponseEntity.internalServerError().body("Error creating order: " + e.getMessage());
        }
    }

    // GET /api/orders/{id}
    @GetMapping("/{id}")
    public ResponseEntity<?> getOrderById(@PathVariable Object id) {
        try (Connection conn = dataSource.getConnection()) {
            String[] candidates = new String[]{"id"};
            String foundCol = null;
            List<String> cols = discoverColumns(conn, "orders");
            for (String c : candidates) {
                if (cols.stream().anyMatch(x -> x.equalsIgnoreCase(c))) {
                    foundCol = cols.stream().filter(x -> x.equalsIgnoreCase(c)).findFirst().get();
                    break;
                }
            }
            if (foundCol == null) {
                return ResponseEntity.internalServerError().body("No PK column found for orders");
            }

            String sql = "SELECT * FROM orders WHERE " + foundCol + " = ?";
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
            logger.error("Error fetching order", e);
            return ResponseEntity.internalServerError().body("Error fetching order: " + e.getMessage());
        }
    }
}
