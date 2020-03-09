package com.example.geritstimmymobile;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NavUtils;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;

import com.example.geritstimmymobile.model.Player;

/**
 * An activity representing a single Player detail screen. This
 * activity is only used on narrow width devices. On tablet-size devices,
 * item details are presented side-by-side with a list of items
 * in a {@link PlayerListActivity}.
 */
public class PlayerDetailActivity extends AppCompatActivity {

    TextView tvBirthdate, tvAddress;
    Button btnUpdate, btnDelete;
    String playerId, firstname, lastname, birthdate, address;
    Player player;
    //    ImageView ivProfilePicture;
    private static final String TAG = "DetailActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player_detail);
        Log.i(TAG, "Views created successfully");

        // Als de oriëntatie van deze Activity wijzigt naar LANDSCAPE, wordt de gebruiker naar MemberListActivity gestuurd
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            Log.i(TAG, "Activity put in landscape");
            Intent intentToPlayerListActivity = new Intent(PlayerDetailActivity.this, PlayerListActivity.class);
            startActivity(intentToPlayerListActivity);
        }

        init();
        Log.i(TAG, "Initialized successfully");

        fillFieldsWithData();
        Log.i(TAG, "Fields filled successfully");

//        btnUpdate.setOnClickListener(this);
//        btnDelete.setOnClickListener(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        Log.i(TAG, "");
        if (id == android.R.id.home) {
            // This ID represents the Home or Up button. In the case of this
            // activity, the Up button is shown. Use NavUtils to allow users
            // to navigate up one level in the application structure. For
            // more details, see the Navigation pattern on Android Design:
            //
            // http://developer.android.com/design/patterns/navigation.html#up-vs-back
            //
            NavUtils.navigateUpTo(this, new Intent(this, PlayerListActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void init() {
        player = new Player();
        tvBirthdate = findViewById(R.id.birthdate_database);
        tvAddress = findViewById(R.id.address_database);
        btnUpdate = findViewById(R.id.btnUpdate);
        btnDelete = findViewById(R.id.btnDelete);
//        storageReference = FirebaseStorage.getInstance().getReference();
//        ivProfilePicture = findViewById(R.id.ivProfilePicture);
    }

    private void fillFieldsWithData() {
        // Data verzamelen die aan de Intent naar de huidige Activity (vanuit MemberListActivity) is meegegeven
        playerId = getIntent().getStringExtra("playerId");
        firstname = getIntent().getStringExtra("firstname");
        lastname = getIntent().getStringExtra("lastname");
        birthdate = getIntent().getStringExtra("birthdate");
        address = getIntent().getStringExtra("address");

        // Titel van ActionBar aanpassen
        String name = firstname + " " + lastname;
        setTitle(name);

        // Verzamelde data in de bijbehorende tekstvakken zetten
        tvBirthdate.setText(birthdate);
        tvAddress.setText(address);

        // Foto tonen in de ImageView
//        fillImageView();
    }
}
