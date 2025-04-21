package com.finance.model;

import javafx.beans.property.*;

import java.math.BigDecimal;
import java.time.LocalDate;

public class Transaction {
    private final IntegerProperty id = new SimpleIntegerProperty();
    private final ObjectProperty<LocalDate> date = new SimpleObjectProperty<>();
    private final StringProperty category = new SimpleStringProperty();
    private final ObjectProperty<BigDecimal> amount = new SimpleObjectProperty<>();
    private final StringProperty description = new SimpleStringProperty();

    public Transaction(LocalDate date, String category, BigDecimal amount, String description) {
        this.date.set(date);
        this.category.set(category);
        this.amount.set(amount);
        this.description.set(description);
    }

    public Transaction(int id, LocalDate date, String category, BigDecimal amount, String description) {
        this.id.set(id);
        this.date.set(date);
        this.category.set(category);
        this.amount.set(amount);
        this.description.set(description);
    }

    public int getId() {
        return id.get();
    }

    public IntegerProperty idProperty() {
        return id;
    }

    public LocalDate getDate() {
        return date.get();
    }

    public ObjectProperty<LocalDate> dateProperty() {
        return date;
    }

    public String getCategory() {
        return category.get();
    }

    public StringProperty categoryProperty() {
        return category;
    }

    public BigDecimal getAmount() {
        return amount.get();
    }

    public ObjectProperty<BigDecimal> amountProperty() {
        return amount;
    }

    public String getDescription() {
        return description.get();
    }

    public StringProperty descriptionProperty() {
        return description;
    }

    public void setDate(LocalDate date) {
        this.date.set(date);
    }

    public void setCategory(String category) {
        this.category.set(category);
    }

    public void setAmount(BigDecimal amount) {
        this.amount.set(amount);
    }

    public void setDescription(String description) {
        this.description.set(description);
    }

    public void setId(int generatedId) {
        this.id.set(generatedId);
    }
}
