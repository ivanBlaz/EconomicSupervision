package com.devivan.economicsupervision.Objects.Account.Concepts;

import android.os.Parcel;
import android.os.Parcelable;

public class Concept implements Parcelable {

    // Parcelable implementation

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(name);
        dest.writeDouble(prof);
        dest.writeDouble(loss);
        dest.writeDouble(money);
    }

    protected Concept(Parcel in) {
        id = in.readInt();
        name = in.readString();
        prof = in.readDouble();
        loss = in.readDouble();
        money = in.readDouble();
    }

    public static final Creator<Concept> CREATOR = new Creator<Concept>() {
        @Override
        public Concept createFromParcel(Parcel in) {
            return new Concept(in);
        }

        @Override
        public Concept[] newArray(int size) {
            return new Concept[size];
        }
    };
    ////////////////////////////////////////////////////////////////////////////

    private int id;
    private String name;
    private double prof;
    private double loss;
    private double money;

    public Concept(int id, String name, double prof, double loss, double money) {
        this.id = id;
        this.name = name;
        this.prof = prof;
        this.loss = loss;
        this.money = money;
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
