package com.devivan.economicsupervision.Objects.Account.Payment;

import android.os.Parcel;
import android.os.Parcelable;

public class Payment implements Parcelable, Comparable {

    // Parcelable implementation
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(lookUpKey);
        dest.writeInt(groupId);
        dest.writeString(date);
        dest.writeDouble(value);
        dest.writeBoolean(transaction);
    }
    protected Payment(Parcel in) {
        id = in.readInt();
        lookUpKey = in.readString();
        groupId = in.readInt();
        date = in.readString();
        value = in.readDouble();
        transaction = in.readBoolean();
    }

    public static final Creator<Payment> CREATOR = new Creator<Payment>() {
        @Override
        public Payment createFromParcel(Parcel in) {
            return new Payment(in);
        }

        @Override
        public Payment[] newArray(int size) {
            return new Payment[size];
        }
    };
    ////////////////////////////////////////////////////////////////////////////

    private int id;
    private String lookUpKey;
    private int groupId;
    private String date;
    private double value;
    private boolean transaction;

    public Payment() {

    }

    public Payment(int id, String lookUpKey, int groupId, String date, double value, boolean transaction) {
        this.id = id;
        this.lookUpKey = lookUpKey;
        this.groupId = groupId;
        this.date = date;
        this.value = value;
        this.transaction = transaction;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getLookUpKey() {
        return lookUpKey;
    }

    public void setLookUpKey(String lookUpKey) {
        this.lookUpKey = lookUpKey;
    }

    public int getGroupId() {
        return groupId;
    }

    public void setGroupId(int groupId) {
        this.groupId = groupId;
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

    public boolean isTransaction() {
        return transaction;
    }

    public void setTransaction(boolean transaction) {
        this.transaction = transaction;
    }

    @Override
    public int compareTo(Object o) {
        Payment payment = (Payment) o;
        return payment.getId() == this.getId() &&
                payment.getGroupId() == this.getGroupId() &&
                payment.getDate().equals(this.getDate()) &&
                payment.getValue() == this.getValue() &&
                payment.isTransaction() == this.isTransaction() ? 0 : 1;
    }
}
