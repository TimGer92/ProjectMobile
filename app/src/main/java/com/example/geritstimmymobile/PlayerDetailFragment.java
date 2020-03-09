package com.example.geritstimmymobile;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.geritstimmymobile.model.Player;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PlayerDetailFragment} factory method to
 * create an instance of this fragment.
 */
public class PlayerDetailFragment extends Fragment {
    private Player player;
    private TextView tvFirstname, tvLastname, tvBirthdate, tvAddress;
    private Button btnUpdate, btnDelete;
    private static final String TAG =  "PlayerDetailFragment";

    public PlayerDetailFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.player_detail, container, false);

        init(rootView);
        Log.i(TAG, "Initialized successfully");

        initializeMember();
        Log.i(TAG, "Player initialized successfully");

        if (player != null) {
            fillFieldsWithData();
            Log.i(TAG, "Fields filled successfully");
        }

//        btnUpdate.setOnClickListener(this);
//        btnDelete.setOnClickListener(this);

        return rootView;
    }

    private void init(View view) {
        tvFirstname = view.findViewById(R.id.firstname_database);
        tvLastname = view.findViewById(R.id.lastname_database);
        tvBirthdate = view.findViewById(R.id.birthdate_database);
        tvAddress = view.findViewById(R.id.address_database);
    }

    private void initializeMember() {
        player = new Player();

        // Member initializeren met gegevens afkomstig van MemberListActivity
        player.setPlayerId(getArguments().getString("playerId"));
        player.setFirstname(getArguments().getString("firstname"));
        player.setLastname(getArguments().getString("lastname"));
        player.setBirthdate(getArguments().getString("birthdate"));
        player.setAddress(getArguments().getString("address"));
    }

    private void fillFieldsWithData() {
        // Velden vullen met de (geïnitialiseerde) waarden van de aangemaakte member hier
        tvFirstname.setText(player.getFirstname());
        tvLastname.setText(player.getLastname());
        tvBirthdate.setText(player.getBirthdate());
        tvAddress.setText(player.getAddress());

//        fillImageView();
    }
}
