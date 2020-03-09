package com.example.geritstimmymobile;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationManagerCompat;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import com.example.geritstimmymobile.model.Player;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;

// Nog te implementeren
public class AddPlayerActivity extends AppCompatActivity {
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    private Button add_button;
    private String name;
    private String lastName;

    private static final String TAG = "AddPlayer :::";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_player);
        mAuth = FirebaseAuth.getInstance();
//        userId = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();
        add_button = findViewById(R.id.add_player);
        db = FirebaseFirestore.getInstance();

        // Notificatie tonen wanneer speler is toegevoegd
        add_button.setOnClickListener(v -> {
            name = "Test-Naam";
            lastName = "Test-achternaam";
            if (v == add_button) {
                addPlayer(name, lastName);
            }
        });
    }

    public void addPlayer(String name, String lastName) {
        CollectionReference dbPlayers = db.collection("players");
        Player newPlayer = new Player(name, lastName);
        dbPlayers.add(newPlayer).addOnSuccessListener(documentReference -> {
            Log.d(TAG, "Player has been added");
            Toast.makeText(AddPlayerActivity.this, "Player has been added", Toast.LENGTH_SHORT).show();
        });
    }
}
