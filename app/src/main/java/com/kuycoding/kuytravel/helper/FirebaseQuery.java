package com.kuycoding.kuytravel.helper;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class FirebaseQuery {
    public static CollectionReference favoriteRef = FirebaseFirestore.getInstance().collection("Favorite");

    private Query query = null;
    //ambil refrensi key

}
