package com.example.thaifood.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/test")
@CrossOrigin(origins = "*")
public class DatabaseTestController {

    private static final Logger logger = LoggerFactory.getLogger(DatabaseTestController.class);

    @Autowired
    private DataSource dataSource;

    @GetMapping("/connection")
    public Map<String, Object> testConnection() {
        Map<String, Object> result = new HashMap<>();
        
        try (Connection connection = dataSource.getConnection()) {
            DatabaseMetaData metaData = connection.getMetaData();
            
            result.put("connected", true);
            result.put("databaseName", metaData.getDatabaseProductName());
            result.put("databaseVersion", metaData.getDatabaseProductVersion());
            result.put("url", metaData.getURL());
            result.put("username", metaData.getUserName());
            
            logger.info("Database connection successful");
            return result;
            
        } catch (Exception e) {
            logger.error("Database connection failed", e);
            result.put("connected", false);
            result.put("error", e.getMessage());
            return result;
        }
    }

    @GetMapping("/tables")
    public Map<String, Object> listTables() {
        Map<String, Object> result = new HashMap<>();
        List<String> tables = new ArrayList<>();
        
        try (Connection connection = dataSource.getConnection()) {
            DatabaseMetaData metaData = connection.getMetaData();
            
            // List all tables
            try (ResultSet rs = metaData.getTables(null, null, "%", new String[]{"TABLE"})) {
                while (rs.next()) {
                    String tableName = rs.getString("TABLE_NAME");
                    tables.add(tableName);
                    logger.info("Found table: {}", tableName);
                }
            }
            
            result.put("success", true);
            result.put("tables", tables);
            result.put("count", tables.size());
            
        } catch (Exception e) {
            logger.error("Error listing tables", e);
            result.put("success", false);
            result.put("error", e.getMessage());
        }
        
        return result;
    }

    @GetMapping("/categories-structure")
    public Map<String, Object> getCategoriesStructure() {
        Map<String, Object> result = new HashMap<>();
        List<Map<String, String>> columns = new ArrayList<>();
        
        try (Connection connection = dataSource.getConnection()) {
            DatabaseMetaData metaData = connection.getMetaData();
            
            // Get column information for categories table
            try (ResultSet rs = metaData.getColumns(null, null, "categories", null)) {
                while (rs.next()) {
                    Map<String, String> column = new HashMap<>();
                    column.put("name", rs.getString("COLUMN_NAME"));
                    column.put("type", rs.getString("TYPE_NAME"));
                    column.put("size", rs.getString("COLUMN_SIZE"));
                    column.put("nullable", rs.getString("IS_NULLABLE"));
                    column.put("autoIncrement", rs.getString("IS_AUTOINCREMENT"));
                    columns.add(column);
                    
                    logger.info("Column: {} - Type: {} - Nullable: {}", 
                        rs.getString("COLUMN_NAME"), 
                        rs.getString("TYPE_NAME"),
                        rs.getString("IS_NULLABLE"));
                }
            }
            
            result.put("success", true);
            result.put("columns", columns);
            result.put("tableName", "categories");
            
        } catch (Exception e) {
            logger.error("Error getting categories structure", e);
            result.put("success", false);
            result.put("error", e.getMessage());
        }
        
        return result;
    }

    @GetMapping("/raw-query")
    public Map<String, Object> testRawQuery() {
        Map<String, Object> result = new HashMap<>();
        List<Map<String, Object>> rows = new ArrayList<>();
        
        try (Connection connection = dataSource.getConnection();
             Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM categories")) {
            
            // Get column names
            int columnCount = rs.getMetaData().getColumnCount();
            List<String> columnNames = new ArrayList<>();
            for (int i = 1; i <= columnCount; i++) {
                columnNames.add(rs.getMetaData().getColumnName(i));
            }
            
            // Get data
            while (rs.next()) {
                Map<String, Object> row = new HashMap<>();
                for (String columnName : columnNames) {
                    row.put(columnName, rs.getObject(columnName));
                }
                rows.add(row);
                logger.info("Raw row data: {}", row);
            }
            
            result.put("success", true);
            result.put("columnNames", columnNames);
            result.put("data", rows);
            result.put("rowCount", rows.size());
            
        } catch (Exception e) {
            logger.error("Error executing raw query", e);
            result.put("success", false);
            result.put("error", e.getMessage());
        }
        
        return result;
    }
}