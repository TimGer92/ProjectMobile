package com.example.geritstimmymobile;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.geritstimmymobile.model.Player;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class MainActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private EditText password;
    private EditText email;
    private Button register_button;
    private Button login_button;
    private static final String TAG = "MainActivity :::";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAuth = FirebaseAuth.getInstance();
        email = findViewById(R.id.signup_email_input);
        password = findViewById(R.id.signup_password_input);
        register_button = findViewById(R.id.button_register);
        login_button = findViewById(R.id.button_login);

        register_button.setOnClickListener(v -> {
            if (v == register_button) {
                Log.d(TAG, "RegisterButton clicked");
                registerUser();
            }
        });

        login_button.setOnClickListener(v -> {
            if (v == login_button) {
                Log.d(TAG, "Login button clicked");
                startActivity(new Intent(getApplicationContext(),
                        LoginActivity.class));
            }
        });

        Log.d(TAG, "onCreate ::: rendered");
    }

    public void registerUser() {
        String register_email = email.getText().toString().trim();
        String register_password = password.getText().toString().trim();

        if (TextUtils.isEmpty(register_email)) {
            Toast.makeText(this, "email is required", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(register_password) || register_password.length() < 6) {
            Toast.makeText(this, "password is required or is to short", Toast.LENGTH_SHORT).show();
            return;
        }
        mAuth.createUserWithEmailAndPassword(register_email, register_password)
                .addOnCompleteListener(this, task -> {
                    try {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "Registration is succesfull");
                            Toast.makeText(MainActivity.this, "registration successful",
                                    Toast.LENGTH_SHORT).show();
                            finish();
//                                startActivity(new Intent(getApplicationContext(), GameActivity.class));
//                            gameActivity nog toevoegen
                        } else {
                            Toast.makeText(MainActivity.this, "Couldn't register, try again",
                                    Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Received an exception after trying to register:  " + e.getMessage());
                    }
                });
    }

//        Player player = new Player("doe rustig", "eric");
//        Player player2 = new Player("Rustige", "Eric");
//        db.collection("players")
//                .add(player)
//                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
//                    @Override
//                    public void onSuccess(DocumentReference documentReference) {
//                        Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());
//                    }
//                })
//                .addOnFailureListener(new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception e) {
//                        Log.w(TAG, "Error adding document", e);
//                    }
//                });
//
//        db.collection("players")
//                .add(player2)
//                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
//                    @Override
//                    public void onSuccess(DocumentReference documentReference) {
//                        Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());
//                    }
//                })
//                .addOnFailureListener(new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception e) {
//                        Log.w(TAG, "Error adding document", e);
//                    }
//                });
}
