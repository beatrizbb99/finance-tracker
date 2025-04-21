package com.finance.controller;

import com.finance.database.BalanceRepository;
import com.finance.database.IncomeRepository;
import com.finance.database.TransactionRepository;
import com.finance.model.Income;
import com.finance.model.Transaction;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import org.kordamp.ikonli.javafx.FontIcon;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;

public class TransactionController {

    @FXML
    private TableView<Transaction> transactionTable;
    @FXML
    private TableColumn<Transaction, LocalDate> dateColumn;
    @FXML
    private TableColumn<Transaction, String> categoryColumn;
    @FXML
    private TableColumn<Transaction, BigDecimal> amountColumn;
    @FXML
    private TableColumn<Transaction, String> descriptionColumn;
    @FXML
    private TableColumn<Transaction, Void> actionColumn;

    @FXML
    private AnchorPane transactionFormInclude;

    @FXML
    private TextField incomeAmount;

    @FXML
    private DatePicker incomeDate;

    @FXML
    private Label balanceAmount;

    @FXML
    private TextField searchField;

    private TransactionRepository transactionRep;
    private BalanceRepository balanceRepo;
    private IncomeRepository incomeRepo;
    private ObservableList<Transaction> transactionList;

    public void initializeController() {
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));
        categoryColumn.setCellValueFactory(new PropertyValueFactory<>("category"));
        amountColumn.setCellValueFactory(new PropertyValueFactory<>("amount"));
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));

        setUpActionColumn();
        loadTransactions();
        loadTransactionForm();
        loadBalance();

        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            filterTransactions(newValue);
        });
    }

    public void setTransactionRepository(TransactionRepository transactionRep) {
        this.transactionRep = transactionRep;
    }

    public void setBalanceRepository(BalanceRepository balanceRepo) {
        this.balanceRepo = balanceRepo;
    }

    public void setIncomeRepository(IncomeRepository incomeRepo) {
        this.incomeRepo = incomeRepo;
    }

    private void setUpActionColumn() {
        actionColumn.setCellFactory(_ -> new TableCell<>() {
            private final FontIcon editIcon = new FontIcon("fa-edit");
            private final FontIcon deleteIcon = new FontIcon("fa-trash");
            private final Button editButton = new Button("", editIcon);
            private final Button deleteButton = new Button("", deleteIcon);

            {
                editIcon.setIconSize(16);
                editIcon.setIconColor(Color.web("#5A6ACF"));
                deleteIcon.setIconSize(16);
                deleteIcon.setIconColor(Color.web("#62657a"));

                editButton.setStyle("-fx-background-color: transparent;");
                deleteButton.setStyle("-fx-background-color: transparent;");

                editButton.setOnAction(_ -> {
                    Transaction transaction = getTableView().getItems().get(getIndex());
                    openTransactionFormDialog(transaction); // Edit popup
                });

                deleteButton.setOnAction(_ -> {
                    Transaction transaction = getTableView().getItems().get(getIndex());
                    deleteTransaction(transaction);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : new HBox(10, editButton, deleteButton));
            }
        });
    }

    private void loadBalance() {
        BigDecimal balance = balanceRepo.getBalance().getBalance();
        balanceAmount.setText(balance.toString());
    }

    private void addToBalance(BigDecimal amount) {
        BigDecimal balance = balanceRepo.getBalance().getBalance().add(amount);
        balanceRepo.updateBalance(balance);
        balanceAmount.setText(balance.toString());

    }

    private void loadTransactions() {
        transactionList = FXCollections.observableArrayList(transactionRep.getAllTransactions());
        FXCollections.reverse(transactionList);
        transactionTable.setItems(transactionList);
    }

    private TransactionFormController setupFormController(FXMLLoader loader) {
        TransactionFormController controller = loader.getController();
        controller.setTransactionRepository(transactionRep);
        controller.setBalanceRepository(balanceRepo);
        controller.setOnSaveCallback(() -> {
            loadTransactions();
            loadBalance();
        });
        return controller;
    }

    private void loadTransactionForm() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/finance/transactionForm.fxml"));
            Pane form = loader.load();
            setupFormController(loader);
            transactionFormInclude.getChildren().clear();
            transactionFormInclude.getChildren().add(form);
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Fehler", "Formular konnte nicht geladen werden.");
        }
    }

    private void openTransactionFormDialog(Transaction transactionToEdit) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/finance/transactionForm.fxml"));
            Pane formPane = loader.load();

            TransactionFormController controller = setupFormController(loader);

            if (transactionToEdit != null) {
                controller.setTransaction(transactionToEdit);
            }

            Dialog<Void> dialog = new Dialog<>();
            dialog.setTitle("Edit Expense");
            dialog.initModality(Modality.APPLICATION_MODAL);
            dialog.setOnCloseRequest(_ -> {
                controller.onCancel();
                dialog.close();
            });
            DialogPane dialogPane = dialog.getDialogPane();
            dialogPane.setContent(formPane);

            dialog.showAndWait();

        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Fehler", "Dialog konnte nicht geladen werden.");
        }
    }

    private void deleteTransaction(Transaction transaction) {
        if (transaction != null) {
            addToBalance(transaction.getAmount());
            transactionRep.deleteTransaction(transaction.getId());
            loadTransactions();
        }
    }

    private void filterTransactions(String keyword) {
        if (keyword == null || keyword.isEmpty()) {
            transactionTable.setItems(transactionList);
            return;
        }

        ObservableList<Transaction> filteredList = FXCollections.observableArrayList();
        for (Transaction t : transactionList) {
            if (t.getDescription() != null && t.getDescription().toLowerCase().contains(keyword.toLowerCase())) {
                filteredList.add(t);
            }
        }

        transactionTable.setItems(filteredList);
    }

    @FXML
    private void onAddIncome(ActionEvent event) {
        if (incomeDate.getValue() == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setHeaderText(null);
            alert.setContentText("Datum auswählen.");
            alert.showAndWait();
            return;
        }

        LocalDate date = incomeDate.getValue();
        BigDecimal amount = new BigDecimal(incomeAmount.getText().replace(",", "."));

        Income income = new Income(date, amount);
        int id = incomeRepo.insertIncome(income);

        if (id != -1) {
            addToBalance(amount);
            System.out.println("Einkommen erfolgreich hinzugefügt mit ID: " + id);
        } else {
            showAlert("Fehler", "Das Einkommen konnte nicht hinzugefügt werden.");
        }

        incomeAmount.setText(null);
        incomeDate.setValue(null);
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
