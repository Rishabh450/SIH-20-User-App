package com.example.userapp;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AppointmentFragment extends Fragment {

    private static final String TAG = "AppointmentFragment";

    FirebaseUser currentUser;

    RecyclerView recyclerView;
    Spinner typeSpinner, categorySpinner;

    String[] type = {"All", "Pending", "Accepted", "Approved", "Rejected"};
    String[] category = {"FIRs", "NOC"};

    String txt_category = "FIRs";

    List<String> appointmentsList;

    AppointmentsAdapter appointmentsAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_appointment, container, false);

//        typeSpinner = view.findViewById(R.id.typeSpinner);
        categorySpinner = view.findViewById(R.id.categorySpinner);

        recyclerView = view.findViewById(R.id.appointmentRecyclerView);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());

        recyclerView.setLayoutManager(layoutManager);

        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if (currentUser != null) {

            appointmentsList = new ArrayList<>();

            readAppointments(txt_category);
            appointmentsAdapter = new AppointmentsAdapter(getContext(), appointmentsList, txt_category);

            recyclerView.setAdapter(appointmentsAdapter);

            categorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                    txt_category = category[position];
//                    Log.i(TAG, "onItemSelected: " + txt_category);
                    readAppointments(txt_category);

                    appointmentsAdapter = new AppointmentsAdapter(getContext(), appointmentsList, txt_category);

                    recyclerView.setAdapter(appointmentsAdapter);


                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });

            ArrayAdapter<String> spin_adapter2 = new ArrayAdapter<>(getContext(), R.layout.support_simple_spinner_dropdown_item, category);

            // setting adapters to spinners
            categorySpinner.setAdapter(spin_adapter2);

        }


        return view;
    }

    private void readAppointments(String txt_category) {

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(currentUser.getUid());

        databaseReference.child(txt_category).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

//                Log.i(TAG, "onDataChange: "+ dataSnapshot.getChildrenCount());

                if (dataSnapshot.exists()) {

                    appointmentsList.clear();

                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                        String string = snapshot.getKey();


                        appointmentsList.add(string);

                    }

                }

//                Log.i(TAG, "onDataChange: " + appointmentsList.size());
                appointmentsAdapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
}