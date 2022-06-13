package com.devivan.economicsupervision.Objects.Account.Should;

import java.util.ArrayList;

public class Should {
    private int id;
    private String to;
    private String date;
    public ArrayList<Double> payments;

    public Should() {
        payments.stream().mapToDouble(x -> x).filter(x -> x < 0).sum();
    }

    public Should(int id, String to, String date, ArrayList<Double> payments) {
        this.id = id;
        this.to = to;
        this.date = date;
        this.payments = payments;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public ArrayList<Double> getPayments() {
        return payments;
    }

    public void setPayments(ArrayList<Double> payments) {
        this.payments = payments;
    }
}
