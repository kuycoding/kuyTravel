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

public class TravelHorizontalAdapter extends FirestoreRecyclerAdapter<Travel, TravelHorizontalAdapter.ViewHolder> {
    private static final String TAG = "HomePatientActivity";
    private TravelHorizontalAdapter.OnItemClickListener listener;
    private FirebaseFirestore fStore = FirebaseFirestore.getInstance();
    /**
     * Create a new RecyclerView adapter that listens to a Firestore Query.  See {@link
     * FirestoreRecyclerOptions} for configuration options.
     *
     * @param options
     */
    public TravelHorizontalAdapter(@NonNull FirestoreRecyclerOptions<Travel> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull TravelHorizontalAdapter.ViewHolder holder, int position, @NonNull Travel model) {
        holder.tvNama.setText(model.getNama());
        holder.tvAlamat.setText(model.getNama());
        holder.tvView.setText(String.valueOf(model.getView()));
        Picasso.get()
                .load(model.getImage())
                .fit()
                .centerCrop()
                .noFade()
                .error(R.drawable.placeholder)
                .placeholder(R.drawable.placeholder)
                .into(holder.img);
        String id = model.getId();
        fStore.collection("travel").document(id).collection("comment").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            int koment = Objects.requireNonNull(task.getResult()).size();
                            String com = String.valueOf(koment);
                            holder.tvComment.setText(com);
                        }
                    }
                });
    }

    @NonNull
    @Override
    public TravelHorizontalAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_travel_horizontal, parent, false);
        return new TravelHorizontalAdapter.ViewHolder(view);
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView tvNama, tvAlamat, tvJarak, tvView, tvComment;
        private ImageView img;

        public ViewHolder(@NonNull View itemView) {
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

        public void deleteItem(){
            listener.handleDeleteItem(getSnapshots().getSnapshot(getAdapterPosition()));
        }
    }

    public interface OnItemClickListener {
        void onItemClick(DocumentSnapshot documentSnapshot, int position);
        void handleDeleteItem(DocumentSnapshot snapshot);
    }
    public void setOnItemClickListener(TravelHorizontalAdapter.OnItemClickListener listener) {
        this.listener = listener;
    }
}
