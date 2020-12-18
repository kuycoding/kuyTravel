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
import com.google.firebase.firestore.DocumentSnapshot;
import com.kuycoding.kuytravel.R;
import com.kuycoding.kuytravel.model.Tiket;
import com.kuycoding.kuytravel.model.Travel;
import com.squareup.picasso.Picasso;

public class BoTiketAdapter extends FirestoreRecyclerAdapter<Tiket, BoTiketAdapter.ViewHolder> {
    private static final String TAG = "tag_adapter";
    private BoTiketAdapter.OnItemClickListener listener;
    /**
     * Create a new RecyclerView adapter that listens to a Firestore Query.  See {@link
     * FirestoreRecyclerOptions} for configuration options.
     *
     * @param options
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_booking, parent, false);g
     */
    public BoTiketAdapter(@NonNull FirestoreRecyclerOptions<Tiket> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull BoTiketAdapter.ViewHolder holder, int position, @NonNull Tiket model) {
        holder.tvNamaTravel.setText(model.getNamaTravel());
        holder.tvNama.setText(model.getNamaTraveler());
        holder.tvId.setText(model.getId());
        holder.tvTgl.setText(model.getTgl());
        holder.tvStatus.setText(model.getStatus());
        Picasso.get()
                .load(model.getImage())
                .placeholder(R.drawable.placeholder)
                .into(holder.imgTravel);
        Log.d(TAG, "onBindViewHolder: " + model.getNamaTravel());
    }

    @NonNull
    @Override
    public BoTiketAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_booking, parent, false);
        return new BoTiketAdapter.ViewHolder(view);
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView tvId, tvNama, tvNamaTravel, tvTgl, tvStatus;
        private ImageView imgTravel;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgTravel = itemView.findViewById(R.id.imgHotel);
            tvId = itemView.findViewById(R.id.tv_id);
            tvNama = itemView.findViewById(R.id.tv_nama);
            tvNamaTravel = itemView.findViewById(R.id.tv_nama_hotel);
            tvTgl = itemView.findViewById(R.id.tv_tanggal);
            tvStatus = itemView.findViewById(R.id.tv_status);
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
        void handleDeleteItem(DocumentSnapshot snapshot);

        void onItemClick(DocumentSnapshot snapshot, int position);
    }

    public void setOnItemClickListener(BoTiketAdapter.OnItemClickListener listener) {
        this.listener = listener;
    }
}
