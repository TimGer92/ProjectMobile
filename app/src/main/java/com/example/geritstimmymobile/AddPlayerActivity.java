package com.example.geritstimmymobile;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.geritstimmymobile.model.Player;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class AddPlayerActivity extends AppCompatActivity {
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private FirebaseUser user;

    private EditText editFirstName;
    private EditText editLastName;
    private EditText editGender;

    private Button add_button;
    private String firstName, lastName, gender, playerId;
    private Player player;
    public int notifyId = 1;
    public String channelId = "some_channel_id";
    private static final String TAG = "AddPlayer :::";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_player);
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        setTitle("Add Player");

        editFirstName = findViewById(R.id.firstname);
        editLastName = findViewById(R.id.lastname);
        editGender = findViewById(R.id.gender);
        add_button = findViewById(R.id.add_player);

        playerId = getIntent().getStringExtra("playerId");
        firstName = getIntent().getStringExtra("firstname");
        lastName = getIntent().getStringExtra("lastname");
        gender = getIntent().getStringExtra("gender");

        editFirstName.setText(firstName);
        editLastName.setText(lastName);
        editGender.setText(gender);

        Log.d(TAG, "OnCreate: AddPlayerActivity rendered successfully");

        // Notificatie tonen wanneer speler is toegevoegd
        add_button.setOnClickListener(v -> {
            if (v == add_button) {
                firstName = editFirstName.getText().toString().trim();
                lastName = editLastName.getText().toString().trim();
                gender = editGender.getText().toString().trim();
                savePlayer(firstName, lastName, gender);
                // hier notificatie toevoegen
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        // Anders kan app crashen
        try {
            menu.findItem(R.id.addPlayer).setVisible(false);
        } catch (Exception e) {
            Log.e(TAG, "MenuItem not found");
        }
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
            case R.id.playerList:
                try {
                    Intent intent = new Intent(this, PlayerListActivity.class);
                    this.startActivity(intent);
                    return true;
                } catch (Exception e) {
                    Log.e(TAG, "Error while starting PlayerListActivity: " + e.getMessage());
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

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void savePlayer(String name, String lastName, String gender) {
        CollectionReference dbPlayers = db.collection("players");
        String id = dbPlayers.document().getId();
        if (playerId != null) {
            player = new Player(playerId, name, lastName, gender);
            dbPlayers.document(playerId).set(player).addOnSuccessListener(documentReference -> {
                Log.d(TAG, "Player has been updated");
                Toast.makeText(AddPlayerActivity.this, "Player has been saved", Toast.LENGTH_SHORT).show();
//                Intent intentToDetailsActivity = new Intent(AddPlayerActivity.this, PlayerListActivity.class);
//                startActivity(intentToDetailsActivity);
                showNotification();
            });
        }
        else {
            player = new Player(id, name,lastName, gender);
            dbPlayers.document(id).set(player).addOnSuccessListener(documentReference -> {
                Log.d(TAG, "Player has been created");
                Toast.makeText(AddPlayerActivity.this, "Player has been saved", Toast.LENGTH_SHORT).show();
//                Intent intentToDetailsActivity = new Intent(AddPlayerActivity.this, PlayerListActivity.class);
//                startActivity(intentToDetailsActivity);
                  showNotification();
            });
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void showNotification() {
        Intent notifyIntent = new Intent(AddPlayerActivity.this, PlayerListActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(AddPlayerActivity.this, 45648, notifyIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        NotificationChannel mChannel = new NotificationChannel(
                channelId, "Test", NotificationManager.IMPORTANCE_HIGH);
        notificationManager.createNotificationChannel(mChannel);


        Notification notification = new Notification.Builder(AddPlayerActivity.this)
                .setContentTitle("Player")
                .setContentText("Player has been added!")
                .setSmallIcon(R.mipmap.ic_launcher_round)
                .setChannelId(channelId)
                .setContentIntent(pendingIntent)
                .build();

        notificationManager.notify(notifyId, notification);
    }
}
