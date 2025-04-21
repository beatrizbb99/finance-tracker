package com.finance.model;

import java.math.BigDecimal;
import java.time.LocalDate;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;

public class Income {
    private final IntegerProperty id = new SimpleIntegerProperty();
    private final ObjectProperty<LocalDate> date = new SimpleObjectProperty<>();
    private final ObjectProperty<BigDecimal> amount = new SimpleObjectProperty<>();

    public Income(LocalDate date, BigDecimal amount) {
        this.date.set(date);
        this.amount.set(amount);
    }

    public Income(int id, LocalDate date, BigDecimal amount) {
        this.id.set(id);
        this.date.set(date);
        this.amount.set(amount);
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

    public BigDecimal getAmount() {
        return amount.get();
    }

    public ObjectProperty<BigDecimal> amountProperty() {
        return amount;
    }

    public void setDate(LocalDate date) {
        this.date.set(date);
    }

    public void setAmount(BigDecimal amount) {
        this.amount.set(amount);
    }

    public void setId(int generatedId) {
        this.id.set(generatedId);
    }
}
