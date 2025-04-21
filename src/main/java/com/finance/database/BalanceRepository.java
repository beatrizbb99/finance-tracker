package com.finance.database;

import java.math.BigDecimal;
import java.sql.*;
import com.finance.model.Balance;

public class BalanceRepository {

    private Connection conn;

    public BalanceRepository() {
        this.conn = DatabaseConnection.getConnection();
    }

    public Balance getBalance() {
        String sql = "SELECT * FROM balance LIMIT 1";  // just one entry

        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            if (rs.next()) {
                return new Balance(
                        rs.getInt("id"),
                        rs.getBigDecimal("balance")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null; // if no balance found
    }

    public void updateBalance(BigDecimal balance) {
        String sql = "UPDATE balance SET balance = ? WHERE id = 1"; 

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setBigDecimal(1, balance);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
