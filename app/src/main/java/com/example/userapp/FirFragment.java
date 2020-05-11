package com.example.userapp;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;


public class FirFragment extends Fragment {


    DatabaseReference databaseReference;
    FirebaseUser currentUser;

    TextView name, email, age, gender;

    // access all auto complete text views
    AutoCompleteTextView act,state,dist;

    EditText place, subject, details;

    Button submit;

    private static final String TAG = "FirFragment";

    // array lists
    // for the spinner in the format : City_no : City , State. Eg : 144 : New Delhi , India
    ArrayList<String> listSpinner=new ArrayList<String>();
    // to store the city and state in the format : City , State. Eg: New Delhi , India
    ArrayList<String> listAll=new ArrayList<String>();
    // for listing all states
    ArrayList<String> listState=new ArrayList<String>();
    // for listing all cities
    ArrayList<String> listCity=new ArrayList<String>();
    View view;


    Map<String , ArrayList<String>> stateDistrict;
    Map<String , ArrayList<String>> districtLocality = new HashMap<>();


    Spinner spinner;
    String crimeType = "";

    int flag = 0;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        view = inflater.inflate(R.layout.fragment_fir, container, false);



        state = view.findViewById(R.id.occurrence_state);
        dist = view.findViewById(R.id.occurrence_district);

        name = view.findViewById(R.id.complainant_name);
        email = view.findViewById(R.id.complainant_email);
        age = view.findViewById(R.id.complainant_age);
        gender = view.findViewById(R.id.complainant_gender);
        place = view.findViewById(R.id.occurrence_place);
        subject = view.findViewById(R.id.complain_subject);
        details = view.findViewById(R.id.complain_details);
        submit = view.findViewById(R.id.btn_submit_fir);


        spinner =view.findViewById(R.id.spinner);
        final String[] type = {"Select type of crime", "Murder", "Illegal Trafficking", "Women Related Crime", "Children Related Crime",
                "Business Related Crime", "Others"};


        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(currentUser.getUid());

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                long count = dataSnapshot.getChildrenCount();
                if(count<=4){

                    new AlertDialog.Builder(getContext())
                            .setTitle("Incomplete Information")
                            .setMessage("Account Details should be filled")
                            .setCancelable(false)

                            // Specifying a listener allows you to take an action before dismissing the dialog.
                            // The dialog is automatically dismissed when a dialog button is clicked.
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {

                                    // Continue with delete operation
                                    // Since account details are incomplete so we need to redirect it to account fragment

                                    try{
                                        ((OnAlertDialogBoxClickedListener) getContext()).onClick();
                                    }catch (ClassCastException cce){

                                    }


//The below code is not used for changing fragments because it cannot be used to change highlighted text of navigation drawer so if you
// want tha then implement interface as done above

/*                                    FragmentTransaction fragmentTransaction = getActivity()
                                            .getSupportFragmentManager().beginTransaction();
                                    AccountFragment accountFragment = new AccountFragment();
                                    fragmentTransaction.replace(R.id.fragment_container, accountFragment);
                                    fragmentTransaction.commit();
*/


                                }
                            }).setIcon(R.drawable.ic_add_alert_black_24dp)
                            .show();

                }else{

                    User user = dataSnapshot.getValue(User.class);
                    name.setText(user.getName());
                    email.setText(user.getEmail());
                    age.setText(user.getAge());
                    gender.setText(user.getGender());

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        callAll();


        // OnItemSelectedListener() is used because we need to also store the type of crime reported by the user and we will store it in
        // string crimeType

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                // Save the type of crime in the string crimeType
                crimeType = type[position];
//                Log.i(TAG, "onItemSelected: "+ crimeType);

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



        return view;
    }


    public interface OnAlertDialogBoxClickedListener{

        public void onClick();

    }


    private void callAll() {

        obj_list();
        addState();
        onClicks();

    }

    private void onClicks() {

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String txt_state, txt_district, txt_place, txt_subject, txt_details;

                txt_state = state.getText().toString();
                txt_district = dist.getText().toString();
                txt_place = place.getText().toString();
                txt_subject = subject.getText().toString();
                txt_details = details.getText().toString();

                if(txt_state.trim().equalsIgnoreCase("")){

                    state.setError("This field cannot be blank");
                    flag = 1;

                }

                if(txt_district.trim().equalsIgnoreCase("")){

                    dist.setError("This field cannot be blank");
                    flag = 1;

                }

                if(txt_place.trim().equalsIgnoreCase("")){

                    place.setError("This field cannot be blank");
                    flag = 1;

                }

                if(txt_subject.trim().equalsIgnoreCase("")){

                    subject.setError("This field cannot be blank");
                    flag = 1;

                }

                if(txt_details.trim().equalsIgnoreCase("")){

                    details.setError("This field cannot be blank");
                    flag = 1;

                }

                if(crimeType.trim().equalsIgnoreCase("Select type of crime")){

                    ((TextView)spinner.getSelectedView()).setError("Select an option");
                    flag = 1;

//                    TextView errorText = (TextView) spinner.getSelectedView();
//                    errorText.setText("");
//                    errorText.setTextColor(Color.RED);  //  Just to highlight that this an error
//                    errorText.setText("Select an option");  //changes the selected item text to this

                }


                DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("FIRs").push();

                final String string = reference.getKey();




//                Fir fir = new Fir(currentUser.getUid(), txt_state, txt_district, txt_place, crimeType, txt_subject, txt_details,
//                        ServerValue.TIMESTAMP, "Pending", "", "", "");

                if(flag == 0){

                    HashMap<String, Object> mapHashMap = new HashMap<>();

                    mapHashMap.put("complainantId", currentUser.getUid());
                    mapHashMap.put("state", txt_state);
                    mapHashMap.put("district", txt_district);
                    mapHashMap.put("place", txt_place);
                    mapHashMap.put("type", crimeType);
                    mapHashMap.put("subject", txt_subject);
                    mapHashMap.put("details", txt_details);
                    mapHashMap.put("timeStamp", ServerValue.TIMESTAMP);
                    mapHashMap.put("status", "Pending");
                    mapHashMap.put("reportingDate", "");
                    mapHashMap.put("reportingPlace", "");
                    mapHashMap.put("correspondent", "");

                    reference.setValue(mapHashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if(task.isSuccessful()){


                                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users")
                                        .child(currentUser.getUid()).child("FIRs").child(string);

                                databaseReference.setValue(ServerValue.TIMESTAMP).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {

                                        if(task.isSuccessful()){

                                            Toast.makeText(getContext(), "FIR Filled successfully.", Toast.LENGTH_SHORT).show();
                                            state.setText("");
                                            dist.setText("");
                                            place.setText("");

                                            spinner.setSelection(0);

                                            subject.setText("");
                                            details.setText("");
                                            flag=0;

                                            state.isFocusable();


                                            //  To hide keyboard

                                            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Activity.INPUT_METHOD_SERVICE);
                                            imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);

                                        }

                                    }
                                });



                            }

                        }
                    });

                }


            }
        });

    }


    // Get the content of cities.json from assets directory and store it as string
    public String getJson()
    {
        String json=null;
        try
        {
            // Opening cities.json file
            InputStream is = getActivity().getAssets().open("cities.json");
            // is there any content in the file
            int size = is.available();
            byte[] buffer = new byte[size];
            // read values in the byte array
            is.read(buffer);
            // close the stream --- very important
            is.close();
            // convert byte to string
            json = new String(buffer, "UTF-8");
        }
        catch (IOException ex)
        {
            ex.printStackTrace();
            return json;
        }
        return json;
    }

    // This add all JSON object's data to the respective lists
    void obj_list()
    {
        // Exceptions are returned by JSONObject when the object cannot be created
        try
        {
            // Convert the string returned to a JSON object
            JSONObject jsonObject=new JSONObject(getJson());
            // Get Json array
            JSONArray array=jsonObject.getJSONArray("array");
            // Navigate through an array item one by one
            for(int i=0;i<array.length();i++)
            {
                // select the particular JSON data
                JSONObject object=array.getJSONObject(i);
                String city=object.getString("name");
                String state=object.getString("state");
                // add to the lists in the specified format
                listSpinner.add(String.valueOf(i+1)+" : "+city+" , "+state);
                listAll.add(city+" , "+state);
                listCity.add(city);
                listState.add(state);
            }
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
    }
    void dist_list(String s)
    {
        // Exceptions are returned by JSONObject when the object cannot be created
        try
        {
            // Convert the string returned to a JSON object
            JSONObject jsonObject=new JSONObject(getJson());
            // Get Json array
            listCity.clear();
            JSONArray array=jsonObject.getJSONArray("array");
            // Navigate through an array item one by one
            for(int i=0;i<array.length();i++)
            {
                JSONObject object=array.getJSONObject(i);
                String city=object.getString("name");
                String state=object.getString("state");
                if(state.equals(s))
                    listCity.add(city);
            }
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
    }
    // The third auto complete text view
    void addState()
    {
        Set<String> set = new HashSet<String>(listState);
        act = view.findViewById(R.id.occurrence_state);
//        adapterSetting(new ArrayList(set));
        ArrayList<String> a = new ArrayList(set);
        ArrayAdapter<String> adapter=new ArrayAdapter<String>(getContext(),android.R.layout.simple_dropdown_item_1line,a);
        act.setAdapter(adapter);


        act.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                addCity(state.getText().toString());
            }
        });


//        hideKeyboard();

    }

    private void addCity(String state) {

        dist_list(state);
        act = view.findViewById(R.id.occurrence_district);
//        Log.i(TAG, "addCity: "+listCity.size());
        ArrayAdapter<String> adapter=new ArrayAdapter<>(getContext(),android.R.layout.simple_dropdown_item_1line,listCity);

        dist.setAdapter(adapter);


//        hideKeyboard();

    }


    // Hide keyboard on selecting an option
//    private void hideKeyboard(){
//
//        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Activity.INPUT_METHOD_SERVICE);
//        imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
//
//    }



}
