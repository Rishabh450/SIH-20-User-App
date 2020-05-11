package com.example.userapp;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;
import java.util.Map;

public class AppointmentsAdapter extends RecyclerView.Adapter<AppointmentsAdapter.ViewHolder> {

    private static final String TAG = "AppointmentsAdapter";


    Context mContext;
    List<String> mAppointments;
    String category;

    DatabaseReference databaseReference;


    public AppointmentsAdapter(Context mContext, List<String> mAppointments, String category) {
        this.mContext = mContext;
        this.mAppointments = mAppointments;
        this.category = category;

//        Log.i(TAG, "AppointmentsAdapter: "+category);

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(mContext).inflate(R.layout.appointment_layout, parent, false);

        return new AppointmentsAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {


//        Log.i(TAG, "onBindViewHolder: "+ mAppointments.get(position));

        final String string = mAppointments.get(position);


        databaseReference = FirebaseDatabase.getInstance().getReference(category).child(string);

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


                if(category.equals("FIRs") && dataSnapshot.exists()){

//                    Toast.makeText(mContext, "Here", Toast.LENGTH_SHORT).show();

                    holder.nocAppointment.setVisibility(View.GONE);
                    holder.firAppointment.setVisibility(View.VISIBLE);

                    Map<String, String> map;

//                    Log.i(TAG, "onDataChange: "+string);

                    map = (Map<String, String>) dataSnapshot.getValue();
//
                    Fir fir = new Fir(map.get("complainantId"), map.get("state"), map.get("district"), map.get("place"), map.get("type"),
                                map.get("subject"), map.get("details"), String.valueOf(map.get("timeStamp")), map.get("status"),
                                map.get("reportingDate"), map.get("reportingPlace"), map.get("correspondent"));
//
////                    Log.i(TAG, "onDataChange: "+ fir.getTs());
//                    fir = dataSnapshot.getValue(Fir.class);

                    holder.firSubject.setText(fir.getSubject());
                    holder.firType.setText(fir.getType());

                    holder.firStatus.setText(fir.getStatus());

                    if(fir.getStatus().equals("Accepted")){

                        holder.firLayoutDate.setVisibility(View.VISIBLE);
                        holder.firLayoutPS.setVisibility(View.VISIBLE);
                        holder.firLayoutCorrespondent.setVisibility(View.VISIBLE);
                        holder.firDate.setText(fir.getReportingDate());
                        holder.firPS.setText(fir.getReportingPlace());
                        holder.firCorrespondent.setText(fir.getCorrespondent());

                    }


                }else if(category.equals("NOC") && dataSnapshot.exists()){


                    holder.nocAppointment.setVisibility(View.VISIBLE);
                    holder.firAppointment.setVisibility(View.GONE);

                    Map<String, String> map;

                    map = (Map<String, String>) dataSnapshot.getValue();

                    if(dataSnapshot.getChildrenCount() == 18){

                        Noc noc = new Noc(map.get("surname"), map.get("name"), map.get("presentAddress"), map.get("homeAddress"),
                                map.get("dateOfBirth"), map.get("placeOfBirth"), map.get("nocType"), map.get("charges"),
                                map.get("identificationMark"), map.get("fatherName"), map.get("motherName"), map.get("spouseName"),
                                map.get("userId"), String.valueOf(map.get("timeStamp")), map.get("status"), map.get("reportingDate"),
                                map.get("reportingPlace"), map.get("correspondent"));

                        holder.nocName.setText(noc.getName()+" "+noc.getSurname());
                        holder.nocType.setText(noc.getNocType());

                        holder.nocStatus.setText(noc.getStatus());

                        if(noc.getStatus().equals("Accepted")){

                            holder.nocLayoutDate.setVisibility(View.VISIBLE);
                            holder.nocLayoutPS.setVisibility(View.VISIBLE);
                            holder.nocLayoutCorrespondent.setVisibility(View.VISIBLE);
                            holder.nocDate.setText(noc.getReportingDate());
                            holder.nocPS.setText(noc.getReportingPlace());
                            holder.nocCorrespondent.setText(noc.getCorrespondent());

                        }


                    }
                    else{

                        Noc noc = new Noc(map.get("surname"), map.get("name"), map.get("presentAddress"), map.get("homeAddress"),
                                map.get("dateOfBirth"), map.get("placeOfBirth"), map.get("nocType"), map.get("rcNumber"),
                                map.get("icNumber"), map.get("etNumber"), map.get("userId"), String.valueOf(map.get("timeStamp")), map.get("status"),
                                map.get("reportingDate"), map.get("reportingPlace"), map.get("correspondent"));

                        holder.nocName.setText(noc.getName()+" "+noc.getSurname());
                        holder.nocType.setText(noc.getNocType());

                        holder.nocStatus.setText(noc.getStatus());

                        if(noc.getStatus().equals("Pending")){

                            holder.nocLayoutDate.setVisibility(View.VISIBLE);
                            holder.nocLayoutPS.setVisibility(View.VISIBLE);
                            holder.nocLayoutCorrespondent.setVisibility(View.VISIBLE);
                            holder.nocDate.setText(noc.getReportingDate());
                            holder.nocPS.setText(noc.getReportingPlace());
                            holder.nocCorrespondent.setText(noc.getCorrespondent());

                        }

                    }


                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    @Override
    public int getItemCount() {
        return mAppointments.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView firSubject, firType, firStatus, firDate, firPS, firCorrespondent, nocName, nocType, nocStatus, nocDate, nocPS,
                nocCorrespondent;

        LinearLayout firAppointment, nocAppointment, firLayoutDate, firLayoutPS, firLayoutCorrespondent,
                nocLayoutDate, nocLayoutPS, nocLayoutCorrespondent ;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            firAppointment = itemView.findViewById(R.id.firAppointments);
            nocAppointment = itemView.findViewById(R.id.nocAppointments);

            firLayoutDate =itemView.findViewById(R.id.firLayoutDate);
            firLayoutPS =itemView.findViewById(R.id.firLayoutPS);
            firLayoutCorrespondent =itemView.findViewById(R.id.firLayoutCorrespondent);
            nocLayoutDate =itemView.findViewById(R.id.nocLayoutDate);
            nocLayoutPS =itemView.findViewById(R.id.nocLayoutPS);
            nocLayoutCorrespondent =itemView.findViewById(R.id.nocLayoutCorrespondent);

            firSubject = itemView.findViewById(R.id.appointmentFirSubject);
            firType = itemView.findViewById(R.id.appointmentFirType);
            firStatus = itemView.findViewById(R.id.appointmentFirStatus);
            firDate = itemView.findViewById(R.id.appointmentFirReportingDate);
            firPS = itemView.findViewById(R.id.appointmentFirReportingPS);
            firCorrespondent = itemView.findViewById(R.id.appointmentFirCorrespondent);
            nocName = itemView.findViewById(R.id.appointmentNocName);
            nocType = itemView.findViewById(R.id.appointmentNocType);
            nocStatus = itemView.findViewById(R.id.appointmentNocStatus);
            nocDate = itemView.findViewById(R.id.appointmentNocReportingDate);
            nocPS = itemView.findViewById(R.id.appointmentNocReportingPS);
            nocCorrespondent = itemView.findViewById(R.id.appointmentNocCorrespondent);


        }

    }

}
