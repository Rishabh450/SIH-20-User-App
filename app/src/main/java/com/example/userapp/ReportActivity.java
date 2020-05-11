package com.example.userapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.widget.ContentLoadingProgressBar;
import androidx.fragment.app.DialogFragment;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.face.FirebaseVisionFace;
import com.google.firebase.ml.vision.face.FirebaseVisionFaceDetector;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import de.hdodenhof.circleimageview.CircleImageView;

public class ReportActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {

    EditText time, date, place, details;
    Button submit;
    int flag = 0;
    private static final String TAG = "ReportActivity";
    ImageView insertImage;

    LocationManager locationManager;
    LocationListener locationListener;

    Location lastKnownLocation;
    Uri resultUri = null;

    CircleImageView imageView;
    StorageReference storageReference;
    String txt_imageURL = null;


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode == 1){
            if(grantResults.length>0 && grantResults[0]== PackageManager.PERMISSION_GRANTED){
                if(ContextCompat.checkSelfPermission(ReportActivity.this,Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0,locationListener);
                }
            }
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);

        Toolbar toolbar = findViewById(R.id.reportActivityToolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Report");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        final String criminal_id = getIntent().getStringExtra("criminal_id");
//        Log.i(TAG, "onCreate: "+ criminal_id);

        time = findViewById(R.id.lastSeenTime);
        date = findViewById(R.id.lastSeenDate);
        place = findViewById(R.id.lastSeenPlace);
        details = findViewById(R.id.reportDetails);
        insertImage = findViewById(R.id.insertImage);
        imageView = findViewById(R.id.reportImage);

        lastKnownLocation = getLastKnownLocation();

        Location currentLocation = lastKnownLocation;

        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);


        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {

                if(location.toString() != null){

                    lastKnownLocation = location;

                }


//                Log.i("TAG",location.toString());

            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };


        if (ContextCompat.checkSelfPermission(ReportActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(ReportActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        } else {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
        }

//        Log.i(TAG, "onCreate: "+lastKnownLocation.toString());



        submit = findViewById(R.id.btn_submit_report);

        time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DialogFragment timePicker = new TimePickerFragment();
                timePicker.show(getSupportFragmentManager(), "time_picker");

            }
        });
        
        date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DialogFragment datePicker = new DatePickerFragment();
                datePicker.show(getSupportFragmentManager(), "date picker");

            }
        });


        insertImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                chooseImage();

            }
        });


        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {



                String txt_time, txt_date, txt_place, txt_details, txt_location;
                txt_time = time.getText().toString();
                txt_date = date.getText().toString();
                txt_place = place.getText().toString();
                txt_details = details.getText().toString();
                txt_location = lastKnownLocation.getLatitude()+"||"+lastKnownLocation.getLongitude();


                if(txt_date.trim().equalsIgnoreCase("")){

                    date.setError("This field cannot be blank");
                    flag = 1;

                }

                if(txt_place.trim().equalsIgnoreCase("")){

                    place.setError("This field cannot be blank");
                    flag = 1;

                }

                if(flag == 0){



//                    final ProgressBar progressBar = new ProgressBar(ReportActivity.this, null, android.R.attr.progressBarStyleSmall);
//                    progressBar.setVisibility(View.VISIBLE);
//                    if(progressBar.isAnimating() == true) Log.i(TAG, "onClick: true");

                    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Criminal Report").push();


                    final ProgressDialog progressDialog = new ProgressDialog(ReportActivity.this);
                    progressDialog.show();



//                    Log.i(TAG, "onClick: "+txt_location);

//                    Log.i(TAG, "onClick: "+txt_imageURL);

                    Report report = new Report(criminal_id, txt_time, txt_date, txt_place, txt_details, txt_location, txt_imageURL);

                    databaseReference.setValue(report).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if(task.isSuccessful()){


                                progressDialog.dismiss();
//                                progressBar.setVisibility(View.GONE);
                                Toast.makeText(ReportActivity.this, "Reported Successfully", Toast.LENGTH_SHORT).show();
                                time.setText("");
                                date.setText("");
                                place.setText("");
                                details.setText("");
                                imageView.setImageURI(null);


                                locationManager.removeUpdates(locationListener);
//                                finish();

                            }else{


                                progressDialog.dismiss();
                                Toast.makeText(ReportActivity.this, "Failed", Toast.LENGTH_SHORT).show();



                            }

                        }
                    });

                }







            }
        });


    }

    private void chooseImage() {

        CropImage.activity().setAspectRatio(1, 1).setGuidelines(CropImageView.Guidelines.ON).start(this);

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                resultUri = result.getUri();

                imageView.setImageURI(resultUri);
                uploadImage(resultUri);


            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }

    }



    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month);
        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        String currentDateString = dateFormat.format(calendar.getTime());

        date.setText(currentDateString);

    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

        time.setText(hourOfDay+":"+minute);

    }


    private Location getLastKnownLocation() {
        LocationManager mLocationManager;
        mLocationManager = (LocationManager) getApplicationContext().getSystemService(LOCATION_SERVICE);
        List<String> providers = mLocationManager.getProviders(true);
        Location bestLocation = null;
        for (String provider : providers) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    Activity#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for Activity#requestPermissions for more details.


                }
            }
            Location l = mLocationManager.getLastKnownLocation(provider);
            if (l == null) {
                continue;
            }
            if (bestLocation == null || l.getAccuracy() < bestLocation.getAccuracy()) {
                // Found best last known location: %s", l);
                bestLocation = l;
            }
        }
        return bestLocation;
    }



    private void uploadImage(final Uri filePath2) {

        if (filePath2 != null) {
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Uploading");
            progressDialog.show();
            progressDialog.setProgress(0);

            final StorageReference ref = FirebaseStorage.getInstance().getReference().child("images/" + UUID.randomUUID().toString());

            ref.putFile(filePath2).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    progressDialog.dismiss();
                    if (task.isSuccessful()) {

//                        Toast.makeText(ReportActivity.this, "Picture uploaded successfully", Toast.LENGTH_SHORT).show();
                    }

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    progressDialog.dismiss();
                    Toast.makeText(ReportActivity.this, "Failed", Toast.LENGTH_SHORT).show();
                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {
                    double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                    int currentprogress = (int) progress;
                    progressDialog.setProgress(currentprogress);
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {

                            txt_imageURL = uri.toString();
                            Log.i(TAG, "onSuccess: "+txt_imageURL);

                        }
                    });
                }
            });

        } else {
            Toast.makeText(this, "No image selected", Toast.LENGTH_SHORT).show();
        }

    }


}
