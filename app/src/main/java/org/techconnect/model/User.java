package org.techconnect.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

/**
 * Created by Phani on 10/26/2016.
 */

public class User implements Parcelable, Cloneable {

    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };
    private String _id;
    private String email;
    private String name;
    private String countryCode;
    private String country;
    private String organization;
    private String pic;
    private List<String> expertises;
    private List<String> upCharts;
    private List<String> downCharts;

    public User() {
    }

    protected User(Parcel in) {
        _id = in.readString();
        email = in.readString();
        name = in.readString();
        countryCode = in.readString();
        country = in.readString();
        organization = in.readString();
        pic = in.readString();
        expertises = in.createStringArrayList();
        upCharts = in.createStringArrayList();
        downCharts = in.createStringArrayList();
}

    //Methods to determine if user likes a chart
    public boolean hasUpVoted(String id) {
        return upCharts.contains(id);
    }

    public boolean hasDownVoted(String id) {
        return downCharts.contains(id);
    }

    //Methods to up or downvote
    public boolean upVote(String id) {
        if (!upCharts.contains(id) && downCharts.contains(id)) {
            upCharts.add(id);
            downCharts.remove(id);
            return true;
        } else if (!upCharts.contains(id) && !downCharts.contains(id)) {
            upCharts.add(id);
            return true;
        } else if (upCharts.contains(id)) {
            upCharts.remove(id);
            return true;
        }
        return false;
    }

    public boolean downVote(String id) {
        if (upCharts.contains(id) && !downCharts.contains(id)) {
            upCharts.remove(id);
            downCharts.add(id);
            return true;
        } else if (!upCharts.contains(id) && !downCharts.contains(id)) {
            downCharts.add(id);
            return true;
        } else if (downCharts.contains(id)) {
            downCharts.remove(id);
            return true;
        }
        return false;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getOrganization() {
        return organization;
    }

    public void setOrganization(String organization) {
        this.organization = organization;
    }

    public List<String> getExpertises() {
        return expertises;
    }

    public void setExpertises(List<String> expertises) {
        this.expertises = expertises;
    }

    public String getPic() {
        return pic;
    }

    public void setPic(String pic) {
        this.pic = pic;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public User clone() throws CloneNotSupportedException {
        super.clone(); //Do I need this?
        User new_user = new User();
        new_user.set_id(this.get_id());
        new_user.setEmail(this.getEmail());
        new_user.setExpertises(this.getExpertises());
        new_user.setUpCharts(this.getUpCharts());
        new_user.setDownCharts(this.getDownCharts());
        new_user.setCountry(this.getCountry());
        new_user.setCountryCode(this.getCountryCode());
        new_user.setName(this.getName());
        new_user.setOrganization(this.getOrganization());

        return new_user;
    }


    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(_id);
        parcel.writeString(email);
        parcel.writeString(name);
        parcel.writeString(countryCode);
        parcel.writeString(country);
        parcel.writeString(organization);
        parcel.writeString(pic);
        parcel.writeStringList(expertises);
        parcel.writeStringList(upCharts);
        parcel.writeStringList(downCharts);
    }

    public List<String> getUpCharts() {
        return upCharts;
    }

    public void setUpCharts(List<String> upCharts) {
        this.upCharts = upCharts;
    }

    public List<String> getDownCharts() {
        return downCharts;
    }

    public void setDownCharts(List<String> downCharts) {
        this.downCharts = downCharts;
    }
}
