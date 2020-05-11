package com.example.userapp;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FeedFragment extends Fragment {

    private static final String TAG = "FeedFragment";

    RecyclerView recyclerView;
    FeedAdapter feedAdapter;
    FirebaseUser currentUser;
    List<Feed> mFeed;

    DatabaseReference databaseReference;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_feed, container, false);

        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        recyclerView = view.findViewById(R.id.feedRecyclerView);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());

        recyclerView.setLayoutManager(layoutManager);


        if(currentUser != null){

            mFeed = new ArrayList<>();

            readFeed();
            feedAdapter = new FeedAdapter(getContext(), mFeed);
            recyclerView.setAdapter(feedAdapter);


        }


        return view;


    }

    private void readFeed() {

        databaseReference = FirebaseDatabase.getInstance().getReference("Feeds");

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                mFeed.clear();

                if(dataSnapshot.exists()){

                    for(DataSnapshot snapshot : dataSnapshot.getChildren()){

                        HashMap<String, String> hashMap = (HashMap<String, String>) snapshot.getValue();

                        Log.i(TAG, "onDataChange: "+snapshot.getKey());

                        Feed feed = new Feed(hashMap.get("content"), hashMap.get("imageURL"), hashMap.get("senderID"),
                                hashMap.get("senderURL"), snapshot.getKey());

                        mFeed.add(feed);

                    }

                }

                feedAdapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

}
