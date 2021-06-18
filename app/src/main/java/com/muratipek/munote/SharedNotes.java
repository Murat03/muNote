package com.muratipek.munote;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;

public class SharedNotes extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shared_notes);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore= FirebaseFirestore.getInstance();

        ListView listView = findViewById(R.id.sharedList);
        ArrayList<String> senderMail = new ArrayList<>();
        ArrayList<String> receiverMail = new ArrayList<>();
        ArrayList<String> title = new ArrayList<>();
        ArrayList<String> listName = new ArrayList<>();

        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        String userEmail = firebaseUser.getEmail();

        firebaseFirestore.collection("SharedNotes").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for(DocumentSnapshot snapshot : queryDocumentSnapshots.getDocuments()){
                    Map<String, Object> data = snapshot.getData();

                    title.add((String) data.get("title"));
                    listName.add("Sender : " + (String) data.get("senderMail") + "\nReceiver : " + (String) data.get("receiverMail"));

                    ArrayAdapter arrayAdapter = new ArrayAdapter(SharedNotes.this, android.R.layout.simple_list_item_1, listName);
                    listView.setAdapter(arrayAdapter);
                }
            }
        }).addOnFailureListener(e -> Toast.makeText(SharedNotes.this, e.getLocalizedMessage(), Toast.LENGTH_LONG).show());

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                System.out.println(title.get(position));
                Intent intent = new Intent(SharedNotes.this, NoteActivity.class);
                intent.putExtra("sharedtitle", title.get(position));
                startActivity(intent);
                finish();
            }
        });
    }
}