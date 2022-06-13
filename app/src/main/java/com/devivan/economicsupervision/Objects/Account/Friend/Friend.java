package com.devivan.economicsupervision.Objects.Account.Friend;

import android.os.Parcel;
import android.os.Parcelable;

import com.devivan.economicsupervision.Objects.Account.Payment.Payment;

import java.util.ArrayList;

public class Friend implements Parcelable, Comparable {

    // Parcelable implementation
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(lookUpKey);
        dest.writeTypedList(payments);
    }

    protected Friend(Parcel in) {
        lookUpKey = in.readString();
        payments = in.createTypedArrayList(Payment.CREATOR);
    }

    public static final Creator<Friend> CREATOR = new Creator<Friend>() {
        @Override
        public Friend createFromParcel(Parcel in) {
            return new Friend(in);
        }

        @Override
        public Friend[] newArray(int size) {
            return new Friend[size];
        }
    };
    //////////////////////////////////////////////////////////////////////////

    private String lookUpKey;
    public ArrayList<Payment> payments;

    public Friend() {

    }

    public Friend(String lookUpKey, String date, int pays, double value) {
        this.lookUpKey = lookUpKey;
    }

    public String getLookUpKey() {
        return lookUpKey;
    }

    public void setLookUpKey(String lookUpKey) {
        this.lookUpKey = lookUpKey;
    }

    public ArrayList<Payment> getPayments() {
        return payments;
    }

    public void setPayments(ArrayList<Payment> payments) {
        this.payments = payments;
    }

    @Override
    public int compareTo(Object o) {
        Friend friend = (Friend) o;
        return friend.getLookUpKey().equals(this.lookUpKey) && friend.getPayments() == this.payments ? 0 : 1;
    }
}
