package com.example.userapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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
import java.util.List;
import java.util.UUID;

import de.hdodenhof.circleimageview.CircleImageView;

public class SignUpActivity extends AppCompatActivity {

    private static final String TAG = "SignUpActivity";

    EditText emaiId, name, password, password2;
    CircleImageView profilePicture;
    Button signUp;
    private FirebaseAuth mAuth;
    StorageReference storageReference;
    String txt_email, txt_name, txt_password, txt_password2, txt_imageURL;
    Uri resultUri = null;

    FirebaseVisionImage image;
    int numberOfFaces, flag=0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        Toolbar toolbar_signUp = findViewById(R.id.signUpToolbar);
        setSupportActionBar(toolbar_signUp);
        getSupportActionBar().setTitle("SignUp");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        emaiId = findViewById(R.id.signUpEmail);
        name = findViewById(R.id.signUpName);
        password = findViewById(R.id.signUpPassword);
        password2 = findViewById(R.id.signUpPassword2);

        profilePicture = findViewById(R.id.signUpProfilePicture);
        storageReference = FirebaseStorage.getInstance().getReference();

        signUp = findViewById(R.id.btnSignUp);
        mAuth = FirebaseAuth.getInstance();
        txt_imageURL = "default";


        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                txt_email = emaiId.getText().toString();
                txt_name = name.getText().toString();
                txt_password = password.getText().toString();
                txt_password2 = password2.getText().toString();

                if(txt_email.trim().equalsIgnoreCase("")){

                    emaiId.setError("This field cannot be empty!");
                    flag = 1;

                }


                if(txt_name.trim().equalsIgnoreCase("")){

                    name.setError("This field cannot be empty!");
                    flag = 1;

                }


                if(txt_password.trim().equalsIgnoreCase("")){

                    password.setError("This field cannot be empty!");
                    flag = 1;

                }


                if(txt_password2.trim().equalsIgnoreCase("")){

                    password2.setError("This field cannot be empty!");
                    flag = 1;

                }

                if(resultUri == null){

                    Toast.makeText(SignUpActivity.this, "Choose a proper Profile Picture", Toast.LENGTH_SHORT).show();
                    flag = 1;

                }

//                if (txt_email.equalsIgnoreCase("") || TextUtils.isEmpty(txt_name) || TextUtils.isEmpty(txt_password) || TextUtils.isEmpty(txt_password2)) {
//
//                    Toast.makeText(SignUpActivity.this, "Field(s) cannot be empty!", Toast.LENGTH_SHORT).show();
//
//                }
                if (!password.equals(password2) && !txt_password.equals("") && !txt_password2.equals("")) {
                    Toast.makeText(SignUpActivity.this, "Passwords don't match", Toast.LENGTH_SHORT).show();
                    password.setText("");
                    password2.setText("");
                    flag = 1;
                }

                if(flag == 0){

                    register(txt_email, txt_password);

                }


            }
        });


        profilePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                chooseImage();

            }
        });

    }

    private void register(final String txt_email, String txt_password) {

        ProgressDialog progressDialog = new ProgressDialog(SignUpActivity.this);
        progressDialog.setTitle("Creating Account");
        progressDialog.show();

        mAuth.createUserWithEmailAndPassword(txt_email, txt_password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {

                    FirebaseUser currentUser = mAuth.getCurrentUser();
                    String userId = currentUser.getUid();

                    uploadImage(resultUri);

                    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(userId);

                    User user = new User(txt_name, txt_email, txt_imageURL, userId);
                    databaseReference.setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {

                                Intent intent = new Intent(SignUpActivity.this, MainActivity.class);
                                startActivity(intent);
                                finish();
                            }
                        }
                    });

                } else {

                    Toast.makeText(SignUpActivity.this, "You can't register with this email and password", Toast.LENGTH_SHORT).show();

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

                final ProgressDialog progressDialog = new ProgressDialog(this);
                progressDialog.setMessage("Checking...");
                progressDialog.setCancelable(false);
                progressDialog.show();


                if(resultUri != null) {

                    try {
                        image = FirebaseVisionImage.fromFilePath(getApplicationContext(), resultUri);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    FirebaseVisionFaceDetector detector = FirebaseVision.getInstance().getVisionFaceDetector();

                    Task<List<FirebaseVisionFace>> taskResult =
                            detector.detectInImage(image).addOnSuccessListener(new OnSuccessListener<List<FirebaseVisionFace>>() {
                                @Override
                                public void onSuccess(List<FirebaseVisionFace> firebaseVisionFaces) {

//                                    progressDialog.dismiss();
//                                    Toast.makeText(SignUpActivity.this, ""+firebaseVisionFaces.size(), Toast.LENGTH_SHORT).show();
                                    numberOfFaces = firebaseVisionFaces.size();

                                    if(numberOfFaces == 1){

                                        profilePicture.setImageURI(resultUri);

                                    }else if(numberOfFaces > 1){

                                        resultUri = null;
                                        Toast.makeText(SignUpActivity.this, "More than 1 faces detected. Choose a new photo containing only your face.", Toast.LENGTH_SHORT).show();

                                    }else{

                                        resultUri = null;
                                        Toast.makeText(SignUpActivity.this, "No faces detected. Choose a photo of your face.", Toast.LENGTH_SHORT).show();

                                    }

                                    progressDialog.dismiss();


                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {

//                                    progressDialog.dismiss();
                                    e.printStackTrace();
                                    Toast.makeText(SignUpActivity.this, "Failed", Toast.LENGTH_SHORT).show();

                                }
                            });


                }else{
                    Toast.makeText(this, "No image selected", Toast.LENGTH_SHORT).show();
                }


            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }

    }

    private void uploadImage(final Uri filePath2) {

        if (filePath2 != null) {
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Uploading");
            progressDialog.show();
            progressDialog.setProgress(0);

            storageReference = FirebaseStorage.getInstance().getReference();

            final StorageReference ref = storageReference.child("images/" + UUID.randomUUID().toString());

            ref.putFile(filePath2).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    progressDialog.dismiss();
                    if (task.isSuccessful()) {

                        Toast.makeText(SignUpActivity.this, "Picture uploaded successfully", Toast.LENGTH_SHORT).show();
                    }

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    progressDialog.dismiss();
                    Toast.makeText(SignUpActivity.this, "Failed", Toast.LENGTH_SHORT).show();
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
                        }
                    });
                }
            });

        } else {
            Toast.makeText(this, "No image selected", Toast.LENGTH_SHORT).show();
        }

    }

}
