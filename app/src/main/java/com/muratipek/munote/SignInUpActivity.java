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

public class SignInUpActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    EditText emailText, passwordText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signinup);

        firebaseAuth = FirebaseAuth.getInstance();
        emailText = findViewById(R.id.emailText);
        passwordText = findViewById(R.id.passwordText);

        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();

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
            firebaseAuth.signInWithEmailAndPassword(email, password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                @Override
                public void onSuccess(AuthResult authResult) {
                    Intent intent = new Intent(SignInUpActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(SignInUpActivity.this, e.getLocalizedMessage().toString(), Toast.LENGTH_LONG).show();
                }
            });
    }
    }
    public void signUpClicked(View view){

        String email = emailText.getText().toString();
        String password = passwordText.getText().toString();

        if(email.matches("")){
            Toast.makeText(SignInUpActivity.this, "Please Enter An Email Address", Toast.LENGTH_LONG).show();
        }else if(password.matches("")){
            Toast.makeText(SignInUpActivity.this, "Please Enter A Password", Toast.LENGTH_LONG).show();
        }else{
            firebaseAuth.createUserWithEmailAndPassword(email, password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                @Override
                public void onSuccess(AuthResult authResult) {
                    Toast.makeText(SignInUpActivity.this, "User Created", Toast.LENGTH_LONG).show();

                    Intent intent = new Intent(SignInUpActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(SignInUpActivity.this, e.getLocalizedMessage().toString(), Toast.LENGTH_LONG).show();
                }
            });
        }

    }
}