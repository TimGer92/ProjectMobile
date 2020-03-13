package com.example.geritstimmymobile;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.geritstimmymobile.model.Player;
import com.google.firebase.firestore.FirebaseFirestore;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PlayerDetailFragment} factory method to
 * create an instance of this fragment.
 */
public class PlayerDetailFragment extends Fragment {
    private Player player;
    private TextView tvFirstname, tvLastname, tvGender;
    private Button btnUpdate, btnDelete;
    private ImageView playerIcon;
    private FirebaseFirestore db;

    private static final String TAG = "PlayerDetailFragment";

    public PlayerDetailFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = FirebaseFirestore.getInstance();

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.player_detail, container, false);

        init(rootView);
        Log.i(TAG, "Initialized successfully");

        initializePlayer();
        Log.i(TAG, "Player initialized successfully");

        if (player != null) {
            fillFieldsWithData();
            Log.i(TAG, "Fields filled successfully");
        }

        btnUpdate.setOnClickListener(v -> {
            Log.i(TAG, "Update-button clicked");
            updatePlayer(v);
        });
        btnDelete.setOnClickListener(v -> {
            Log.i(TAG, "Delete-button clicked");
            createDeleteDialog(player.getPlayerId());
        });

        return rootView;
    }

    private void init(View view) {
        tvFirstname = view.findViewById(R.id.firstname_database);
        tvLastname = view.findViewById(R.id.lastname_database);
        tvGender = view.findViewById(R.id.gender_database);
        btnUpdate = view.findViewById(R.id.btnUpdate);
        btnDelete = view.findViewById(R.id.btnDelete);
        playerIcon = view.findViewById(R.id.ivProfilePicture);
    }

    private void initializePlayer() {
        player = new Player();

        // Player initializeren met gegevens afkomstig van MemberListActivity
        player.setPlayerId(getArguments().getString("playerId"));
        player.setFirstname(getArguments().getString("firstname"));
        player.setLastname(getArguments().getString("lastname"));
        player.setGender(getArguments().getString("gender"));
    }

    private void fillFieldsWithData() {
        // Velden vullen met de (geïnitialiseerde) waarden van de aangemaakte player hier
        tvFirstname.setText(player.getFirstname());
        tvLastname.setText(player.getLastname());
        tvGender.setText(player.getGender());

        fillImageView();
    }

    private void fillImageView() {
        String gender = player.getGender();
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
    }

    private void updatePlayer(View v) {
        Intent intent = new Intent(this.getContext(), AddPlayerActivity.class);

        intent.putExtra("playerId", player.getPlayerId());
        intent.putExtra("firstname", player.getFirstname());
        intent.putExtra("lastname", player.getLastname());
        intent.putExtra("gender", player.getGender());

        startActivity(intent);
    }

    private void createDeleteDialog(String playerId) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getContext());
        alertDialog.setMessage("Are you sure to delete " + player.getFirstname() + "?");
        alertDialog.setCancelable(false);

        alertDialog.setPositiveButton("Yes", (dialog, which) -> {
            Log.i(TAG, "Delete player confirmed");

            deletePlayer(playerId);
        });

        alertDialog.setNegativeButton("No", (dialog, which) -> Log.i(TAG, "Delete player denied"));

        alertDialog.create().show();
    }

    private void deletePlayer(String playerId) {
        Log.i(TAG, playerId);
        db.collection("players").document(playerId).delete()
                .addOnSuccessListener(documentReference -> {
                    Log.d(TAG, "Player has been deleted");
                    Toast.makeText(getContext(), "Player has been deleted", Toast.LENGTH_LONG).show();
                    Intent intentToDetailsActivity = new Intent(getContext(), PlayerListActivity.class);
                    startActivity(intentToDetailsActivity);
                });
    }
}
