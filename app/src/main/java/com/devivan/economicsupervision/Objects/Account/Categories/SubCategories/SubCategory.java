package com.devivan.economicsupervision.Objects.Account.Categories.SubCategories;

public class SubCategory {
    private String name;
    public double prof;
    public double loss;
    public double money;

    public SubCategory(String name) {
        this.name = name;
    }

    public SubCategory(String name, double prof, double loss, double money) {
        this.name = name;
        this.prof = prof;
        this.loss = loss;
        this.money = money;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getProf() {
        return prof;
    }

    public void setProf(double prof) {
        this.prof = prof;
    }

    public double getLoss() {
        return loss;
    }

    public void setLoss(double loss) {
        this.loss = loss;
    }

    public double getMoney() {
        return money;
    }

    public void setMoney(double money) {
        this.money = money;
    }
}
