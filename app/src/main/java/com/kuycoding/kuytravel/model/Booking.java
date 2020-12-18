package com.kuycoding.kuytravel.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Booking implements Parcelable {
    String id, uid, idHotel, postuid, jam, tgl, catatan, total, status, namaHotel, namaTraveler, image;
    String idTravel, namaTravel, dateCreate;

    public Booking() {
    }

    public Booking(String id, String uid, String idHotel, String postuid, String jam, String tgl, String catatan, String total, String status, String namaHotel, String namaTraveler, String image, String idTravel, String namaTravel, String dateCreate) {
        this.id = id;
        this.uid = uid;
        this.idHotel = idHotel;
        this.postuid = postuid;
        this.jam = jam;
        this.tgl = tgl;
        this.catatan = catatan;
        this.total = total;
        this.status = status;
        this.namaHotel = namaHotel;
        this.namaTraveler = namaTraveler;
        this.image = image;
        this.idTravel = idTravel;
        this.namaTravel = namaTravel;
        this.dateCreate = dateCreate;
    }

    protected Booking(Parcel in) {
        id = in.readString();
        uid = in.readString();
        idHotel = in.readString();
        postuid = in.readString();
        jam = in.readString();
        tgl = in.readString();
        catatan = in.readString();
        total = in.readString();
        status = in.readString();
        namaHotel = in.readString();
        namaTraveler = in.readString();
        image = in.readString();
        idTravel = in.readString();
        namaTravel = in.readString();
        dateCreate = in.readString();
    }

    public static final Creator<Booking> CREATOR = new Creator<Booking>() {
        @Override
        public Booking createFromParcel(Parcel in) {
            return new Booking(in);
        }

        @Override
        public Booking[] newArray(int size) {
            return new Booking[size];
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

    public String getIdHotel() {
        return idHotel;
    }

    public void setIdHotel(String idHotel) {
        this.idHotel = idHotel;
    }

    public String getPostuid() {
        return postuid;
    }

    public void setPostuid(String postuid) {
        this.postuid = postuid;
    }

    public String getJam() {
        return jam;
    }

    public void setJam(String jam) {
        this.jam = jam;
    }

    public String getTgl() {
        return tgl;
    }

    public void setTgl(String tgl) {
        this.tgl = tgl;
    }

    public String getCatatan() {
        return catatan;
    }

    public void setCatatan(String catatan) {
        this.catatan = catatan;
    }

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getNamaHotel() {
        return namaHotel;
    }

    public void setNamaHotel(String namaHotel) {
        this.namaHotel = namaHotel;
    }

    public String getNamaTraveler() {
        return namaTraveler;
    }

    public void setNamaTraveler(String namaTraveler) {
        this.namaTraveler = namaTraveler;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getIdTravel() {
        return idTravel;
    }

    public void setIdTravel(String idTravel) {
        this.idTravel = idTravel;
    }

    public String getNamaTravel() {
        return namaTravel;
    }

    public void setNamaTravel(String namaTravel) {
        this.namaTravel = namaTravel;
    }

    public String getDateCreate() {
        return dateCreate;
    }

    public void setDateCreate(String dateCreate) {
        this.dateCreate = dateCreate;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(uid);
        dest.writeString(idHotel);
        dest.writeString(postuid);
        dest.writeString(jam);
        dest.writeString(tgl);
        dest.writeString(catatan);
        dest.writeString(total);
        dest.writeString(status);
        dest.writeString(namaHotel);
        dest.writeString(namaTraveler);
        dest.writeString(image);
        dest.writeString(idTravel);
        dest.writeString(namaTravel);
        dest.writeString(dateCreate);
    }
}
