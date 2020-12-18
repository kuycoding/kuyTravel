package com.kuycoding.kuytravel.model;

public class Comment {
    private String id, idUser, nama, comment, tgl, imgUrl;

    public Comment() {
    }

    public Comment(String id, String idUser, String nama, String comment, String tgl, String imgUrl) {
        this.id = id;
        this.idUser = idUser;
        this.nama = nama;
        this.comment = comment;
        this.tgl = tgl;
        this.imgUrl = imgUrl;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getIdUser() {
        return idUser;
    }

    public void setIdUser(String idUser) {
        this.idUser = idUser;
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getTgl() {
        return tgl;
    }

    public void setTgl(String tgl) {
        this.tgl = tgl;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }
}
