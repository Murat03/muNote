package com.muratipek.munote;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class NoteActivity extends AppCompatActivity {

    Bitmap selectedImage;
    ImageView selectImage;
    EditText titleText, noteText;
    TextView locationText;
    Button addNoteButton;
    private FirebaseStorage firebaseStorage;
    private StorageReference storageReference;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth firebaseAuth;
    Uri imageData;
    String downloadUrl;
    String selectedTitle, selectedNote, selectedUrl;
    String documentID;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);

        selectImage = findViewById(R.id.selectImage);
        titleText = findViewById(R.id.titleText);
        noteText = findViewById(R.id.noteText);
        locationText = findViewById(R.id.locationText);

        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference();
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();

        Intent intent = getIntent();
        selectedTitle = intent.getStringExtra("title");
        locationText.setText(intent.getStringExtra("address"));

        noteSelected();
    }

    //When Note is Selected
    public void noteSelected(){

        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        String userEmail = firebaseUser.getEmail();

        //from docs
        firebaseFirestore.collection("Notes").whereEqualTo("useremail", userEmail)
                .whereEqualTo("title", selectedTitle).get().addOnCompleteListener(task -> {
                    if(task.isSuccessful()){
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Map<String, Object> data = document.getData();

                            documentID = document.getId();

                            titleText.setText((String) data.get("title"));
                            noteText.setText((String) data.get("note"));
                            locationText.setText((String) data.get("location"));

                            if(data.get("downloadurl") != null){
                                Picasso.get().load((String) data.get("downloadurl")).into(selectImage);
                            }
                        }
                    }
                });
    }

    public void maps(View v){
        Intent intentToMaps = new Intent(NoteActivity.this, MapsActivity.class);
        startActivity(intentToMaps);
    }

    //Button
    public void addNoteButton(View view){

        UUID uuid = UUID.randomUUID();
        String imageName = "images/" + uuid + ".jpg";

        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        String userEmail = firebaseUser.getEmail();
        HashMap<String, Object> noteData = new HashMap<>();
            if (imageData != null) {
                storageReference.child(imageName).putFile(imageData).addOnSuccessListener(taskSnapshot -> {
                    StorageReference storageReference = firebaseStorage.getInstance().getReference(imageName);
                    storageReference.getDownloadUrl().addOnSuccessListener(uri -> {
                        downloadUrl = uri.toString();

                        String title = titleText.getText().toString();
                        String note = noteText.getText().toString();
                        String location = locationText.getText().toString();

                        noteData.put("useremail", userEmail);
                        noteData.put("title", title);
                        noteData.put("note", note);
                        noteData.put("location", location);
                        noteData.put("date", FieldValue.serverTimestamp());
                        noteData.put("downloadurl", downloadUrl);

                        if (selectedTitle != null) {
                            firebaseFirestore.collection("Notes").document(documentID).set(noteData).addOnSuccessListener(documentReference -> {
                                Intent intent = new Intent(NoteActivity.this, MainActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(intent);
                            }).addOnFailureListener(e -> {
                                Toast.makeText(NoteActivity.this, e.getLocalizedMessage().toString(), Toast.LENGTH_LONG).show();
                                System.out.println(e.getLocalizedMessage());
                            });
                        } else{
                            firebaseFirestore.collection("Notes").add(noteData).addOnSuccessListener(documentReference -> {
                                Intent intent = new Intent(NoteActivity.this, MainActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(intent);
                            }).addOnFailureListener(e -> {
                                Toast.makeText(NoteActivity.this, e.getLocalizedMessage().toString(), Toast.LENGTH_LONG).show();
                                System.out.println(e.getLocalizedMessage());
                            });
                    }
                    });
                }).addOnFailureListener(e -> Toast.makeText(NoteActivity.this, e.getLocalizedMessage(), Toast.LENGTH_LONG).show());
            } else {
                String title = titleText.getText().toString();
                String note = noteText.getText().toString();
                String location = locationText.getText().toString();

                noteData.put("useremail", userEmail);
                noteData.put("title", title);
                noteData.put("note", note);
                noteData.put("location", location);
                noteData.put("date", FieldValue.serverTimestamp());

                if(selectedTitle != null){
                    firebaseFirestore.collection("Notes").document(documentID).set(noteData).addOnSuccessListener(aVoid -> {
                        Intent intent = new Intent(NoteActivity.this, MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                    }).addOnFailureListener(e -> {
                        Toast.makeText(NoteActivity.this, e.getLocalizedMessage().toString(), Toast.LENGTH_LONG).show();
                        System.out.println(e.getLocalizedMessage());
                    });
                }else {
                    firebaseFirestore.collection("Notes").add(noteData).addOnSuccessListener(documentReference -> {
                        Intent intent = new Intent(NoteActivity.this, MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                    }).addOnFailureListener(e -> {
                        Toast.makeText(NoteActivity.this, e.getLocalizedMessage().toString(), Toast.LENGTH_LONG).show();
                        System.out.println(e.getLocalizedMessage());
                    });
                }
            }
    }

    public void selectImage(View view){
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
        }else{
            Intent intentToGallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intentToGallery, 2);
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == 1){
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                Intent intentToGallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intentToGallery, 2);
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(requestCode == 2 && resultCode == RESULT_OK && data != null){
            imageData = data.getData();
            try {
                if(Build.VERSION.SDK_INT >=28){
                    ImageDecoder.Source source = ImageDecoder.createSource(this.getContentResolver(), imageData);
                    selectedImage = ImageDecoder.decodeBitmap(source);
                    selectImage.setImageBitmap(selectedImage);
                }else{
                    selectedImage = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageData);
                    selectImage.setImageBitmap(selectedImage);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}