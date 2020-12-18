package com.kuycoding.kuytravel.adapter;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.DocumentSnapshot;
import com.kuycoding.kuytravel.R;
import com.kuycoding.kuytravel.model.Hotel;
import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;

public class HotelAdapter extends FirestoreRecyclerAdapter<Hotel, HotelAdapter.ViewHolder> {
    private HotelAdapter.OnItemClick listener;
    private DecimalFormat decimalFormat = new DecimalFormat("#,###,###");

    /**
     * Create a new RecyclerView adapter that listens to a Firestore Query.  See {@link
     * FirestoreRecyclerOptions} for configuration options.
     *
     * @param options
     */
    public HotelAdapter(@NonNull FirestoreRecyclerOptions<Hotel> options) {
        super(options);
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void onBindViewHolder(@NonNull HotelAdapter.ViewHolder holder, int position, @NonNull Hotel model) {
        holder.tvNama.setText(model.getNama());
        holder.tvHarga.setText("IDR " + decimalFormat.format(Integer.parseInt(model.getHarga())));
        holder.tvAlamat.setText(model.getAlamat());
        holder.ratingBar.setRating(Float.parseFloat(model.getRating()));
        Picasso.get()
                .load(model.getImage())
                .into(holder.imgHotel);
    }

    @NonNull
    @Override
    public HotelAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_hotel, parent, false);
        return new ViewHolder(view);
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private ImageView imgHotel;
        private TextView tvNama, tvAlamat, tvHarga;
        private RatingBar ratingBar;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgHotel = itemView.findViewById(R.id.imgHotel);
            tvNama = itemView.findViewById(R.id.txt_nama);
            tvAlamat = itemView.findViewById(R.id.txt_alamat);
            tvHarga = itemView.findViewById(R.id.txt_harga);
            ratingBar = itemView.findViewById(R.id.rateHotel);

            itemView.setOnClickListener(this);
        }
        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            if (position != RecyclerView.NO_POSITION && listener != null) {
                listener.onItemClick(getSnapshots().getSnapshot(position), position);
            }
        }
        public void deleteItem() {
            listener.handlerDeleteItem(getSnapshots().getSnapshot(getAdapterPosition()));
        }

    }
    public interface OnItemClick {
        void onItemClick(DocumentSnapshot documentSnapshot, int position);
        void handlerDeleteItem(DocumentSnapshot snapshot);
    }
    public void setOnItemClickListener(HotelAdapter.OnItemClick listener) {
        this.listener = listener;
    }
}
