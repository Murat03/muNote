package com.muratipek.munote;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;
    ArrayList<String> titleFromFB;
    ArrayList<String> noteFromFB;
    ArrayList<String> imageFromFB;
    RecyclerView recyclerView;
    RecyclerViewAdapter recyclerViewAdapter;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        //Inflater
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.note_options_menu, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if(item.getItemId() == R.id.upload){
            Intent intentToUpload = new Intent(MainActivity.this, NoteActivity.class);
            startActivity(intentToUpload);
        }else if(item.getItemId() == R.id.chronometer){
            Intent intentToChronometer = new Intent(MainActivity.this, ChronometerActivity.class);
            startActivity(intentToChronometer);
        }else if (item.getItemId() == R.id.signout){
            firebaseAuth.signOut();
            Intent intentToSignInUp = new Intent(MainActivity.this, SignInUpActivity.class);
            startActivity(intentToSignInUp);
            finish();
        }else if(item.getItemId() == R.id.profile){
            Intent intentToProfile = new Intent(MainActivity.this, Profile.class);
            startActivity(intentToProfile);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        titleFromFB = new ArrayList<>();
        noteFromFB = new ArrayList<>();
        imageFromFB = new ArrayList<>();

        recyclerView = findViewById(R.id.recyclerView);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        getDataFromFirestore();
    }

    public void getDataFromFirestore(){

        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        String userEmail = firebaseUser.getEmail();

        firebaseFirestore.collection("Notes").whereEqualTo("useremail", userEmail).orderBy("date", Query.Direction.DESCENDING)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if(error != null){
                    Toast.makeText(MainActivity.this, error.getLocalizedMessage().toString(), Toast.LENGTH_LONG).show();
                    System.out.println(error.getLocalizedMessage().toString());
                }
                if(value != null){
                    for(DocumentSnapshot snapshot : value.getDocuments()){
                        Map<String, Object> data = snapshot.getData();

                        String title = (String) data.get("title");
                        String note = (String) data.get("note");
                        String downloadUrl = (String) data.get("downloadurl");

                        titleFromFB.add(title);
                        noteFromFB.add(note);
                        imageFromFB.add(downloadUrl);

                        //RecyclerView
                        recyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this));
                        recyclerViewAdapter = new RecyclerViewAdapter(titleFromFB);
                        recyclerView.setAdapter(recyclerViewAdapter);

                    }
                }
            }
        });
    }
}