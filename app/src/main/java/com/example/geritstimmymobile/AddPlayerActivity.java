package com.example.geritstimmymobile;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationManagerCompat;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.Toast;

import com.example.geritstimmymobile.model.Player;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;

// Nog te implementeren
public class AddPlayerActivity extends AppCompatActivity {
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private FirebaseUser user;

    private Button add_button;
    private String name;
    private String lastName;
    private static final String TAG = "AddPlayer :::";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_player);
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.Game:
                try {
                    Intent intent = new Intent(this, GameActivity.class);
                    this.startActivity(intent);
                    return true;
                } catch (Exception e) {
                    Log.e(TAG, "Error while starting GameActivity: " + e.getMessage());

                }
            case R.id.addPlayer:
                try {
                    Intent intent2 = new Intent(this, AddPlayerActivity.class);
                    this.startActivity(intent2);
                    return true;
                } catch (Exception e) {
                    Log.e(TAG, "Error while starting AddPlayerActivity: " + e.getMessage());
                }
            case R.id.Logout:
                if (user != null) {
                    mAuth.signOut();
                    startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                }
            default:
                return super.onOptionsItemSelected(item);
        }
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
