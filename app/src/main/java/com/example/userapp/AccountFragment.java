package com.example.userapp;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import de.hdodenhof.circleimageview.CircleImageView;


public class AccountFragment extends Fragment {

    private CircleImageView profilePicture;
    private TextView name, email;
    private EditText age, fathername, address, pin, phone, fax, aadhaar;
    private Button submit;
    FirebaseUser currentUser;
    DatabaseReference databaseReference;
    RadioGroup radioGroup;
    User user;
    private static final String TAG = "AccountFragment";
    String gender = "";
    int flag = 0;
    View view;
    RadioButton lastButton;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        view  = inflater.inflate(R.layout.fragment_account,container,false);
        profilePicture = view.findViewById(R.id.accountProfilePicture);
        name = view.findViewById(R.id.label_displayName);
        email = view.findViewById(R.id.label_displayEmail);
        age = view.findViewById(R.id.edit_age);
        fathername = view.findViewById(R.id.edit_fathersname);
        address = view.findViewById(R.id.edit_address);
        pin = view.findViewById(R.id.edit_pin);
        phone = view.findViewById(R.id.edit_phone);
        fax = view.findViewById(R.id.edit_fax);
        aadhaar = view.findViewById(R.id.edit_aadhaar);
        submit = view.findViewById(R.id.btn_submit);
        radioGroup = view.findViewById(R.id.radioGroup);
        lastButton = view.findViewById(R.id.radio_others);

        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(currentUser.getUid());
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(dataSnapshot.exists()) {

                    user = dataSnapshot.getValue(User.class);

                    name.setText(user.getName());
                    email.setText(user.getEmail());

                    if (!TextUtils.equals(user.getImageURL(), "default")) {


                        Glide.with(getContext()).load(user.getImageURL()).into(profilePicture);

                    }

                    Log.i(TAG, "onDataChange: " + dataSnapshot.getChildrenCount());

                    if (dataSnapshot.getChildrenCount() > 4) {

                        age.setText(user.getAge());
                        fathername.setText(user.getFathername());
                        address.setText(user.getAddress());
                        pin.setText(user.getPincode());
                        phone.setText(user.getPhone());
                        aadhaar.setText(user.getAadhaar());

                        switch (user.getGender()) {

                            case "Male":
                                radioGroup.check(R.id.radio_male);
                                break;

                            case "Female":
                                radioGroup.check(R.id.radio_female);
                                break;

                            case "Others":
                                radioGroup.check(R.id.radio_others);
                                break;

                        }

//                    if(TextUtils.equals(user.getFax(), "")){
//
//                        fax.setVisibility(View.INVISIBLE);
//
//                    }

                    }

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {

//                lastButton.setError(null);

                switch (checkedId){

                    case R.id.radio_male:
                        gender = "Male";
                        break;

                    case R.id.radio_female:
                        gender = "Female";
                        break;

                    case R.id.radio_others:
                        gender = "Others";
                        break;

                }


                Log.i(TAG, "onCheckedChanged: "+gender);

            }
        });




        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String txt_age, txt_fathername, txt_address, txt_pin, txt_phone, txt_fax, txt_aadhaar;

                txt_age = age.getText().toString();
                txt_fathername = fathername.getText().toString();
                txt_address = address.getText().toString();
                txt_pin = pin.getText().toString();
                txt_phone = phone.getText().toString();
                txt_fax = fax.getText().toString();
                txt_aadhaar = aadhaar.getText().toString();



                if(radioGroup.getCheckedRadioButtonId() <= 0){  //radiogroup is your radio group object

                    lastButton.setError("Choose one option");   //Set error to last Radio button
                    flag = 1;

                }


                if(txt_age.trim().equalsIgnoreCase("")){

                    age.setError("This field cannot be blank");
                    flag = 1;

                }

                if(txt_fathername.trim().equalsIgnoreCase("")){

                    fathername.setError("This field cannot be blank");
                    flag = 1;

                }

                if(txt_address.trim().equalsIgnoreCase("")){

                    address.setError("This field cannot be blank");
                    flag = 1;

                }

                if(txt_pin.trim().equalsIgnoreCase("")){

                    pin.setError("This field cannot be blank");
                    flag = 1;

                }

                if(txt_phone.trim().equalsIgnoreCase("")){

                    phone.setError("This field cannot be blank");
                    flag = 1;

                }

                if(txt_aadhaar.trim().equalsIgnoreCase("")){

                    aadhaar.setError("This field cannot be blank");
                    flag = 1;

                }


                if(flag == 0){

                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(currentUser.getUid());

                    User user1 = new User(user.getName(), user.getEmail(), user.getImageURL(), user.getUserId(), gender, txt_age, txt_fathername,
                            txt_address, txt_pin, txt_phone, txt_fax, txt_aadhaar);

                    reference.setValue(user1).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){

                                Toast.makeText(getContext(), "Updated Successfully", Toast.LENGTH_SHORT).show();

                            }
                        }
                    });

                }


            }
        });




        return view;
    }



}