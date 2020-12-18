package com.kuycoding.kuytravel.adapter;

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
import com.kuycoding.kuytravel.model.Booking;
import com.squareup.picasso.Picasso;

public class BookingAdapter extends FirestoreRecyclerAdapter<Booking, BookingAdapter.ViewHolder> {
    private static final String TAG = "HomePatientActivity";
    private BookingAdapter.OnItemClickListener listener;
    /**
     * Create a new RecyclerView adapter that listens to a Firestore Query.  See {@link
     * FirestoreRecyclerOptions} for configuration options.
     *
     * @param options
     */
    public BookingAdapter(@NonNull FirestoreRecyclerOptions<Booking> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull BookingAdapter.ViewHolder holder, int position, @NonNull Booking model) {
        holder.tvId.setText(model.getId());
        holder.tvNama.setText(model.getNamaTraveler());

        if (model.getNamaHotel() == null) {
            holder.tvNamaTempat.setText(model.getNamaTravel());
        } else {
            holder.tvNamaTempat.setText(model.getNamaHotel());
        }

        holder.tvTgl.setText(model.getTgl());
        holder.tvJam.setText(model.getJam());
        holder.tvStatus.setText(model.getStatus());
        Picasso.get()
                .load(model.getImage())
                .placeholder(R.drawable.placeholder)
                .into(holder.imgHotel);
    }

    @NonNull
    @Override
    public BookingAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_booking, parent, false);
        return new  BookingAdapter.ViewHolder(view);
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView tvId, tvNama, tvNamaTempat, tvTgl, tvJam, tvStatus;
        private ImageView imgHotel;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgHotel = itemView.findViewById(R.id.imgHotel);
            tvId = itemView.findViewById(R.id.tv_id);
            tvNama = itemView.findViewById(R.id.tv_nama);
            tvNamaTempat = itemView.findViewById(R.id.tv_nama_hotel);
            tvTgl = itemView.findViewById(R.id.tv_tanggal);
            tvJam = itemView.findViewById(R.id.tv_jam);
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

    public void setOnItemClickListener(BookingAdapter.OnItemClickListener listener) {
        this.listener = listener;
    }
}
