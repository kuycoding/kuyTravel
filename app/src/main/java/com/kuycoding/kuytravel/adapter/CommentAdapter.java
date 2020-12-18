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
import com.kuycoding.kuytravel.R;
import com.kuycoding.kuytravel.model.Comment;
import com.squareup.picasso.Picasso;

public class CommentAdapter extends FirestoreRecyclerAdapter<Comment, CommentAdapter.ViewHolder> {
    private static final String TAG = "HomePatientActivity";
    private CommentAdapter.OnItemClickListener listener;
    /**
     * Create a new RecyclerView adapter that listens to a Firestore Query.  See {@link
     * FirestoreRecyclerOptions} for configuration options.
     *
     * @param options
     */
    public CommentAdapter(@NonNull FirestoreRecyclerOptions<Comment> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull CommentAdapter.ViewHolder holder, int position, @NonNull Comment model) {
        holder.tvNama.setText(model.getNama());
        holder.tvComment.setText(model.getComment());
        holder.tvTgl.setText(model.getTgl());
        Picasso.get()
                .load(model.getImgUrl())
                .placeholder(R.drawable.person_upload)
                .error(R.drawable.person_upload)
                .into(holder.imgUser);
    }

    @NonNull
    @Override
    public CommentAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_comment, parent, false);
        return new CommentAdapter.ViewHolder(view);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tvNama, tvTgl, tvComment;
        private ImageView imgUser;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNama = itemView.findViewById(R.id.txt_nama);
            tvTgl = itemView.findViewById(R.id.txt_tgl);
            tvComment = itemView.findViewById(R.id.txt_comment);
            imgUser = itemView.findViewById(R.id.imgUser);
        }
    }

    public class OnItemClickListener {
    }
}
