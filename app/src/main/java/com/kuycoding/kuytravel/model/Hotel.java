package com.kuycoding.kuytravel.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Hotel implements Parcelable {
    private String id, uid, nama, rating, image, keterangan, detailKamar, alamat, lat, lng, harga, checkBox_tv, checkBox_ac, checkBox_wifi, checkBox_kolam, checkBox_parkir, checkBox_resepsionis, checkBox_resto, checkBox_spa;
    private int view;

    public Hotel(String id, String uid, String nama, String rating, String image, String keterangan, String detailKamar, String alamat, String lat, String lng, String harga, String checkBox_tv, String checkBox_ac, String checkBox_wifi, String checkBox_kolam, String checkBox_parkir, String checkBox_resepsionis, String checkBox_resto, String checkBox_spa, int view) {
        this.id = id;
        this.uid = uid;
        this.nama = nama;
        this.rating = rating;
        this.image = image;
        this.keterangan = keterangan;
        this.detailKamar = detailKamar;
        this.alamat = alamat;
        this.lat = lat;
        this.lng = lng;
        this.harga = harga;
        this.checkBox_tv = checkBox_tv;
        this.checkBox_ac = checkBox_ac;
        this.checkBox_wifi = checkBox_wifi;
        this.checkBox_kolam = checkBox_kolam;
        this.checkBox_parkir = checkBox_parkir;
        this.checkBox_resepsionis = checkBox_resepsionis;
        this.checkBox_resto = checkBox_resto;
        this.checkBox_spa = checkBox_spa;
        this.view = view;
    }

    public Hotel() {
    }

    protected Hotel(Parcel in) {
        id = in.readString();
        uid = in.readString();
        nama = in.readString();
        rating = in.readString();
        image = in.readString();
        keterangan = in.readString();
        detailKamar = in.readString();
        alamat = in.readString();
        lat = in.readString();
        lng = in.readString();
        harga = in.readString();
        checkBox_tv = in.readString();
        checkBox_ac = in.readString();
        checkBox_wifi = in.readString();
        checkBox_kolam = in.readString();
        checkBox_parkir = in.readString();
        checkBox_resepsionis = in.readString();
        checkBox_resto = in.readString();
        checkBox_spa = in.readString();
    }

    public static final Creator<Hotel> CREATOR = new Creator<Hotel>() {
        @Override
        public Hotel createFromParcel(Parcel in) {
            return new Hotel(in);
        }

        @Override
        public Hotel[] newArray(int size) {
            return new Hotel[size];
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

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getKeterangan() {
        return keterangan;
    }

    public void setKeterangan(String keterangan) {
        this.keterangan = keterangan;
    }

    public String getDetailKamar() {
        return detailKamar;
    }

    public void setDetailKamar(String detailKamar) {
        this.detailKamar = detailKamar;
    }

    public String getAlamat() {
        return alamat;
    }

    public void setAlamat(String alamat) {
        this.alamat = alamat;
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

    public String getHarga() {
        return harga;
    }

    public void setHarga(String harga) {
        this.harga = harga;
    }

    public String getCheckBox_tv() {
        return checkBox_tv;
    }

    public void setCheckBox_tv(String checkBox_tv) {
        this.checkBox_tv = checkBox_tv;
    }

    public String getCheckBox_ac() {
        return checkBox_ac;
    }

    public void setCheckBox_ac(String checkBox_ac) {
        this.checkBox_ac = checkBox_ac;
    }

    public String getCheckBox_wifi() {
        return checkBox_wifi;
    }

    public void setCheckBox_wifi(String checkBox_wifi) {
        this.checkBox_wifi = checkBox_wifi;
    }

    public String getCheckBox_kolam() {
        return checkBox_kolam;
    }

    public void setCheckBox_kolam(String checkBox_kolam) {
        this.checkBox_kolam = checkBox_kolam;
    }

    public String getCheckBox_parkir() {
        return checkBox_parkir;
    }

    public void setCheckBox_parkir(String checkBox_parkir) {
        this.checkBox_parkir = checkBox_parkir;
    }

    public String getCheckBox_resepsionis() {
        return checkBox_resepsionis;
    }

    public void setCheckBox_resepsionis(String checkBox_resepsionis) {
        this.checkBox_resepsionis = checkBox_resepsionis;
    }

    public String getCheckBox_resto() {
        return checkBox_resto;
    }

    public void setCheckBox_resto(String checkBox_resto) {
        this.checkBox_resto = checkBox_resto;
    }

    public String getCheckBox_spa() {
        return checkBox_spa;
    }

    public void setCheckBox_spa(String checkBox_spa) {
        this.checkBox_spa = checkBox_spa;
    }

    public int getView() {
        return view;
    }

    public void setView(int view) {
        this.view = view;
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
        dest.writeString(rating);
        dest.writeString(image);
        dest.writeString(keterangan);
        dest.writeString(detailKamar);
        dest.writeString(alamat);
        dest.writeString(lat);
        dest.writeString(lng);
        dest.writeString(harga);
        dest.writeString(checkBox_tv);
        dest.writeString(checkBox_ac);
        dest.writeString(checkBox_wifi);
        dest.writeString(checkBox_kolam);
        dest.writeString(checkBox_parkir);
        dest.writeString(checkBox_resepsionis);
        dest.writeString(checkBox_resto);
        dest.writeString(checkBox_spa);
        dest.writeInt(view);
    }
}
