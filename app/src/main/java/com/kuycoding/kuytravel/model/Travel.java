package com.kuycoding.kuytravel.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Travel implements Parcelable {
    private String id, uid, nama, keterangan, harga, lat, lng, alamat, category, jarak, image;
    private int view, like;

    public Travel() {
    }

    public Travel(String id, String uid, String nama, String keterangan, String harga, String lat, String lng, String alamat, String category, String jarak, String image, int view, int like) {
        this.id = id;
        this.uid = uid;
        this.nama = nama;
        this.keterangan = keterangan;
        this.harga = harga;
        this.lat = lat;
        this.lng = lng;
        this.alamat = alamat;
        this.category = category;
        this.jarak = jarak;
        this.image = image;
        this.view = view;
        this.like = like;
    }

    protected Travel(Parcel in) {
        id = in.readString();
        uid = in.readString();
        nama = in.readString();
        keterangan = in.readString();
        harga = in.readString();
        lat = in.readString();
        lng = in.readString();
        alamat = in.readString();
        category = in.readString();
        jarak = in.readString();
        image = in.readString();
        view = in.readInt();
        like = in.readInt();
    }

    public static final Creator<Travel> CREATOR = new Creator<Travel>() {
        @Override
        public Travel createFromParcel(Parcel in) {
            return new Travel(in);
        }

        @Override
        public Travel[] newArray(int size) {
            return new Travel[size];
        }
    };

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public String getKeterangan() {
        return keterangan;
    }

    public void setKeterangan(String keterangan) {
        this.keterangan = keterangan;
    }

    public String getHarga() {
        return harga;
    }

    public void setHarga(String harga) {
        this.harga = harga;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLng() {
        return lng;
    }

    public void setLng(String lng) {
        this.lng = lng;
    }

    public String getAlamat() {
        return alamat;
    }

    public void setAlamat(String alamat) {
        this.alamat = alamat;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getJarak() {
        return jarak;
    }

    public void setJarak(String jarak) {
        this.jarak = jarak;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public int getView() {
        return view;
    }

    public void setView(int view) {
        this.view = view;
    }

    public int getLike() {
        return like;
    }

    public void setLike(int like) {
        this.like = like;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(uid);
        dest.writeString(nama);
        dest.writeString(keterangan);
        dest.writeString(harga);
        dest.writeString(lat);
        dest.writeString(lng);
        dest.writeString(alamat);
        dest.writeString(category);
        dest.writeString(jarak);
        dest.writeString(image);
        dest.writeInt(view);
        dest.writeInt(like);
    }
}
