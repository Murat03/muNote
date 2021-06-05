package com.muratipek.munote;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class UserListActivity extends AppCompatActivity {

    String title,note,address,downloadUrl,senderMail,receiverMail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_list);

        Intent intent = getIntent();
        title = intent.getStringExtra("title");
        note = intent.getStringExtra("note");
        address = intent.getStringExtra("address");
        downloadUrl = intent.getStringExtra("downloadUrl");
        System.out.println(address);
        System.out.println(downloadUrl);

        ListView listView = findViewById(R.id.listView);
        ArrayList<String> userMails = new ArrayList<>();

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        senderMail = firebaseUser.getEmail();

        firebaseFirestore.collection("Profiles").whereNotEqualTo("useremail", senderMail).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for(DocumentSnapshot value : queryDocumentSnapshots.getDocuments()){
                    Map<String, Object> data = value.getData();

                    userMails.add((String) data.get("useremail"));

                    ArrayAdapter arrayAdapter = new ArrayAdapter(UserListActivity.this, android.R.layout.simple_list_item_1, userMails);
                    listView.setAdapter(arrayAdapter);
                }
            }
        });

        HashMap<String, Object> noteData = new HashMap<>();
        noteData.put("title", title);
        noteData.put("note", note);
        noteData.put("address", address);
        noteData.put("downloadUrl", downloadUrl);
        noteData.put("senderMail", senderMail);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                noteData.put("receiverMail", userMails.get(position));

                firebaseFirestore.collection("SharedNotes").add(noteData).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        startActivity(new Intent(UserListActivity.this, MainActivity.class));
                    }
                });
            }
        });

    }
}