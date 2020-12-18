package com.kuycoding.kuytravel.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Tiket implements Parcelable {
    String id, uid, idTravel, postuid, namaTravel, namaTraveler, image, tgl, dateCreate, catatan, status, total;

    public Tiket(String id, String uid, String idTravel, String postuid, String namaTravel, String namaTraveler, String image, String tgl, String dateCreate, String catatan, String status, String total) {
        this.id = id;
        this.uid = uid;
        this.idTravel = idTravel;
        this.postuid = postuid;
        this.namaTravel = namaTravel;
        this.namaTraveler = namaTraveler;
        this.image = image;
        this.tgl = tgl;
        this.dateCreate = dateCreate;
        this.catatan = catatan;
        this.status = status;
        this.total = total;
    }

    protected Tiket(Parcel in) {
        id = in.readString();
        uid = in.readString();
        idTravel = in.readString();
        postuid = in.readString();
        namaTravel = in.readString();
        namaTraveler = in.readString();
        image = in.readString();
        tgl = in.readString();
        dateCreate = in.readString();
        catatan = in.readString();
        status = in.readString();
        total = in.readString();
    }

    public static final Creator<Tiket> CREATOR = new Creator<Tiket>() {
        @Override
        public Tiket createFromParcel(Parcel in) {
            return new Tiket(in);
        }

        @Override
        public Tiket[] newArray(int size) {
            return new Tiket[size];
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

    public String getIdTravel() {
        return idTravel;
    }

    public void setIdTravel(String idTravel) {
        this.idTravel = idTravel;
    }

    public String getPostuid() {
        return postuid;
    }

    public void setPostuid(String postuid) {
        this.postuid = postuid;
    }

    public String getNamaTravel() {
        return namaTravel;
    }

    public void setNamaTravel(String namaTravel) {
        this.namaTravel = namaTravel;
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

    public String getTgl() {
        return tgl;
    }

    public void setTgl(String tgl) {
        this.tgl = tgl;
    }

    public String getDateCreate() {
        return dateCreate;
    }

    public void setDateCreate(String dateCreate) {
        this.dateCreate = dateCreate;
    }

    public String getCatatan() {
        return catatan;
    }

    public void setCatatan(String catatan) {
        this.catatan = catatan;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(uid);
        dest.writeString(idTravel);
        dest.writeString(postuid);
        dest.writeString(namaTravel);
        dest.writeString(namaTraveler);
        dest.writeString(image);
        dest.writeString(tgl);
        dest.writeString(dateCreate);
        dest.writeString(catatan);
        dest.writeString(status);
        dest.writeString(total);
    }
}
