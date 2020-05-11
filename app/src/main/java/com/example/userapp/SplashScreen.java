package com.example.userapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SplashScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                sendToMainActicity();
            }
        }, 1000);

    }


    private void sendToMainActicity() {
//        mProgress.dismiss();
        DatabaseReference mRootRef ;
        mRootRef = FirebaseDatabase.getInstance().getReference().child("Users");
        if(FirebaseAuth.getInstance().getCurrentUser()!=null)
        {
            mRootRef.child(FirebaseAuth.getInstance().getUid()).child("notificationId").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if(dataSnapshot.exists())
                    {
                        startActivity(new Intent(SplashScreen.this, MainActivity.class));
                        finish();

                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
        else{
            startActivity(new Intent(SplashScreen.this, LoginActivity.class));
            finish();
        }


    }
}


