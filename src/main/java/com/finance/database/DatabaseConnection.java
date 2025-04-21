package com.finance.database;

import java.sql.*;
import java.util.Properties;
import java.io.InputStream;
import java.io.IOException;

public class DatabaseConnection {

    private static Properties prop = new Properties();
    private static final String SYSTEM_DB_URL = "jdbc:postgresql://localhost:5432/postgres";
    private static String DB_NAME;
    private static String USER;
    private static String PASSWORD;

    private static Connection connection;

    static {
        try (InputStream input = DatabaseConnection.class.getClassLoader().getResourceAsStream("db.properties")) {
            if (input == null) {
                throw new IOException("The file 'db.properties' was not found");
            }
            prop.load(input);
            DB_NAME = prop.getProperty("db.name");
            USER = prop.getProperty("db.user");
            PASSWORD = prop.getProperty("db.password");

            createDatabaseIfNotExists();

            connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/" + DB_NAME, USER, PASSWORD);
            System.out.println("Connection to database '" + DB_NAME + "' successful!");

        } catch (IOException | SQLException ex) {
            ex.printStackTrace();
            throw new RuntimeException("Fehler beim Laden der Datenbankkonfiguration oder beim Verbindungsaufbau: " + ex.getMessage());
        }
    }

    public static Connection getConnection() {
        return connection;
    }

    // Initialise database and tables
    public static void initializeDatabase() {
        createTransactionsTableIfNotExists();
        createIncomeTableIfNotExists();
        createBalanceTableIfNotExists();
    }

    private static void createDatabaseIfNotExists() {
        String sql = "SELECT 1 FROM pg_database WHERE datname = ?";

        try (Connection conn = DriverManager.getConnection(SYSTEM_DB_URL, USER, PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, DB_NAME);
            ResultSet rs = stmt.executeQuery();

            if (!rs.next()) {
                System.out.println("Database '" + DB_NAME + "' does not exist. Create...");
                try (Statement createStmt = conn.createStatement()) {
                    createStmt.executeUpdate("CREATE DATABASE " + DB_NAME);
                    System.out.println("Database created.");
                }
            } else {
                System.out.println("Database exists.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void createTransactionsTableIfNotExists() {
        String sql = "CREATE TABLE IF NOT EXISTS transactions (" +
                     "id SERIAL PRIMARY KEY, " +
                     "date DATE NOT NULL, " +
                     "category VARCHAR(255) NOT NULL, " +
                     "amount NUMERIC(10,2) NOT NULL, " +
                     "description TEXT);";

        try (Statement stmt = connection.createStatement()) {
            stmt.executeUpdate(sql);
            System.out.println("Table 'transactions' checked/created.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void createIncomeTableIfNotExists() {
        String sql = "CREATE TABLE IF NOT EXISTS income (" +
                     "id SERIAL PRIMARY KEY, " +
                     "date DATE NOT NULL, " +
                     "amount NUMERIC(10,2) NOT NULL);";

        try (Statement stmt = connection.createStatement()) {
            stmt.executeUpdate(sql);
            System.out.println("Table 'income' checked/created.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void createBalanceTableIfNotExists() {
        String createTableSQL = "CREATE TABLE IF NOT EXISTS balance (" +
                                "id SERIAL PRIMARY KEY, " +
                                "balance NUMERIC(10,2) NOT NULL);";
    
        String insertBalanceSQL = "INSERT INTO balance (balance) SELECT 0 WHERE NOT EXISTS (SELECT 1 FROM balance);";
    
        try (Statement stmt = connection.createStatement()) {
            stmt.executeUpdate(createTableSQL);
            System.out.println("Table 'balance' checked/created.");
    
            stmt.executeUpdate(insertBalanceSQL);
            System.out.println("Initial balance has been checked/inserted.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
}
