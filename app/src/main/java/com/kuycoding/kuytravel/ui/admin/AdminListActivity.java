package com.kuycoding.kuytravel.ui.admin;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.kuycoding.kuytravel.R;
import com.kuycoding.kuytravel.adapter.UsersAdapter;
import com.kuycoding.kuytravel.model.Users;

public class AdminListActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private UsersAdapter adapter;
    private FirebaseFirestore fStore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_list);
        fStore = FirebaseFirestore.getInstance();

        recyclerView = findViewById(R.id.rv_admin);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        Query query = fStore.collection("admin").orderBy("nama", Query.Direction.ASCENDING);
        FirestoreRecyclerOptions<Users> options = new FirestoreRecyclerOptions.Builder<Users>().setQuery(query, Users.class).build();

        adapter = new UsersAdapter(options);
        adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                recyclerView.scrollToPosition(adapter.getItemCount());
                //super.onItemRangeInserted(positionStart, itemCount);
            }
        });
        recyclerView.setAdapter(adapter);
        adapter.setOnItemClick(new UsersAdapter.OnItemClickListener() {
            @Override
            public void handleItemDelete(DocumentSnapshot documentSnapshot) {

            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    protected void onPause() {
        super.onPause();
        adapter.startListening();
    }
}
