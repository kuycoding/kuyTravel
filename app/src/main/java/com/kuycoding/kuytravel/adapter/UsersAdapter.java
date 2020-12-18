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
import com.kuycoding.kuytravel.model.Users;
import com.squareup.picasso.Picasso;

public class UsersAdapter extends FirestoreRecyclerAdapter<Users, UsersAdapter.ViewHolder> {
    private UsersAdapter.OnItemClickListener listener;
    /**
     * Create a new RecyclerView adapter that listens to a Firestore Query.  See {@link
     * FirestoreRecyclerOptions} for configuration options.
     *
     * @param options
     */
    public UsersAdapter(@NonNull FirestoreRecyclerOptions<Users> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull UsersAdapter.ViewHolder holder, int position, @NonNull Users model) {
        holder.tvNama.setText(model.getNama());
        holder.tvEmail.setText(model.getEmail());
        holder.tvBio.setText(model.getBio());
        Picasso.get()
                .load(model.getImgUrl())
                .placeholder(R.drawable.placeholder)
                .error(R.drawable.placeholder)
                .into(holder.imgProfile);
    }

    @NonNull
    @Override
    public UsersAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_users, parent, false);
        return new ViewHolder(view);
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView tvNama, tvEmail, tvBio;
        private ImageView imgProfile;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNama = itemView.findViewById(R.id.txt_nama);
            tvEmail = itemView.findViewById(R.id.txt_email);
            tvBio = itemView.findViewById(R.id.txt_bio);
            imgProfile = itemView.findViewById(R.id.imgProfile);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
        }

        public void itemDelete(){
            listener.handleItemDelete(getSnapshots().getSnapshot(getAdapterPosition()));
        }
    }

    public interface OnItemClickListener {
        void handleItemDelete(DocumentSnapshot documentSnapshot);
    }

    public void setOnItemClick(UsersAdapter.OnItemClickListener listener) {
        this.listener = listener;
    }
}
