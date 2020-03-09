package com.example.geritstimmymobile;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {
    private EditText email;
    private EditText password;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private Button loginButton;
    private static final String TAG = "LoginActivity :::";
    private Button reset;
    SharedPreferences preferences;
    SharedPreferences.Editor editor;
    CheckBox saveCredentials;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        email = findViewById(R.id.login_email_input);
        password = findViewById(R.id.login_password_input);
        saveCredentials = findViewById(R.id.RememberLogin);
        reset = findViewById(R.id.resetPw);
        saveCredentials.setChecked(true);
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        loginButton = findViewById(R.id.login);
        Log.d(TAG, "onCreate::: rendered");

        preferences = getSharedPreferences("myApp", Context.MODE_PRIVATE);
        editor = preferences.edit();
        String preferredEmail = preferences.getString(getString(R.string.preferencesEmail), "");
        String preferredPassword = preferences.getString(getString(R.string.preferencesPassword), "");
        email.setText(preferredEmail);
        password.setText(preferredPassword);

        reset.setOnClickListener(v -> {
            FirebaseAuth auth = FirebaseAuth.getInstance();

            auth.sendPasswordResetEmail(preferredEmail)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "Email was sent.");
                            Toast.makeText(LoginActivity.this, "Reset link is send to your e-mail, also check SPAM folder.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    });

        });
        loginButton.setOnClickListener(v -> {
            if (v == loginButton) {
                Log.d(TAG, "Login button was clicked");
                    loginUser();
            }
        });
    }

    public void loginUser() {
        String loginEmail = email.getText().toString().trim();
        String loginPassword = password.getText().toString().trim();
        mAuth.signInWithEmailAndPassword(loginEmail, loginPassword)
                .addOnCompleteListener(this, task -> {
                    try {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "Login is succesful");

                            if (saveCredentials.isChecked()) {
                                editor.putString(getString(R.string.preferencesEmail), loginEmail);
                                editor.commit();
                                editor.putString(getString(R.string.preferencesPassword), loginPassword);
                                editor.commit();
                            } else {
                                editor.putString(getString(R.string.preferencesEmail), "");
                                editor.commit();
                                editor.putString(getString(R.string.preferencesPassword), "");
                                editor.commit();
                            }

                            Log.i(TAG, "Shared Preferences saved");

                            currentUser = mAuth.getCurrentUser();
                            finish();
//                            startActivity(new Intent(getApplicationContext(),
//                                    ProfileActivity.class));
//                            GameActivity nog toevoegen
                        } else {
                            Toast.makeText(LoginActivity.this, "login failed",
                                    Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Exception was thrown when trying to login:  " + e.getMessage());
                    }

                });
    }
}
