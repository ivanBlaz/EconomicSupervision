package com.devivan.economicsupervision.Objects.Device.DeviceAccounts;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.database.PropertyName;

import java.io.Serializable;
import java.util.ArrayList;

public class DeviceAccount implements Serializable, Parcelable {

    // Parcelable implementation
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeStringList(auxR);
        dest.writeString(id);
        dest.writeInt(pos);
        dest.writeStringList(r);
    }

    protected DeviceAccount(Parcel in) {
        auxR = in.createStringArrayList();
        id = in.readString();
        pos = in.readInt();
        r = in.createStringArrayList();
    }

    public static final Creator<DeviceAccount> CREATOR = new Creator<DeviceAccount>() {
        @Override
        public DeviceAccount createFromParcel(Parcel in) {
            return new DeviceAccount(in);
        }

        @Override
        public DeviceAccount[] newArray(int size) {
            return new DeviceAccount[size];
        }
    };
    ////////////////////////////////////////////////////////////////////////////////////////

    @PropertyName("auxR")
    public ArrayList<String> auxR;
    @PropertyName("id")
    private String id;
    @PropertyName("pos")
    private int pos;
    @PropertyName("r")
    public ArrayList<String> r;

    public DeviceAccount(String id, int pos) {
        this.id = id;
        this.pos = pos;
    }

    public DeviceAccount(ArrayList<String> auxR, String id, int pos, ArrayList<String> r) {
        this.auxR = auxR;
        this.id = id;
        this.pos = pos;
        this.r = r;
    }

    public ArrayList<String> getAuxR() {
        return auxR;
    }

    public void setAuxR(ArrayList<String> auxR) {
        this.auxR = auxR;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getPos() {
        return pos;
    }

    public void setPos(int pos) {
        this.pos = pos;
    }

    public ArrayList<String> getR() {
        return r;
    }

    public void setR(ArrayList<String> r) {
        this.r = r;
    }
}
