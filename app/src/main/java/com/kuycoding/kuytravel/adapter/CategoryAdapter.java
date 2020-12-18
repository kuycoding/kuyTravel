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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.kuycoding.kuytravel.R;
import com.kuycoding.kuytravel.model.Category;
import com.squareup.picasso.Picasso;

import java.util.Objects;

public class CategoryAdapter extends FirestoreRecyclerAdapter<Category, CategoryAdapter.ViewHolder> {
    private static final String TAG = "HomePatientActivity";
    private CategoryAdapter.OnItemClickListener listener;
    /**
     * Create a new RecyclerView adapter that listens to a Firestore Query.  See {@link
     * FirestoreRecyclerOptions} for configuration options.
     *
     * @param options
     */
    public CategoryAdapter(@NonNull FirestoreRecyclerOptions<Category> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull final CategoryAdapter.ViewHolder holder, int position, @NonNull final Category model) {
        FirebaseFirestore.getInstance().collection("category").document(model.getId()).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        holder.tvCategory.setText(Objects.requireNonNull(task.getResult()).getString("category"));
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });
        Picasso.get()
                .load(model.getImage())
                .placeholder(R.drawable.placeholder)
                .error(R.drawable.placeholder)
                .into(holder.imgCategory);
    }

    @NonNull
    @Override
    public CategoryAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_category, parent, false);
        return new CategoryAdapter.ViewHolder(view);
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView tvCategory;
        private ImageView imgCategory;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvCategory = itemView.findViewById(R.id.tvCategory);
            imgCategory = itemView.findViewById(R.id.imgCategory);
            itemView.setOnClickListener(this);
        }
        public void deleteItem(){
            listener.handleDeleteItem(getSnapshots().getSnapshot(getAdapterPosition()));
        }

        @Override
        public void onClick(View v) {

        }
    }

    public interface OnItemClickListener {
        void handleDeleteItem(DocumentSnapshot snapshot);
    }

    public void setOnItemClickListener(CategoryAdapter.OnItemClickListener listener) {
        this.listener = listener;
    }
}
