package com.devivan.economicsupervision.Objects.Account.Categories;

import com.devivan.economicsupervision.Objects.Account.Categories.SubCategories.SubCategory;

import java.util.ArrayList;

public class Category {
    private String name;
    public double prof;
    public double loss;
    public double money;
    public ArrayList<SubCategory> subCategories;

    public Category(String name) {
        this.name = name;
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

    public ArrayList<SubCategory> getSubCategories() {
        return subCategories;
    }

    public void setSubCategories(ArrayList<SubCategory> subCategories) {
        this.subCategories = subCategories;
    }
}
