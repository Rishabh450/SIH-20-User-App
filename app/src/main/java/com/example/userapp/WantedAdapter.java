package com.example.userapp;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.transition.AutoTransition;
import androidx.transition.TransitionManager;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.util.List;

public class WantedAdapter extends RecyclerView.Adapter<WantedAdapter.ViewHolder> {

    private static final String TAG = "WantedAdapter";

    Context mContext;
    List<String> mCriminals;
    Criminals criminals;


    public WantedAdapter(Context mContext, List<String> mCriminals){
        this.mContext = mContext;
        this.mCriminals = mCriminals;
//        Log.i(TAG, "WantedAdapter: "+ this.mCriminals.size());
    }



    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(mContext).inflate(R.layout.wanted_layout,parent, false);
        return new WantedAdapter.ViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {

        String string = mCriminals.get(position);


        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("criminal_ref").child(string);


        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                criminals = dataSnapshot.getValue(Criminals.class);

                if(dataSnapshot.exists()) {

                    assert criminals != null;
                    Glide.with(mContext).load(criminals.getProfile_pic_url()).into(holder.picture);
                    holder.name.setText(criminals.getCriminal_name());
                    holder.bodyMark.setText(criminals.getCriminal_BodyMark());
                    holder.rating.setText(criminals.getCriminal_rating());

                    holder.showButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {


                            if (holder.last_crime.getVisibility() == View.GONE) {

                                holder.showButton.setRotation(180);
                                TransitionManager.beginDelayedTransition(holder.last_crime, new AutoTransition());

                                holder.last_crime.setVisibility(View.VISIBLE);
                            } else {

                                holder.showButton.setRotation(360);
                                TransitionManager.beginDelayedTransition(holder.last_crime, new AutoTransition());
                                holder.last_crime.setVisibility(View.GONE);
                            }
                        }
                    });


//                Log.i(TAG, "onDataChange: "+criminals.getLast_crime());

                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference("crime_ref").child(criminals.getLast_crime());
                    reference.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                            if(dataSnapshot.exists()) {

                                Crime crime = dataSnapshot.getValue(Crime.class);

                                if (crime != null) {

                                    holder.crimeType.setText(crime.getCrime_type());
                                    holder.crimePlace.setText(crime.getDistrict_of_crime() + ", " + crime.getState_of_crime());
                                    holder.crimeRating.setText(crime.getRating_of_crime());


                                    long milliseconds = Long.parseLong(crime.gettime_when_crime_added());
                                    String simpleDateFormat = DateFormat.getDateTimeInstance().format(milliseconds);
                                    holder.crimeDate.setText(simpleDateFormat);

//                            Log.i(TAG, "onDataChange: " + crime.getCrime_id());
                                }
                            }

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        holder.report.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent =  new Intent(mContext, ReportActivity.class);
                intent.putExtra("criminal_id", criminals.criminal_id);
                mContext.startActivity(intent);

            }
        });


    }

    @Override
    public int getItemCount() {
        return mCriminals.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder{

        ImageView picture;
        TextView name, bodyMark, rating, crimeType, crimePlace, crimeRating, crimeDate;
        Button showButton, report;

        LinearLayout last_crime;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            picture = itemView.findViewById(R.id.wanted_picture);
            name = itemView.findViewById(R.id.wanted_name);
            bodyMark = itemView.findViewById(R.id.wanted_body_mark);
            rating = itemView.findViewById(R.id.wanted_rating);
            showButton = itemView.findViewById(R.id.showButton);
            last_crime = itemView.findViewById(R.id.last_crime);
            crimeType = itemView.findViewById(R.id.crime_type);
            crimePlace = itemView.findViewById(R.id.crime_place);
            crimeRating = itemView.findViewById(R.id.crime_rating);
            crimeDate = itemView.findViewById(R.id.crime_date);
            report = itemView.findViewById(R.id.btn_report);

        }
    }

}
