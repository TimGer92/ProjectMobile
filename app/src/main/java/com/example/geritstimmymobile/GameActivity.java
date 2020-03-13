package com.example.geritstimmymobile;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.geritstimmymobile.model.Game;
import com.example.geritstimmymobile.model.Player;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class GameActivity extends AppCompatActivity {
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    public int notifyId = 1;
    public String channelId = "some_channel_id";
    private ImageButton cmdPlus1, cmdPlus2, cmdMin1, cmdMin2, btnReset, btnPlayers;
    private TextView lifePlayer1, lifePlayer2, namePlayer1, namePlayer2;
    private Game game;

    private static final String TAG = "GameActivity :::";

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        game = new Game();
        init();
        setValues(new Game());

        cmdPlus1.setOnClickListener(v -> {
            int life = Integer.parseInt(lifePlayer1.getText().toString());
            int total = life + 1;
            lifePlayer1.setText(Integer.toString(total));
            Log.i(TAG, "Set life total of player to: " + total);
        });

        cmdPlus2.setOnClickListener(v -> {
            int life = Integer.parseInt(lifePlayer2.getText().toString());
            int total = life + 1;
            lifePlayer2.setText(Integer.toString(total));
            Log.i(TAG, "Set life total of player to: " + total);
        });

        cmdMin1.setOnClickListener(v -> {
            int life = Integer.parseInt(lifePlayer1.getText().toString());
            int total = life - 1;
            lifePlayer1.setText(Integer.toString(total));
            Log.i(TAG, "Set life total of player to: " + total);
        });

        cmdMin2.setOnClickListener(v -> {
            int life = Integer.parseInt(lifePlayer2.getText().toString());
            int total = life - 1;
            lifePlayer2.setText(Integer.toString(total));
            Log.i(TAG, "Set life total of player to: " + total);
        });

        btnReset.setOnClickListener(v -> {
            createDialog();
        });

        btnPlayers.setOnClickListener(view -> {
            Log.i(TAG, "SetPlayersButton clicked");
        });
    }

    public void init() {
        cmdPlus1 = findViewById(R.id.cmdPlusGuest);
        cmdPlus2 = findViewById(R.id.cmdPlusHome);
        cmdMin1 = findViewById(R.id.cmdMinusGuest);
        cmdMin2 = findViewById(R.id.cmdMinusHome);
        btnReset = findViewById(R.id.cmdResetLP);
        btnPlayers = findViewById(R.id.cmdSetPlayers);
        lifePlayer1 = findViewById(R.id.txtLifeCount2p1);
        lifePlayer2 = findViewById(R.id.txtLifeCount2p2);
        namePlayer1 = findViewById(R.id.namePlayer1);
        namePlayer2 = findViewById(R.id.namePlayer2);
    }

    public void setValues(Game game) {
        Log.i(TAG, game.toString());
        lifePlayer1.setText(game.getLifeCount1());
        lifePlayer2.setText(game.getLifeCount2());
        namePlayer1.setText(game.getNamePlayer1());
        namePlayer2.setText(game.getNamePlayer2());

    }

    public void createDialog() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setMessage("Reset the game?");
        alertDialog.setCancelable(false);

        alertDialog.setPositiveButton("Yes", (dialog, which) -> {
            Log.i(TAG, "Reset game confirmed");
            Game newGame = new Game();
            newGame.setNamePlayer1(namePlayer1.getText().toString());
            newGame.setNamePlayer2(namePlayer2.getText().toString());
            setValues(newGame);
        });

        alertDialog.setNegativeButton("No", (dialog, which) -> Log.i(TAG, "Reset game denied"));
        alertDialog.create().show();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        // Anders kan app crashen
        try {
            menu.findItem(R.id.Game).setVisible(false);
        } catch (Exception e) {
            Log.e(TAG, "MenuItem not found");
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.addPlayer:
                try {
                    Intent intent = new Intent(this, AddPlayerActivity.class);
                    this.startActivity(intent);
                    return true;
                } catch (Exception e) {
                    Log.e(TAG, "Error while starting AddPlayerActivity: " + e.getMessage());
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
    private void showNotification() {
        Intent notifyIntent = new Intent(GameActivity.this, PlayerListActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(GameActivity.this, 45648, notifyIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        NotificationChannel mChannel = new NotificationChannel(
                channelId, "Test", NotificationManager.IMPORTANCE_HIGH);
        notificationManager.createNotificationChannel(mChannel);


        Notification notification = new Notification.Builder(GameActivity.this)
                .setContentTitle("Game")
                .setContentText("Game finished!")
                .setSmallIcon(R.mipmap.ic_launcher_round)
                .setChannelId(channelId)
                .setContentIntent(pendingIntent)
                .build();

        notificationManager.notify(notifyId, notification);
    }
}
