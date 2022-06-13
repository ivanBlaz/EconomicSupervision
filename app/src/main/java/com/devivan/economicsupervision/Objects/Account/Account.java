package com.devivan.economicsupervision.Objects.Account;

import android.os.Parcel;
import android.os.Parcelable;

import com.devivan.economicsupervision.Objects.Account.Concepts.Concept;
import com.devivan.economicsupervision.Objects.Account.Friend.Friend;
import com.devivan.economicsupervision.Objects.Account.Group.Group;
import com.devivan.economicsupervision.Objects.Account.Movements.Movement;
import com.devivan.economicsupervision.Objects.Account.Categories.Category;

import java.util.ArrayList;

public class Account implements Parcelable {

    // Parcelable implementation

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(currency);
        dest.writeDouble(money);
        dest.writeDouble(monthBenefits);
        dest.writeDouble(monthExpenses);
        dest.writeDouble(should);
        dest.writeDouble(transact);
        dest.writeDouble(have);
    }

    protected Account(Parcel in) {
        id = in.readString();
        currency = in.readString();
        money = in.readDouble();
        monthBenefits = in.readDouble();
        monthExpenses = in.readDouble();
        should = in.readDouble();
        transact = in.readDouble();
        have = in.readDouble();
    }

    public static final Creator<Account> CREATOR = new Creator<Account>() {
        @Override
        public Account createFromParcel(Parcel in) {
            return new Account(in);
        }

        @Override
        public Account[] newArray(int size) {
            return new Account[size];
        }
    };
    ////////////////////////////////////////////////////////////////////////////

    private String id;
    private String currency;
    private double money;

    private double monthBenefits;
    private double monthExpenses;
    private double should;
    private double transact;
    private double have;

    public Account() {

    }

    public Account(String id, String currency, double money, double monthBenefits, double monthExpenses, double should, double transact, double have) {
        this.id = id;
        this.currency = currency;
        this.money = money;
        this.monthBenefits = monthBenefits;
        this.monthExpenses = monthExpenses;
        this.should = should;
        this.transact = transact;
        this.have = have;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public double getMoney() {
        return money;
    }

    public void setMoney(double money) {
        this.money = money;
    }

    public double getMonthBenefits() {
        return monthBenefits;
    }

    public void setMonthBenefits(double monthBenefits) {
        this.monthBenefits = monthBenefits;
    }

    public double getMonthExpenses() {
        return monthExpenses;
    }

    public void setMonthExpenses(double monthExpenses) {
        this.monthExpenses = monthExpenses;
    }

    public double getShould() {
        return should;
    }

    public void setShould(double should) {
        this.should = should;
    }

    public double getTransact() {
        return transact;
    }

    public void setTransact(double transact) {
        this.transact = transact;
    }

    public double getHave() {
        return have;
    }

    public void setHave(double have) {
        this.have = have;
    }
}
