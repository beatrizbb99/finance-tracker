package com.finance.controller;

import com.finance.database.BalanceRepository;
import com.finance.database.TransactionRepository;
import com.finance.model.Transaction;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.math.BigDecimal;
import java.time.LocalDate;

public class TransactionFormController {

    @FXML
    private DatePicker datePicker;
    @FXML
    private SplitMenuButton categoryField;
    @FXML
    private TextField amountField;
    @FXML
    private TextField descriptionField;
    @FXML
    private Button saveButton;

    private TransactionRepository transactionRep;
    private BalanceRepository balanceRep;
    private Transaction transaction;

    private boolean isEditMode = false;

    private Runnable onSaveCallback;

    @FXML
    private void initialize() {
        String[] categories = {
                "Food", "Transportation", "Travel", "Rent", "Utilities",
                "Insurance", "Eating Out", "Entertainment", "Clothing",
                "Shopping", "Taxes", "Other"
        };

        for (String category : categories) {
            MenuItem item = new MenuItem(category);
            item.setOnAction(this::handleCategorySelection);
            categoryField.getItems().add(item);
        }

        if (!categoryField.getItems().isEmpty()) {
            categoryField.setText(categoryField.getItems().get(0).getText());
        }
    }

    public void setOnSaveCallback(Runnable onSaveCallback) {
        this.onSaveCallback = onSaveCallback;
    }

    public void setTransactionRepository(TransactionRepository transactionRep) {
        this.transactionRep = transactionRep;
    }

    public void setBalanceRepository(BalanceRepository balanceRep) {
        this.balanceRep = balanceRep;
    }

    public void setTransaction(Transaction transaction) {
        this.transaction = transaction;
        isEditMode = transaction != null;
        if (isEditMode) {
            saveButton.setText("Edit Expense");
            fillForm(transaction);
        }
    }

    private void fillForm(Transaction transaction) {
        datePicker.setValue(transaction.getDate());
        categoryField.setText(transaction.getCategory());
        amountField.setText(transaction.getAmount().toString());
        descriptionField.setText(transaction.getDescription());
    }

    private void clearForm() {
        datePicker.setValue(null);
        categoryField.setText(categoryField.getItems().get(0).getText());
        amountField.setText(null);
        descriptionField.setText(null);
    }

    @FXML
    private void handleCategorySelection(ActionEvent event) {
        MenuItem selected = (MenuItem) event.getSource();
        categoryField.setText(selected.getText());
    }

    public Transaction getTransaction() {
        return transaction;
    }

    @FXML
    private void onSave() {
        String errMsg = validateInput();
        if (!errMsg.isEmpty()) {
            showAlert(errMsg);
            return;
        }

        LocalDate date = datePicker.getValue();
        String category = categoryField.getText();
        BigDecimal amount = new BigDecimal(amountField.getText().replace(",", "."));
        String description = descriptionField.getText();

        if (isEditMode) {
            if (!transaction.getAmount().equals(amount)) {
                BigDecimal oldAmount = transaction.getAmount();
                BigDecimal newAmount = amount;
                BigDecimal balance = balanceRep.getBalance().getBalance();
                balance = balance.add(oldAmount).subtract(newAmount);
                balanceRep.updateBalance(balance);
                System.out.println(
                        "Expense changed from: " + oldAmount + " to " + newAmount + " and balance is: " + balance);
            }
            transaction.setDate(date);
            transaction.setCategory(category);
            transaction.setAmount(amount);
            transaction.setDescription(description);
            transactionRep.updateTransaction(transaction);
            ((Stage) datePicker.getScene().getWindow()).close();
        } else {
            transaction = new Transaction(date, category, amount, description);
            int id = transactionRep.insertTransaction(transaction);
            transaction.setId(id);
            clearForm();
            BigDecimal balance = balanceRep.getBalance().getBalance().subtract(amount);
            balanceRep.updateBalance(balance);
        }
        
        isEditMode = false;

        if (onSaveCallback != null) {
            onSaveCallback.run();
        }
    }

    public void onCancel() {
        isEditMode = false;
        transaction = null;
        ((DialogPane) datePicker.getScene().getRoot()).getScene().getWindow().hide();
    }

    private String validateInput() {
        if (datePicker.getValue() == null) {
            return "Please choose a date.";
        }

        if (datePicker.getValue().isAfter(java.time.LocalDate.now())) {
            return "The date cannot be in the future.";
        }

        if (categoryField.getText().trim().isEmpty()) {
            return "Please choose a category.";
        }

        if (amountField.getText().trim().isEmpty()) {
            return "Please enter a amount.";
        }

        try {
            new BigDecimal(amountField.getText().trim());
        } catch (NumberFormatException e) {
            return "Please enter a valid amount.";
        }

        return ""; // ok very good very nice
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
