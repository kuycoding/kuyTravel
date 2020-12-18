package com.kuycoding.kuytravel.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Users implements Parcelable {
    String id, nama, email, password, imgUrl, bio, role;

    public Users() {
    }

    public Users(String id, String nama, String email, String password, String imgUrl, String bio, String role) {
        this.id = id;
        this.nama = nama;
        this.email = email;
        this.password = password;
        this.imgUrl = imgUrl;
        this.bio = bio;
        this.role = role;
    }

    protected Users(Parcel in) {
        id = in.readString();
        nama = in.readString();
        email = in.readString();
        password = in.readString();
        imgUrl = in.readString();
        bio = in.readString();
        role = in.readString();
    }

    public static final Creator<Users> CREATOR = new Creator<Users>() {
        @Override
        public Users createFromParcel(Parcel in) {
            return new Users(in);
        }

        @Override
        public Users[] newArray(int size) {
            return new Users[size];
        }
    };

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(nama);
        dest.writeString(email);
        dest.writeString(password);
        dest.writeString(imgUrl);
        dest.writeString(bio);
        dest.writeString(role);
    }
}
