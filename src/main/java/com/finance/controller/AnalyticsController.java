package com.finance.controller;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

import java.util.List;
import java.util.Map;

import com.finance.database.IncomeRepository;
import com.finance.database.TransactionRepository;

public class AnalyticsController {

    @FXML
    private PieChart pieChart;

    @FXML
    private VBox totalExpenses;

    @FXML
    private BarChart<String, Number> barChart;
    
    @FXML
    private CategoryAxis xAxis;

    @FXML
    private NumberAxis yAxis;

    private TransactionRepository transactionRep;

    private IncomeRepository incomeRep;

    public void initializeController() {
        Map<String, Double> categoryExpenses = transactionRep.getCategoryExpenses();
        loadChart(categoryExpenses);
        loadStackedBarChart();
    }

    public void setTransactionRepository(TransactionRepository transactionRep) {
        this.transactionRep = transactionRep;
    }

    public void setIncomeRepository(IncomeRepository incomeRep) {
        this.incomeRep = incomeRep;
    }

    private void loadChart(Map<String, Double> categoryExpenses) {
        pieChart.getData().clear();
        totalExpenses.getChildren().clear();

        double total = categoryExpenses.values().stream().mapToDouble(Double::doubleValue).sum();

        // Farben definieren
        List<String> colors = List.of(
                "#f3622d", "#fba71b", "#57b757", "#41a9c9",
                "#4258c9", "#9a42c8", "#c84164", "#888888",
                "#1abc9c", "#e67e22", "#3498db", "#9b59b6"
        );

        for (Map.Entry<String, Double> entry : categoryExpenses.entrySet()) {
            String category = entry.getKey();
            double amount = entry.getValue();

            PieChart.Data slice = new PieChart.Data(category, amount);
            pieChart.getData().add(slice);
        }

        pieChart.applyCss();
        pieChart.layout();
        pieChart.setLegendVisible(false);
        pieChart.setLabelsVisible(false);

        int i = 0;
        for (PieChart.Data slice : pieChart.getData()) {
            String hexColor = colors.get(i % colors.size());
            Node node = slice.getNode();
            node.setStyle("-fx-pie-color: " + hexColor + ";");

            String category = slice.getName();
            double amount = slice.getPieValue();
            double percent = (amount / total) * 100;

            createLegendItem(hexColor, category, amount, percent);
            i++;
        }
    }

    private void createLegendItem(String hexColor, String category, double amount, double percent) {
        Circle colorCircle = new Circle(6, Color.web(hexColor));

        Label label = new Label(String.format("%s: %.2f€ (%.1f%%)", category, amount, percent));
        label.setStyle("-fx-text-fill: #dcdedf; -fx-font-size: 13;");

        HBox legendItem = new HBox(10, colorCircle, label);
        totalExpenses.getChildren().add(legendItem);
    }

    private void loadStackedBarChart() {
        barChart.getData().clear();
        
        XYChart.Series<String, Number> incomeSeries = new XYChart.Series<>();
        incomeSeries.setName("Income");

        XYChart.Series<String, Number> expenseSeries = new XYChart.Series<>();
        expenseSeries.setName("Expenses");

        Map<Integer, Double> monthlyIncomes = incomeRep.getMonthlyIncomes();
        Map<Integer, Double> monthlyExpenses = transactionRep.getMonthlyExpenses();

        for (int month = 1; month <= 12; month++) {
            double income = monthlyIncomes.getOrDefault(month, 0.0);
            double expense = monthlyExpenses.getOrDefault(month, 0.0);

            incomeSeries.getData().add(new XYChart.Data<>(String.valueOf(month), income));
            expenseSeries.getData().add(new XYChart.Data<>(String.valueOf(month), expense));
        }

        barChart.getData().add(incomeSeries);
        barChart.getData().add(expenseSeries);
    }
}
