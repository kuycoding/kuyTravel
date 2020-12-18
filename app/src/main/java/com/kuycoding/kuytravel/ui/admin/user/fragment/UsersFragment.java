package com.kuycoding.kuytravel.ui.admin.user.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.kuycoding.kuytravel.R;
import com.kuycoding.kuytravel.adapter.UsersAdapter;
import com.kuycoding.kuytravel.model.Users;
import com.kuycoding.kuytravel.ui.admin.user.AddUserActivity;

/**
 * A simple {@link Fragment} subclass.
 */
public class UsersFragment extends Fragment {
    private RecyclerView recyclerView;
    private UsersAdapter adapter;
    private FirebaseFirestore fStore;
    private FirebaseAuth fAuth;
    private FloatingActionButton fab;


    public UsersFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_users, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        fStore = FirebaseFirestore.getInstance();
        fAuth = FirebaseAuth.getInstance();

        fab = view.findViewById(R.id.fab_add_user);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), AddUserActivity.class));
            }
        });
        recyclerView = view.findViewById(R.id.rv_user);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);

        Query query = fStore.collection("usersTravel").orderBy("nama", Query.Direction.ASCENDING);
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
    public void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    public void onPause() {
        super.onPause();
        adapter.startListening();
    }
}
