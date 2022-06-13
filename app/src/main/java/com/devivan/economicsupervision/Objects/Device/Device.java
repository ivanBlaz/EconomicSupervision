package com.devivan.economicsupervision.Objects.Device;


import android.os.Parcel;
import android.os.Parcelable;

import com.devivan.economicsupervision.Objects.Device.DeviceAccounts.DeviceAccount;
import com.google.firebase.database.PropertyName;

import java.io.Serializable;
import java.util.ArrayList;

public class Device implements Serializable, Parcelable {

    // Parcelable implementation
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedList(accounts);
        dest.writeInt(autR);
        dest.writeString(country);
        dest.writeByte((byte) (password ? 1 : 0));
        dest.writeString(phone);
        dest.writeByte((byte) (premium ? 1 : 0));
    }

    protected Device(Parcel in) {
        accounts = in.createTypedArrayList(DeviceAccount.CREATOR);
        autR = in.readInt();
        country = in.readString();
        password = in.readByte() != 0;
        phone = in.readString();
        premium = in.readByte() != 0;
    }

    public static final Creator<Device> CREATOR = new Creator<Device>() {
        @Override
        public Device createFromParcel(Parcel in) {
            return new Device(in);
        }

        @Override
        public Device[] newArray(int size) {
            return new Device[size];
        }
    };
    //////////////////////////////////////////////////////////////////////////

    // From FirebaseDatabase
    @PropertyName("accounts")
    public ArrayList<DeviceAccount> accounts;
    @PropertyName("autR")
    private int autR;
    @PropertyName("country")
    private String country;
    @PropertyName("password")
    private boolean password;
    @PropertyName("phone")
    private String phone;
    @PropertyName("premium")
    private boolean premium;
    ////////////////////////

    public Device() {

    }

    public ArrayList<DeviceAccount> getAccounts() {
        return accounts;
    }

    public void setAccounts(ArrayList<DeviceAccount> accounts) {
        this.accounts = accounts;
    }

    public int getAutR() {
        return autR;
    }

    public void setAutR(int autR) {
        this.autR = autR;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public boolean isPassword() {
        return password;
    }

    public void setPassword(boolean password) {
        this.password = password;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public boolean isPremium() {
        return premium;
    }

    public void setPremium(boolean premium) {
        this.premium = premium;
    }


    @Override
    public String toString() {
        return "Device{" +
                "accounts=" + accounts +
                ", autR=" + autR +
                ", country='" + country + '\'' +
                ", password=" + password +
                ", phone='" + phone + '\'' +
                ", premium=" + premium +
                '}';
    }
}
