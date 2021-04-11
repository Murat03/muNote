package com.muratipek.munote;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.note_options_menu, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if(item.getItemId() == R.id.upload){
            Intent intentToUpload = new Intent(MainActivity.this, UploadActivity.class);
            startActivity(intentToUpload);
        }else if(item.getItemId() == R.id.chronometer){
            Intent intentToChronometer = new Intent(MainActivity.this, ChronometerActivity.class);
            startActivity(intentToChronometer);
        }else if (item.getItemId() == R.id.signout){
            firebaseAuth.signOut();
            Intent intentToSignInUp = new Intent(MainActivity.this, SignInUpActivity.class);
            startActivity(intentToSignInUp);
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        firebaseAuth = FirebaseAuth.getInstance();
    }
}