package com.finance.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.finance.model.Income;

public class IncomeRepository {

    private Connection conn;

    public IncomeRepository() {
        this.conn = DatabaseConnection.getConnection();
    }

    public int insertIncome(Income income) {
        String sql = "INSERT INTO income (date, amount) VALUES (?, ?) RETURNING id";

         try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setDate(1, java.sql.Date.valueOf(income.getDate()));
            pstmt.setBigDecimal(2, income.getAmount());
    
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public List<Income> getAllIcomes() {
        List<Income> incomes = new ArrayList<>();
        String sql = "SELECT * FROM income";

        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Income income = new Income(
                        rs.getInt("id"),
                        rs.getDate("date").toLocalDate(),
                        rs.getBigDecimal("amount")
                );
                incomes.add(income);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return incomes;
    }

    public Map<Integer, Double> getMonthlyIncomes() {
        Map<Integer, Double> monthlyIncomes = new HashMap<>();
        String sql = "SELECT EXTRACT(MONTH FROM date) AS month, SUM(amount) AS total_income " +
                     "FROM income GROUP BY EXTRACT(MONTH FROM date) ORDER BY month";
    
        try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                int month = rs.getInt("month");
                double totalIncome = rs.getDouble("total_income");
    
                monthlyIncomes.put(month, totalIncome);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    
        return monthlyIncomes;
    }    
    
}
