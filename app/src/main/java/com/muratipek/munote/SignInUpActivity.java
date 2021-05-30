package com.muratipek.munote;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;

public class SignInUpActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;
    EditText emailText, passwordText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signinup);

        firebaseAuth = FirebaseAuth.getInstance();
        emailText = findViewById(R.id.emailText);
        passwordText = findViewById(R.id.passwordText);

        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        firebaseFirestore = FirebaseFirestore.getInstance();

        if(firebaseUser != null){
            Intent intent = new Intent(SignInUpActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }
    }

    public void signInClicked(View view) {
        String email = emailText.getText().toString();
        String password = passwordText.getText().toString();

        if (email.matches("")) {
            Toast.makeText(SignInUpActivity.this, "Please Enter An Email Address", Toast.LENGTH_LONG).show();
        } else if (password.matches("")) {
            Toast.makeText(SignInUpActivity.this, "Please Enter A Password", Toast.LENGTH_LONG).show();
        } else{
            firebaseAuth.signInWithEmailAndPassword(email, password).addOnSuccessListener(authResult -> {
                Intent intent = new Intent(SignInUpActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }).addOnFailureListener(e -> Toast.makeText(SignInUpActivity.this, e.getLocalizedMessage(), Toast.LENGTH_LONG).show());
    }
    }
    public void signUpClicked(View view){

        String email = emailText.getText().toString();
        String password = passwordText.getText().toString();

        HashMap<String, Object> profileData = new HashMap<>();

        if(email.matches("")){
            Toast.makeText(SignInUpActivity.this, "Please Enter An Email Address", Toast.LENGTH_LONG).show();
        }else if(password.matches("")){
            Toast.makeText(SignInUpActivity.this, "Please Enter A Password", Toast.LENGTH_LONG).show();
        }else{
            firebaseAuth.createUserWithEmailAndPassword(email, password).addOnSuccessListener(authResult -> {
                Toast.makeText(SignInUpActivity.this, "User Created", Toast.LENGTH_LONG).show();

                profileData.put("useremail", email);
                profileData.put("userpassword", password);

                firebaseFirestore.collection("Profiles").add(profileData).addOnSuccessListener(documentReference -> {
                    Intent intent = new Intent(SignInUpActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }).addOnFailureListener(e -> Toast.makeText(SignInUpActivity.this, e.getLocalizedMessage(), Toast.LENGTH_LONG).show());
            }).addOnFailureListener(e -> Toast.makeText(SignInUpActivity.this, e.getLocalizedMessage(), Toast.LENGTH_LONG).show());
        }

    }
}