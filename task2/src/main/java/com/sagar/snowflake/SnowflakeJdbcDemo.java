package com.sagar.snowflake;

import java.sql.*;
import java.util.Properties;
import java.io.*;
import java.nio.file.*;

public class SnowflakeJdbcDemo {

    private static String loadEnvVariable(String key) {
        // First try system environment variable
        String value = System.getenv(key);
        if (value != null && !value.isEmpty()) {
            return value;
        }

        // Then try reading from .env file
        try {
            Path envPath = Paths.get(".env");
            if (Files.exists(envPath)) {
                BufferedReader reader = Files.newBufferedReader(envPath);
                String line;
                while ((line = reader.readLine()) != null) {
                    line = line.trim();
                    if (!line.isEmpty() && !line.startsWith("#")) {
                        String[] parts = line.split("=", 2);
                        if (parts.length == 2 && parts[0].trim().equals(key)) {
                            reader.close();
                            return parts[1].trim();
                        }
                    }
                }
                reader.close();
            }
        } catch (IOException e) {
            // Ignore file reading errors
        }

        return null;
    }

    private static Properties parseConnectionStringParams(String connectionString) {
        Properties properties = new Properties();

        if (connectionString.contains("?")) {
            String paramString = connectionString.split("\\?")[1];
            String[] pairs = paramString.split("&");

            for (String pair : pairs) {
                String[] keyValue = pair.split("=", 2);
                if (keyValue.length == 2) {
                    properties.put(keyValue[0], keyValue[1]);
                }
            }
        }

        return properties;
    }

    public static void main(String[] args) {
        // Option 1: Use full JDBC connection string
        String connectionString = loadEnvVariable("SNOWFLAKE_CONNECTION_STRING");

        String url;
        Properties properties;

        if (connectionString != null && !connectionString.isEmpty()) {
            // Parse connection string
            url = connectionString.split("\\?")[0];
            properties = parseConnectionStringParams(connectionString);
            // Force JSON format to avoid Arrow/Java module issues
            properties.put("CLIENT_RESULT_FORMAT", "JSON");
            System.out.println("Using connection string from environment variable");
        } else {
            // Option 2: Use individual environment variables
            String username = System.getenv("SNOWFLAKE_USER");
            String password = System.getenv("SNOWFLAKE_PASSWORD");
            String account = System.getenv("SNOWFLAKE_ACCOUNT");
            String warehouse = System.getenv("SNOWFLAKE_WAREHOUSE");
            String database = System.getenv("SNOWFLAKE_DATABASE");
            String schema = System.getenv("SNOWFLAKE_SCHEMA");

            // Validate environment variables
            if (username == null || password == null || account == null) {
                System.err.println("Error: Required environment variables not set.");
                System.err.println("Either set SNOWFLAKE_CONNECTION_STRING");
                System.err.println("Or set: SNOWFLAKE_USER, SNOWFLAKE_PASSWORD, SNOWFLAKE_ACCOUNT");
                System.err.println("Optional: SNOWFLAKE_WAREHOUSE, SNOWFLAKE_DATABASE, SNOWFLAKE_SCHEMA");
                System.exit(1);
            }

            // Set defaults if not provided
            if (warehouse == null) warehouse = "NEWS_WH";
            if (database == null) database = "NEWS_DB";
            if (schema == null) schema = "NEWS_SCHEMA";

            // Build connection URL
            url = "jdbc:snowflake://" + account + ".snowflakecomputing.com";

            // Set connection properties
            properties = new Properties();
            properties.put("user", username);
            properties.put("password", password);
            properties.put("warehouse", warehouse);
            properties.put("db", database);
            properties.put("schema", schema);
            System.out.println("Using individual environment variables");
        }

        Connection connection = null;

        try {
            // Load Snowflake JDBC driver explicitly
            Class.forName("net.snowflake.client.jdbc.SnowflakeDriver");

            // Establish connection
            System.out.println("Connecting to Snowflake...");
            connection = DriverManager.getConnection(url, properties);
            System.out.println("Connected successfully!");

            // Execute SELECT query with fully qualified table name
            String sql = """
                    SELECT *
                    FROM NEWS_DB.NEWS_SCHEMA.NEWS
                    ORDER BY id
                    """;

            PreparedStatement statement = connection.prepareStatement(sql);
            ResultSet resultSet = statement.executeQuery();

            // Process results
            System.out.println("\n=== News Articles ===");

            // Get metadata to see available columns
            ResultSetMetaData metaData = resultSet.getMetaData();
            int columnCount = metaData.getColumnCount();
            System.out.println("Available columns:");
            for (int i = 1; i <= columnCount; i++) {
                System.out.println("  - " + metaData.getColumnName(i));
            }
            System.out.println();

            while (resultSet.next()) {
                StringBuilder row = new StringBuilder();
                for (int i = 1; i <= columnCount; i++) {
                    if (i > 1) row.append(" | ");
                    row.append(metaData.getColumnName(i)).append(": ").append(resultSet.getString(i));
                }
                System.out.println(row.toString());
            }

            resultSet.close();
            statement.close();

        } catch (SQLException e) {
            System.err.println("Error connecting to Snowflake:");
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        } finally {
            // Close connection
            if (connection != null) {
                try {
                    connection.close();
                    System.out.println("\nConnection closed.");
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
