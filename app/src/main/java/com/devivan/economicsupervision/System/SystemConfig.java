package com.devivan.economicsupervision.System;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.database.PropertyName;

import java.io.Serializable;

public class SystemConfig implements Serializable, Parcelable {

    // Parcelable implementation
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeByte((byte) (bak ? 1 : 0));
        dest.writeByte((byte) (bm ? 1 : 0));
        dest.writeLong(maxSize);
        dest.writeByte((byte) (on ? 1 : 0));
        dest.writeByte((byte) (shop ? 1 : 0));
        dest.writeInt(v);
    }

    protected SystemConfig(Parcel in) {
        bak = in.readByte() != 0;
        bm = in.readByte() != 0;
        maxSize = in.readLong();
        on = in.readByte() != 0;
        shop = in.readByte() != 0;
        v = in.readInt();
    }

    public static final Creator<SystemConfig> CREATOR = new Creator<SystemConfig>() {
        @Override
        public SystemConfig createFromParcel(Parcel in) {
            return new SystemConfig(in);
        }

        @Override
        public SystemConfig[] newArray(int size) {
            return new SystemConfig[size];
        }
    };
    //////////////////////////////////////////////////////////////////////////////////////

    @PropertyName("bak")
    private boolean bak;
    @PropertyName("bm")
    private boolean bm;
    @PropertyName("maxSize")
    private long maxSize;
    @PropertyName("on")
    private boolean on;
    @PropertyName("shop")
    private boolean shop;
    @PropertyName("v")
    private int v;

    public SystemConfig() {

    }

    public SystemConfig(boolean bak, boolean bm, long maxSize, boolean on, boolean shop, int v) {
        this.bak = bak;
        this.bm = bm;
        this.maxSize = maxSize;
        this.on = on;
        this.shop = shop;
        this.v = v;
    }

    public boolean isBak() {
        return bak;
    }

    public void setBak(boolean bak) {
        this.bak = bak;
    }

    public boolean isBm() {
        return bm;
    }

    public void setBm(boolean bm) {
        this.bm = bm;
    }

    public long getMaxSize() {
        return maxSize;
    }

    public void setMaxSize(long maxSize) {
        this.maxSize = maxSize;
    }

    public boolean isOn() {
        return on;
    }

    public void setOn(boolean on) {
        this.on = on;
    }

    public boolean isShop() {
        return shop;
    }

    public void setShop(boolean shop) {
        this.shop = shop;
    }

    public int getV() {
        return v;
    }

    public void setV(int v) {
        this.v = v;
    }
}