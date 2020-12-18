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
import com.kuycoding.kuytravel.model.Category;
import com.squareup.picasso.Picasso;

public class CategoryCircleAdapter extends FirestoreRecyclerAdapter<Category, CategoryCircleAdapter.ViewHolder> {
    private OnClickListener listener;
    /**
     * Create a new RecyclerView adapter that listens to a Firestore Query.  See {@link
     * FirestoreRecyclerOptions} for configuration options.
     *
     * @param options
     */
    public CategoryCircleAdapter(@NonNull FirestoreRecyclerOptions<Category> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull CategoryCircleAdapter.ViewHolder holder, int position, @NonNull Category model) {
        holder.tvNama.setText(model.getCategory());
        Picasso.get()
                .load(model.getImage())
                .placeholder(R.drawable.ic_image_placeholder)
                .into(holder.imgCategory);

    }

    @NonNull
    @Override
    public CategoryCircleAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_category_circler, parent, false);
        return new CategoryCircleAdapter.ViewHolder(view);
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private ImageView imgCategory;
        private TextView tvNama;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgCategory = itemView.findViewById(R.id.imgCategory);
            tvNama = itemView.findViewById(R.id.txt_nama);

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
            listener.handlerDeleteItem(getSnapshots().getSnapshot(getAdapterPosition()));
        }
    }

    public interface OnClickListener {
        void onItemClick(DocumentSnapshot snapshot, int position);
        void handlerDeleteItem(DocumentSnapshot snapshot);
    }

    public void onItemClickListener(OnClickListener listener) {
        this.listener = listener;
    }
}
