package com.finance.controller;

import com.finance.util.ViewSwitcher;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class SidebarController {

    @FXML
    private Label transactionsLabel;

    @FXML
    private Label analyticsLabel;

    private Label selectedLabel;

    private ViewSwitcher viewSwitcher;

    @FXML
    public void initialize() {
        setSelected(transactionsLabel);
    }

    public void setViewSwitcher(ViewSwitcher viewSwitcher) {
        this.viewSwitcher = viewSwitcher;
    }

    @FXML
    private void onTransactionsClicked() {
        if (viewSwitcher != null) {
            setSelected(transactionsLabel);
            viewSwitcher.switchTo("transactions");
        }
    }

    @FXML
    private void onAnalyticsClicked() {
        if (viewSwitcher != null) {
            setSelected(analyticsLabel);
            viewSwitcher.switchTo("analytics");
        }
    }

    private void setSelected(Label label) {
        if (selectedLabel != null) {
            selectedLabel.getStyleClass().remove("selected");
        }
        selectedLabel = label;
        if (!label.getStyleClass().contains("selected")) {
            label.getStyleClass().add("selected");
        }
    }
}
