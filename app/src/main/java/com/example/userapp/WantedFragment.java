package com.example.userapp;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;


public class WantedFragment extends Fragment {

    private static final String TAG = "WantedFragment";

    private RecyclerView recyclerView;
    private WantedAdapter wantedAdapter;
    private List<String> criminalsLists;



    private ImageView img_DOB,img_DOC;
    private EditText DOB,DOC;
    private int c=0;

    String imageUri;
    CircleImageView circleImageView;

    AutoCompleteTextView act,state,dist;
    Spinner spinner,query_type;
    private Button addCriminal;
    private TextView criminalAddress,crimeAddress,criminalName,bodyMark,crimeRating,crimeType,title;
    private ProgressDialog mProgressDialog;
    private RecyclerView criminalListRV;
//    private RecyclerView.Adapter<CriminalListAdapter.ViewHolder> mAdapter;
    private ArrayList<Criminals> shortlistedCriminals;
    private Context ctx;
    private DatabaseReference mRootRef,mCriminalRef;
    private int error = 0;
    private  String getImageUri="";
    private AlertDialog alertDialog;
    private ImageView search,cross,down,search2;
    private EditText searchEditext;
    private CardView sortCV,searchCV,noMatchesFoundCV;
    private ArrayAdapter<CharSequence> adapter;
    private String searchedWord="",searchType="";
    private RatingBar ratingBar;
    ArrayList<String> listState=new ArrayList<String>();
    // for listing all cities
    ArrayList<String> listCity=new ArrayList<String>();
    // access all auto complete text views




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_wanted, container, false);




//        DOB= view.findViewById(R.id.DOB);
//        img_DOB = view.findViewById(R.id.img_DOB);
//        mProgressDialog = new ProgressDialog(getContext());
//        criminalAddress = view.findViewById(R.id.address_of_criminal);
//        criminalName = view.findViewById(R.id.criminal_name);
//        bodyMark = view.findViewById(R.id.body_mark);
//        criminalListRV = view.findViewById(R.id.criminal_list_RV);
        shortlistedCriminals = new ArrayList<>();
        ctx = getActivity().getApplicationContext();
        mRootRef = FirebaseDatabase.getInstance().getReference();
        mCriminalRef = mRootRef.child("criminal_ref");
        search = view.findViewById(R.id.search);
        search2 = view.findViewById(R.id.search2);
        cross = view.findViewById(R.id.CROSS);
        title  = view.findViewById(R.id.title);
        down = view.findViewById(R.id.down);
        sortCV = view.findViewById(R.id.sort_cv);
        sortCV.setVisibility(View.GONE);
//        ratingBar = view.findViewById(R.id.rating_of_crime2);
        searchCV = view.findViewById(R.id.searchCV);
        searchCV.setVisibility(View.GONE);
        searchEditext = view.findViewById(R.id.search_editText);
        spinner = view.findViewById(R.id.spinner);
        noMatchesFoundCV = view.findViewById(R.id.noMatchesFound);
        act=view.findViewById(R.id.stateOrDistrict);
        noMatchesFoundCV.setVisibility(View.GONE);
        query_type = view.findViewById(R.id.query_type);
//        adapter = ArrayAdapter.createFromResource(this,R.array.QueryType,android.R.layout.simple_spinner_item);
//        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        query_type.setAdapter(adapter);
//        obj_list();



        recyclerView = view.findViewById(R.id.recyclerView);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
//        layoutManager.setReverseLayout(true);
//        layoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(layoutManager);

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if (currentUser != null) {
            criminalsLists = new ArrayList<>();
            wantedAdapter = new WantedAdapter(getContext(), criminalsLists);
            recyclerView.setAdapter(wantedAdapter);
            readCriminals();

        }


        return view;
    }

    private void readCriminals(){

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("criminal_ref");

        databaseReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

//                String string = dataSnapshot.getValue(String.class);

//                Log.i(TAG, "onChildAdded: "+string);

//                criminalsLists.add(string);

                Map<String, String>  map = (Map<String, String>) dataSnapshot.getValue();

                if(Float.valueOf(map.get("criminal_rating")) >= 2.5){

                    criminalsLists.add(dataSnapshot.getKey());

                }



                wantedAdapter.notifyItemInserted(criminalsLists.size()-1);


            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

                String string = dataSnapshot.getValue(String.class);

//                Log.i(TAG, "onChildRemoved: "+string);

                int i=0;
                for(i=0 ; i<criminalsLists.size() ; i++){

                    if(criminalsLists.get(i).equals(string)){

                        criminalsLists.remove(i);
                        break;

                    }
                }

//                Log.i(TAG, "onChildRemoved: "+i);
//
//                Log.i(TAG, "onChildRemoved: "+criminalsLists.size());

                wantedAdapter.notifyItemRemoved(i);

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }


}
