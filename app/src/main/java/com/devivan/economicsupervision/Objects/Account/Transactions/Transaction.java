package com.devivan.economicsupervision.Objects.Account.Transactions;

public class Transaction {
    private int id;
    private String name;
    private String date;
    private double value;

    public Transaction() {

    }

    public Transaction(int id, String name, String date, double value) {
        this.id = id;
        this.name = name;
        this.date = date;
        this.value = value;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }
}
