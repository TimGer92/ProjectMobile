package com.example.geritstimmymobile;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NavUtils;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.IOException;

/**
 * An activity representing a single Player detail screen. This
 * activity is only used on narrow width devices. On tablet-size devices,
 * item details are presented side-by-side with a list of items
 * in a {@link PlayerListActivity}.
 */
public class PlayerDetailActivity extends AppCompatActivity {
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    TextView tvFirstName, tvLastName, tvGender;
    Button btnUpdate, btnDelete;
    String playerId, firstName, lastName, gender;
    ImageView playerIcon;
    Uri filePath;
    StorageReference storageReference;

    static final int PICK_IMAGE_REQUEST = 124;

    private static final String TAG = "DetailActivity :::";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player_detail);
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        db = FirebaseFirestore.getInstance();

        Log.i(TAG, "Views created successfully");

        // Als de oriëntatie van deze Activity wijzigt naar LANDSCAPE, wordt de gebruiker naar PlayerListActivity gestuurd
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            Log.i(TAG, "Activity put in landscape");
            Intent intentToPlayerListActivity = new Intent(PlayerDetailActivity.this, PlayerListActivity.class);
            startActivity(intentToPlayerListActivity);
        }

        init();
        Log.i(TAG, "Initialized successfully");

        fillFieldsWithData();
        Log.i(TAG, "Fields filled successfully");

        playerIcon.setOnClickListener(v -> {
            showFileChooser();
        });

        btnUpdate.setOnClickListener(v -> {
            Log.i(TAG, "Update_button clicked");
            updatePlayer(v);
        });

        btnDelete.setOnClickListener(v -> {
            Log.i(TAG, "Delete_button clicked");
            createDeleteDialog(playerId);
        });
    }

    private void init() {
        tvFirstName = findViewById(R.id.firstname_database);
        tvLastName = findViewById(R.id.lastname_database);
        tvGender = findViewById(R.id.gender_database);
        btnUpdate = findViewById(R.id.btnUpdate);
        btnDelete = findViewById(R.id.btnDelete);
        playerIcon = findViewById(R.id.ivProfilePicture);
        storageReference = FirebaseStorage.getInstance().getReference();
    }

    private void updatePlayer(View view) {
        Intent intent = new Intent(PlayerDetailActivity.this, AddPlayerActivity.class);
        intent.putExtra("playerId", playerId);
        intent.putExtra("firstname", firstName);
        intent.putExtra("lastname", lastName);
        intent.putExtra("gender", gender);

        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        // Anders kan app crashen
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
            case R.id.addPlayer:
                try {
                    Intent intent = new Intent(this, AddPlayerActivity.class);
                    this.startActivity(intent);
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

    private void fillFieldsWithData() {
        // Data verzamelen die aan de Intent naar de huidige Activity (vanuit PlayerListActivity) is meegegeven
        playerId = getIntent().getStringExtra("playerId");
        firstName = getIntent().getStringExtra("firstname");
        lastName = getIntent().getStringExtra("lastname");
        gender = getIntent().getStringExtra("gender");

        String name = firstName + " " + lastName;
        setTitle(name);

        tvFirstName.setText(firstName);
        tvLastName.setText(lastName);
        tvGender.setText(gender);

        // Foto tonen in de ImageView
        fillImageView();
    }

    private void fillImageView() {
        switch(gender) {
            case "Female":
                playerIcon.setImageResource(R.drawable.lilana);
                return;
            case "Male":
                playerIcon.setImageResource(R.drawable.jace);
                return;
            default:
                playerIcon.setImageResource(R.drawable.mtg);
                return;
        }
//        String imageName = playerId;
//
//        if (imageName != null) {
//            StorageReference storageRef = storageReference.child(imageName);
//
//            final long ONE_MEGABYTE = 1024 * 1024;
//            storageRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(bytes -> {
//                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
//                playerIcon.setImageBitmap(bitmap);
//            });
//        }
    }

    private void showFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Choose image"), PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            filePath = data.getData();
            uploadPicture();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                playerIcon.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void uploadPicture() {
        if (filePath != null) {
            String pictureName = playerId;
            storageReference = storageReference.child(pictureName);
            storageReference.putFile(filePath).addOnSuccessListener(taskSnapshot -> Log.i(TAG, "Image uploaded to Firebase Storage successfully")).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(PlayerDetailActivity.this, "Something went wrong uploading image Firebase Storage", Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "Something went wrong uploading image to Firebase Storage");
                }
            });
        }
    }

    private void createDeleteDialog(String playerId) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setMessage("Are you sure to delete " + firstName + "?");
        alertDialog.setCancelable(false);

        alertDialog.setPositiveButton("Yes", (dialog, which) -> {
            Log.i(TAG, "Delete player confirmed");

            deletePlayer(playerId);
        });

        alertDialog.setNegativeButton("No", (dialog, which) -> Log.i(TAG, "Delete player denied"));

        alertDialog.create().show();
    }

    private void deletePlayer(String playerId) {
        db.collection("players").document(playerId).delete()
                .addOnSuccessListener(documentReference -> {
                    Log.d(TAG, "Player has been deleted");
                    Toast.makeText(PlayerDetailActivity.this, "Player has been deleted", Toast.LENGTH_LONG).show();
                    Intent intentToDetailsActivity = new Intent(PlayerDetailActivity.this, PlayerListActivity.class);
                    startActivity(intentToDetailsActivity);
                });
    }
}
