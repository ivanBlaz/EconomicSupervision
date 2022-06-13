package com.devivan.economicsupervision.Objects.Account.Group;

import android.os.Parcel;
import android.os.Parcelable;

import com.devivan.economicsupervision.Objects.Account.Friend.Friend;

import java.util.ArrayList;

public class Group implements Parcelable, Comparable {

    // Parcelable implementation
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(name);
        dest.writeString(date);
        dest.writeDouble(value);
        dest.writeDouble(paid);
        dest.writeDouble(total);
        dest.writeTypedList(friends);
    }

    protected Group(Parcel in) {
        id = in.readInt();
        name = in.readString();
        date = in.readString();
        value = in.readDouble();
        paid = in.readDouble();
        total = in.readDouble();
        friends = in.createTypedArrayList(Friend.CREATOR);
    }

    public static final Creator<Group> CREATOR = new Creator<Group>() {
        @Override
        public Group createFromParcel(Parcel in) {
            return new Group(in);
        }

        @Override
        public Group[] newArray(int size) {
            return new Group[size];
        }
    };
    ////////////////////////////////////////////////////////////////////////

    private int id;
    private String name;
    private String date;
    private double value;
    private double paid;
    private double total;
    public ArrayList<Friend> friends;

    public Group() {

    }


    public Group(int id, String name, String date, double value, double paid, double total) {
        this.id = id;
        this.name = name;
        this.date = date;
        this.value = value;
        this.paid = paid;
        this.total = total;
    }

    @Override
    public String toString() {
        return "Group{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", date='" + date + '\'' +
                ", value=" + value +
                ", paid=" + paid +
                ", total=" + total +
                '}';
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

    public double getPaid() {
        return paid;
    }

    public void setPaid(double paid) {
        this.paid = paid;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public ArrayList<Friend> getFriends() {
        return friends;
    }

    public void setFriends(ArrayList<Friend> friends) {
        this.friends = friends;
    }

    @Override
    public int compareTo(Object o) {
        Group group = (Group) o;
        return group.getId() == this.id && group.getName().equals(this.name) && group.getDate().equals(this.date) && group.getValue() == this.value && group.getPaid() == this.paid && group.getTotal() == this.total && group.getFriends() == this.friends ? 0 : 1;
    }
}
