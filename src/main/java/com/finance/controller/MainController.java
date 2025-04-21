package com.finance.controller;

import java.io.IOException;

import com.finance.database.BalanceRepository;
import com.finance.database.IncomeRepository;
import com.finance.database.TransactionRepository;
import com.finance.util.ViewSwitcher;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;

public class MainController implements ViewSwitcher {
    @FXML
    private AnchorPane contentArea;

    @FXML
    private AnchorPane sidebarArea;

    private final TransactionRepository transactionRep = new TransactionRepository();
    private final BalanceRepository balanceRepo = new BalanceRepository();
    private final IncomeRepository incomeRepo = new IncomeRepository();

    @FXML
    public void initialize() {
        loadSidebar();
        switchTo("transactions");
    }

    private void loadSidebar() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/finance/sidebar.fxml"));
            Pane sidebar = loader.load();

            SidebarController sidebarController = loader.getController();
            sidebarController.setViewSwitcher(this);

            sidebarArea.getChildren().setAll(sidebar);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void switchTo(String viewName) {
        System.out.println("Switch to: " + viewName);
        try {
            Pane view;
            FXMLLoader loader;
            if (viewName.equals("transactions")) {
                loader = new FXMLLoader(getClass().getResource("/com/finance/transaction_view.fxml"));
                view = loader.load();
                TransactionController transactionController = loader.getController();
                transactionController.setTransactionRepository(transactionRep);
                transactionController.setBalanceRepository(balanceRepo);
                transactionController.setIncomeRepository(incomeRepo);
                transactionController.initializeController();
            } else if (viewName.equals("analytics")) {
                loader = new FXMLLoader(getClass().getResource("/com/finance/analytics_view.fxml"));
                view = loader.load();
                AnalyticsController analyticsController = loader.getController();
                analyticsController.setTransactionRepository(transactionRep);
                analyticsController.setIncomeRepository(incomeRepo);
                analyticsController.initializeController();
            } else {
                return;
            }

            contentArea.getChildren().clear();
            contentArea.getChildren().setAll(view);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
