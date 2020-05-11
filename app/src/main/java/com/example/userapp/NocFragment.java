package com.example.userapp;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class NocFragment extends Fragment implements DatePickerDialog.OnDateSetListener {

    private static final String TAG = "NocFragment";

    EditText surname, name, presentAddress, homeAddress, dateOfBirth, placeOfBirth, charges, identificationMark, fatherName, motherName
            ,spouseName, rcNumber, icNumber, etNumber;
    Spinner spinner;
    Button submit;
    LinearLayout passport, vehicle;
    String nocType;

    String[] type = {"Passport", "Re-registering Vehicle"};

    FirebaseUser currentUser;

    int flag = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_noc, container, false);

        surname = view.findViewById(R.id.nocSurname);
        name = view.findViewById(R.id.nocName);
        presentAddress = view.findViewById(R.id.nocPresentAddress);
        homeAddress = view.findViewById(R.id.nocHomeAddress);
        dateOfBirth = view.findViewById(R.id.nocDateOfBirth);
        placeOfBirth = view.findViewById(R.id.nocPlaceOfBirth);
        charges = view.findViewById(R.id.nocCharges);
        fatherName = view.findViewById(R.id.nocFatherName);
        motherName = view.findViewById(R.id.nocMotherName);
        identificationMark = view.findViewById(R.id.nocIdentificationMark);
        spouseName = view.findViewById(R.id.nocSpouseName);
        rcNumber = view.findViewById(R.id.nocRC);
        icNumber = view.findViewById(R.id.nocInsuranceCertificate);
        etNumber = view.findViewById(R.id.nocEmissionTest);
        spinner = view.findViewById(R.id.nocSpinner);
        submit = view.findViewById(R.id.btn_submit_noc);
        passport = view.findViewById(R.id.nocPassport);
        vehicle = view.findViewById(R.id.nocVehicle);

        currentUser = FirebaseAuth.getInstance().getCurrentUser();


        dateOfBirth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DialogFragment datePicker = new DatePickerFragmentForFragment();
                datePicker.setTargetFragment(NocFragment.this,0);
                datePicker.show(getFragmentManager(), "date picker");

            }
        });


        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                // Save the type of noc in the string nocType
                nocType = type[position];
                if(position == 0){

                    passport.setVisibility(View.VISIBLE);
                    vehicle.setVisibility(View.GONE);

                }

                if(position == 1){

                    passport.setVisibility(View.GONE);
                    vehicle.setVisibility(View.VISIBLE);

                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        //setting array adaptors to spinners
        //ArrayAdapter is a BaseAdapter that is backed by an array of arbitrary objects
            ArrayAdapter<String> spin_adapter = new ArrayAdapter<>(getContext(), R.layout.support_simple_spinner_dropdown_item, type);

            // setting adapters to spinners
            spinner.setAdapter(spin_adapter);

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String txt_surname, txt_name, txt_present, txt_home, txt_DOB, txt_POB;

                txt_surname = surname.getText().toString();
                txt_name = name.getText().toString();
                txt_present = presentAddress.getText().toString();
                txt_home = homeAddress.getText().toString();
                txt_DOB = dateOfBirth.getText().toString();
                txt_POB = placeOfBirth.getText().toString();

                if(txt_surname.trim().equalsIgnoreCase("")){

                    surname.setError("This field cannot be Empty");
                    flag = 1;

                }

                if(txt_name.trim().equalsIgnoreCase("")){

                    name.setError("This field cannot be Empty");
                    flag = 1;

                }

                if(txt_present.trim().equalsIgnoreCase("")){

                    presentAddress.setError("This field cannot be Empty");
                    flag = 1;

                }

                if(txt_home.trim().equalsIgnoreCase("")){

                    homeAddress.setError("This field cannot be Empty");
                    flag = 1;

                }

                if(txt_DOB.trim().equalsIgnoreCase("")){

                    dateOfBirth.setError("This field cannot be Empty");
                    flag = 1;

                }

                if(txt_POB.trim().equalsIgnoreCase("")){

                    placeOfBirth.setError("This field cannot be Empty");
                    flag = 1;

                }

                if(TextUtils.equals(nocType,"Passport")){

                    String txt_charges, txt_mark, txt_father, txt_mother, txt_spouse;

                    txt_charges = charges.getText().toString();
                    txt_mark = identificationMark.getText().toString();
                    txt_father = fatherName.getText().toString();
                    txt_mother = motherName.getText().toString();
                    txt_spouse = spouseName.getText().toString();


                    if(txt_charges.trim().equalsIgnoreCase("")){

                        charges.setError("This field cannot be Empty");
                        flag = 1;

                    }

                    if(txt_mark.trim().equalsIgnoreCase("")){

                        identificationMark.setError("This field cannot be Empty");
                        flag = 1;

                    }

                    if(txt_father.trim().equalsIgnoreCase("")){

                        fatherName.setError("This field cannot be Empty");
                        flag = 1;

                    }

                    if(txt_mother.trim().equalsIgnoreCase("")){

                        motherName.setError("This field cannot be Empty");
                        flag = 1;

                    }

                    if(txt_spouse.trim().equalsIgnoreCase("")){

                        spouseName.setError("This field cannot be Empty");
                        flag = 1;

                    }


                    if(flag == 0){

                        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("NOC").push();


                        final String  string =databaseReference.getKey();

//                        Log.i(TAG, "onClick: "+currentUser.getUid());

                        HashMap<String, Object> hashMap = new HashMap<>();

                        hashMap.put("surname", txt_surname);
                        hashMap.put("name", txt_name);
                        hashMap.put("presentAddress", txt_present);
                        hashMap.put("homeAddress", txt_home);
                        hashMap.put("dateOfBirth", txt_DOB);
                        hashMap.put("placeOfBirth", txt_POB);
                        hashMap.put("nocType", nocType);
                        hashMap.put("charges", txt_charges);
                        hashMap.put("identificationMark", txt_mark);
                        hashMap.put("fatherName", txt_father);
                        hashMap.put("motherName", txt_mother);
                        hashMap.put("spouseName", txt_spouse);
                        hashMap.put("userId", currentUser.getUid());
                        hashMap.put("timeStamp", ServerValue.TIMESTAMP);
                        hashMap.put("status", "Pending");
                        hashMap.put("reportingDate", "");
                        hashMap.put("reportingPlace", "");
                        hashMap.put("correspondent", "");

//                        Noc noc = new Noc(txt_surname, txt_name, txt_present, txt_home, txt_DOB, txt_POB, nocType, txt_charges, txt_mark,
//                                txt_father, txt_mother, txt_spouse, currentUser.getUid(), ServerValue.TIMESTAMP, "Pending",
//                                "", "", "");

                        final ProgressDialog progressDialog = new ProgressDialog(getContext());
                        progressDialog.show();

                        databaseReference.setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {

                                if(task.isSuccessful()){

                                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users")
                                            .child(currentUser.getUid()).child("NOC").child(string);

                                    reference.setValue(ServerValue.TIMESTAMP).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {

                                            if(task.isSuccessful()){

                                                progressDialog.dismiss();
                                                Toast.makeText(getContext(), "Submitted Successfully", Toast.LENGTH_SHORT).show();


                                            }

                                        }
                                    });


                                }else{

                                    progressDialog.dismiss();
                                    Toast.makeText(getContext(), "Failed", Toast.LENGTH_SHORT).show();

                                }

                                name.setText("");
                                surname.setText("");
                                presentAddress.setText("");
                                homeAddress.setText("");
                                dateOfBirth.setText("");
                                placeOfBirth.setText("");
                                charges.setText("");
                                identificationMark.setText("");
                                fatherName.setText("");
                                motherName.setText("");
                                spouseName.setText("");
                                flag = 0;

                            }
                        });

                    }


                }
                else{

                    String txt_rc, txt_ic, txt_et;

                    txt_rc = rcNumber.getText().toString();
                    txt_ic = icNumber.getText().toString();
                    txt_et = etNumber.getText().toString();

                    if(txt_rc.trim().equalsIgnoreCase("")){

                        rcNumber.setError("This field cannot be Empty");
                        flag = 1;

                    }

                    if(txt_ic.trim().equalsIgnoreCase("")){

                        icNumber.setError("This field cannot be Empty");
                        flag = 1;

                    }

                    if(txt_et.trim().equalsIgnoreCase("")){

                        etNumber.setError("This field cannot be Empty");
                        flag = 1;

                    }


                    if(flag == 0){

                        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("NOC").push();

                        final String  string =databaseReference.getKey();


                        HashMap<String, Object> hashMap = new HashMap<>();

                        hashMap.put("surname", txt_surname);
                        hashMap.put("name", txt_name);
                        hashMap.put("presentAddress", txt_present);
                        hashMap.put("homeAddress", txt_home);
                        hashMap.put("dateOfBirth", txt_DOB);
                        hashMap.put("placeOfBirth", txt_POB);
                        hashMap.put("nocType", nocType);
                        hashMap.put("rcNumber", txt_rc);
                        hashMap.put("icNumber", txt_ic);
                        hashMap.put("etNumber", txt_et);
                        hashMap.put("userId", currentUser.getUid());
                        hashMap.put("timeStamp", ServerValue.TIMESTAMP);
                        hashMap.put("status", "Pending");
                        hashMap.put("reportingDate", "");
                        hashMap.put("reportingPlace", "");
                        hashMap.put("correspondent", "");

//                        Noc noc = new Noc(txt_surname, txt_name, txt_present, txt_home, txt_DOB, txt_POB, nocType, txt_rc, txt_ic,
//                                txt_et, currentUser.getUid(), ServerValue.TIMESTAMP, "Pending", "", "",
//                                "");

                        final ProgressDialog progressDialog = new ProgressDialog(getContext());
                        progressDialog.show();

                        databaseReference.setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {

                                if(task.isSuccessful()){

                                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users")
                                            .child(currentUser.getUid()).child("NOC").child(string);

                                    reference.setValue(ServerValue.TIMESTAMP).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {

                                            if(task.isSuccessful()){

                                                progressDialog.dismiss();
                                                Toast.makeText(getContext(), "Submitted Successfully", Toast.LENGTH_SHORT).show();


                                            }

                                        }
                                    });


                                }else{

                                    progressDialog.dismiss();
                                    Toast.makeText(getContext(), "Failed", Toast.LENGTH_SHORT).show();

                                }

                                name.setText("");
                                surname.setText("");
                                presentAddress.setText("");
                                homeAddress.setText("");
                                dateOfBirth.setText("");
                                placeOfBirth.setText("");
                                rcNumber.setText("");
                                icNumber.setText("");
                                etNumber.setText("");
                                flag = 0;

                            }
                        });

                    }

                }


            }
        });



        return view;

    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month);
        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        String currentDateString = dateFormat.format(calendar.getTime());

        dateOfBirth.setText(currentDateString);

    }
}
