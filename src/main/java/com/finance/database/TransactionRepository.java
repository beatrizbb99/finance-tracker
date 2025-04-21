package com.finance.database;

import com.finance.model.Transaction;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class TransactionRepository {

    private Connection conn;

    public TransactionRepository() {
        this.conn = DatabaseConnection.getConnection();
    }

    // CREATE (Daten einfügen)
    public int insertTransaction(Transaction transaction) {
        String sql = "INSERT INTO transactions (date, category, amount, description) VALUES (?, ?, ?, ?) RETURNING id";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setDate(1, java.sql.Date.valueOf(transaction.getDate()));
            pstmt.setString(2, transaction.getCategory());
            pstmt.setBigDecimal(3, transaction.getAmount());
            pstmt.setString(4, transaction.getDescription());

            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public List<Transaction> getAllTransactions() {
        List<Transaction> transactions = new ArrayList<>();
        String sql = "SELECT * FROM transactions";

        try (Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Transaction transaction = new Transaction(
                        rs.getInt("id"),
                        rs.getDate("date").toLocalDate(),
                        rs.getString("category"),
                        rs.getBigDecimal("amount"),
                        rs.getString("description"));
                transactions.add(transaction);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return transactions;
    }

    public void updateTransaction(Transaction transaction) {
        String sql = "UPDATE transactions SET date = ?, category = ?, amount = ?, description = ? WHERE id = ?";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setDate(1, Date.valueOf(transaction.getDate()));
            pstmt.setString(2, transaction.getCategory());
            pstmt.setBigDecimal(3, transaction.getAmount());
            pstmt.setString(4, transaction.getDescription());
            pstmt.setInt(5, transaction.getId());

            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteTransaction(int id) {
        String sql = "DELETE FROM transactions WHERE id = ?";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            int affectedRows = pstmt.executeUpdate();

            if (affectedRows == 0) {
                System.out.println("Keine Transaktion mit dieser ID gefunden.");
            } else {
                System.out.println("Transaktion erfolgreich gelöscht.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Map<String, Double> getCategoryExpenses() {
        Map<String, Double> categoryExpenses = new HashMap<>();
        String sql = "SELECT category, SUM(amount) AS total FROM transactions GROUP BY category";

        try (Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                String category = rs.getString("category");
                double totalAmount = rs.getBigDecimal("total").doubleValue();
                categoryExpenses.put(category, totalAmount);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Sort the map by value in descending order
        return categoryExpenses.entrySet()
                .stream()
                .sorted((e1, e2) -> e2.getValue().compareTo(e1.getValue()))
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (e1, e2) -> e1,
                        LinkedHashMap::new
                ));
    }

    public Map<Integer, Double> getMonthlyExpenses() {
        Map<Integer, Double> monthlyExpenses = new HashMap<>();
        String sql = "SELECT EXTRACT(MONTH FROM date) AS month, SUM(amount) AS total_expense " +
                     "FROM transactions GROUP BY EXTRACT(MONTH FROM date) ORDER BY month";

        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                int month = rs.getInt("month");
                double totalExpense = rs.getDouble("total_expense");
                monthlyExpenses.put(month, Math.abs(totalExpense));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return monthlyExpenses;
    }

    // Close the database connection
    public void close() {
        try {
            if (conn != null && !conn.isClosed()) {
                conn.close();
                System.out.println("Verbindung zur Datenbank geschlossen.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
