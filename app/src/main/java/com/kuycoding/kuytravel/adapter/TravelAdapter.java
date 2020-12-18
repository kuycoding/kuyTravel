package com.kuycoding.kuytravel.adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.kuycoding.kuytravel.R;
import com.kuycoding.kuytravel.model.Travel;
import com.squareup.picasso.Picasso;

import java.util.Objects;

public class TravelAdapter extends FirestoreRecyclerAdapter<Travel, TravelAdapter.ViewHolder> {
    private static final String TAG = "HomePatientActivity";
    private TravelAdapter.OnItemClickListener listener;

    /**
     * Create a new RecyclerView adapter that listens to a Firestore Query.  See {@link
     * FirestoreRecyclerOptions} for configuration options.
     *
     * @param options
     */
    public TravelAdapter(@NonNull FirestoreRecyclerOptions<Travel> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull final TravelAdapter.ViewHolder holder, int position, @NonNull Travel model) {
        holder.tvNama.setText(model.getNama());
        holder.tvAlamat.setText(model.getAlamat());
        holder.tvView.setText(String.valueOf(model.getView()));

        FirebaseFirestore fStore = FirebaseFirestore.getInstance();

        Picasso.get()
                .load(model.getImage())
                .error(R.drawable.placeholder)
                .placeholder(R.drawable.placeholder)
                .fit()
                .centerCrop()
                .noFade()
                .into(holder.img);

        fStore.collection("travel").document(model.getId()).collection("comment").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            int koment = Objects.requireNonNull(task.getResult()).size();
                            String com = String.valueOf(koment);
                            holder.tvComment.setText(com);
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "onFailure: total");
            }
        });
    }

    private double calculateDistance(double startLat, double startlng, double endlat, double endLng) {
        double earthRadius = 6371;
        double latDiff = Math.toRadians(startLat - endlat);
        double lngDiff = Math.toRadians(startlng - endLng);
        double a = Math.sin(latDiff / 2) * Math.sin(latDiff / 2) +
                Math.cos(Math.toRadians(startLat)) * Math.cos(Math.toRadians(endlat)) *
                        Math.sin(lngDiff / 2) * Math.sin(lngDiff / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distance = earthRadius * c;

        int meterConversion = 1609;

        return (distance * meterConversion / 1000);
    }

    @NonNull
    @Override
    public TravelAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_travel, parent, false);
        return new TravelAdapter.ViewHolder(view);
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView tvNama, tvAlamat, tvJarak, tvView, tvComment;
        private ImageView img;

        private Double distance;
        ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNama = itemView.findViewById(R.id.txt_nama);
            tvAlamat = itemView.findViewById(R.id.txt_alamat);
           // tvJarak = itemView.findViewById(R.id.txt_jarak);
            img = itemView.findViewById(R.id.imgTravel);
            tvView = itemView.findViewById(R.id.txt_view);
            tvComment = itemView.findViewById(R.id.txt_comment);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            if (position != RecyclerView.NO_POSITION && listener != null) {
                listener.onItemClick(getSnapshots().getSnapshot(position),position);
            }

        }
        public void deleteItem() {
            listener.handleDeleteItem(getSnapshots().getSnapshot(getAdapterPosition()));
        }
    }
    public interface OnItemClickListener {
        void onItemClick(DocumentSnapshot documentSnapshot, int position);
        void handleDeleteItem(DocumentSnapshot snapshot);
    }
    public void setOnItemClickListener(TravelAdapter.OnItemClickListener listener) {
        this.listener = listener;
    }
}
