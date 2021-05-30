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
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import de.hdodenhof.circleimageview.CircleImageView;

public class Profile extends AppCompatActivity {

    Bitmap selectedImage;
    ImageView profileImage;
    EditText editNick, editPass;
    TextView textNick, textMail, textPass;


    private CircleImageView profileImageView;
    private Button editButton ,saveButton;

    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseStorage firebaseStorage;
    private StorageReference storageReference;
    private FirebaseUser firebaseUser;
    String profileUrl;
    Uri imageData;
    String documentID;
    String userEmail, userPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();

        profileImage = findViewById(R.id.profileImage);
        editNick = findViewById(R.id.editNick);
        editPass = findViewById(R.id.editPass);
        textNick = findViewById(R.id.textNick);
        textMail = findViewById(R.id.textMail);
        textPass = findViewById(R.id.textPass);
        saveButton = findViewById(R.id.saveButton);
        editButton = findViewById(R.id.editButton);

        saveButton = findViewById(R.id.saveButton);
       // saveButton.setOnClickListener();

        firebaseUser = firebaseAuth.getCurrentUser();
        userEmail = firebaseUser.getEmail();

        getProfileData();
    }
    public void getProfileData(){
        firebaseFirestore.collection("Profiles").whereEqualTo("useremail", userEmail).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    for(QueryDocumentSnapshot document : task.getResult()){
                        Map<String, Object> data = document.getData();
                        documentID = document.getId();

                        textMail.setText((String) data.get("useremail"));
                        textPass.setText((String) data.get("userpassword"));
                        textNick.setText((String) data.get("usernick"));

                        if(data.get("profileUrl") != null){
                            Picasso.get().load((String) data.get("profileUrl")).into(profileImage);
                        }
                    }
                }
            }
        });
    }
    public void editSaveButton(View v){
        getProfileData();

        UUID uuid = UUID.randomUUID();
        String profileImageName = "profileimages/" + uuid +".jpg";

        firebaseUser.updatePassword(editPass.getText().toString());

        HashMap<String, Object> profileData = new HashMap<>();

        if(imageData != null) {
            storageReference.child(profileImageName).putFile(imageData).addOnSuccessListener(taskSnapshot -> {
                StorageReference storageReference = firebaseStorage.getInstance().getReference(profileImageName);
                storageReference.getDownloadUrl().addOnSuccessListener(uri -> {
                    profileUrl = uri.toString();

                    profileData.put("profileUrl", profileUrl);
                    profileData.put("usernick", editNick.getText().toString());
                    profileData.put("useremail", textMail.getText().toString());
                    profileData.put("userpassword", editPass.getText().toString());
                    firebaseFirestore.collection("Profiles").document(documentID).set(profileData).addOnSuccessListener(aVoid -> {

                    }).addOnFailureListener(e -> Toast.makeText(Profile.this, e.getLocalizedMessage(), Toast.LENGTH_LONG).show());
                }).addOnFailureListener(e -> Toast.makeText(Profile.this, e.getLocalizedMessage(), Toast.LENGTH_LONG).show());
            }).addOnFailureListener(e -> Toast.makeText(Profile.this, e.getLocalizedMessage(), Toast.LENGTH_LONG).show());
        }else{
            profileData.put("profileUrl", profileUrl);
            profileData.put("usernick", editNick.getText().toString());
            profileData.put("userpassword", editPass.getText().toString());
            firebaseFirestore.collection("Profiles").document(documentID).set(profileData).addOnSuccessListener(aVoid -> {

            }).addOnFailureListener(e -> Toast.makeText(Profile.this, e.getLocalizedMessage(), Toast.LENGTH_LONG).show());
        }
        saveButton.setVisibility(View.INVISIBLE);
        editNick.setVisibility(View.INVISIBLE);
        editPass.setVisibility(View.INVISIBLE);

        editButton.setVisibility(View.VISIBLE);
        textNick.setVisibility(View.VISIBLE);
        textPass.setVisibility(View.VISIBLE);
    }
    public void turnEditButton(View v){
        editButton.setVisibility(View.INVISIBLE);
        textNick.setVisibility(View.INVISIBLE);
        textPass.setVisibility(View.INVISIBLE);

        saveButton.setVisibility(View.VISIBLE);
        editNick.setVisibility(View.VISIBLE);
        editPass.setVisibility(View.VISIBLE);

        editNick.setText(textNick.getText().toString());
        editPass.setText(textPass.getText().toString());
    }

    public void profileImage(View v){
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
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
                if(Build.VERSION.SDK_INT >= 28){
                    ImageDecoder.Source source = ImageDecoder.createSource(this.getContentResolver(), imageData);
                    selectedImage = ImageDecoder.decodeBitmap(source);
                    profileImage.setImageBitmap(selectedImage);
                }else{
                    selectedImage = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageData);
                    profileImage.setImageBitmap(selectedImage);
                }
            }catch (IOException e){
                e.printStackTrace();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}