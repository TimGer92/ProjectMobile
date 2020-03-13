package com.example.geritstimmymobile;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.example.geritstimmymobile.model.Player;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

/**
 * An activity representing a list of Players. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {@link PlayerDetailActivity} representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 */
public class PlayerListActivity extends AppCompatActivity {

    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private boolean mTwoPane;
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private FirebaseFirestore db;
    private static final String TAG = "PlayerListActivity :::";
    private List<Player> playerList = new ArrayList<>();

    public PlayerListActivity() {
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player_list);
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();

        setTitle("Players");

        if (checkLandscapeMode()) {
            Log.i(TAG, "In landscape mode");
            mTwoPane = true;
        }

        db = FirebaseFirestore.getInstance();
        getPlayers();
        RecyclerView recyclerView = findViewById(R.id.rv_player_list);
        assert recyclerView != null;
        setupRecyclerView(recyclerView, playerList);
        Log.i(TAG, "OnCreate finished");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        // Anders kan app crashen
        try {
            menu.findItem(R.id.playerList).setVisible(false);
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

    private boolean checkLandscapeMode() {
        int orientation = getResources().getConfiguration().orientation;
        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            Log.i(TAG, "In Landscape mode");

            return true;
        } else {
            Log.i(TAG, "In Portait mode");

            return false;
        }
    }

    private void setupRecyclerView(@NonNull RecyclerView recyclerView, List<Player> players) {
        recyclerView.setAdapter(new PlayerRecyclerViewAdapter(this, players, mTwoPane));

        Log.i(TAG, "Adapter set to RecyclerView successfully");
    }

    public void getPlayers() {
        db.collection("players").get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();
                        for (DocumentSnapshot object : list) {
                            Player player = object.toObject(Player.class);
                            playerList.add(player);
                        }
                        findViewById(R.id.progressBar_read).setVisibility(View.GONE);
                    }
                });
    }

    public static class PlayerRecyclerViewAdapter
            extends RecyclerView.Adapter<PlayerRecyclerViewAdapter.ViewHolder> {

        private final PlayerListActivity mParentActivity;
        private final List<Player> players;
        private final boolean mTwoPane;
        private StorageReference storageReference;


        private final View.OnClickListener mOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Player player = (Player) view.getTag();
                if (mTwoPane) {
                    Bundle arguments = new Bundle();
                    arguments.putString("playerId", player.getPlayerId());
                    arguments.putString("firstname", player.getFirstname());
                    arguments.putString("lastname", player.getLastname());
                    arguments.putString("gender", player.getGender());

                    PlayerDetailFragment fragment = new PlayerDetailFragment();
                    fragment.setArguments(arguments);
                    mParentActivity.getSupportFragmentManager().beginTransaction()
                            .replace(R.id.player_detail_landscape, fragment)
                            .commit();
                } else {
                    Context context = view.getContext();
                    Intent intent = new Intent(context, PlayerDetailActivity.class);
                    intent.putExtra("playerId", player.getPlayerId());
                    intent.putExtra("firstname", player.getFirstname());
                    intent.putExtra("lastname", player.getLastname());
                    intent.putExtra("gender", player.getGender());

                    context.startActivity(intent);
                }
            }
        };

        PlayerRecyclerViewAdapter(PlayerListActivity parent,
                                  List<Player> players,
                                  boolean twoPane) {
            this.players = players;
            this.mParentActivity = parent;
            this.mTwoPane = twoPane;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            storageReference = FirebaseStorage.getInstance().getReference();
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.player_list_content, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {
            String name = players.get(position).getFirstname() + " " + players.get(position).getLastname();
            holder.tvName.setText(name);

            String imageName = "gs://mtglifecounter-2f06e.appspot.com/hhnBhigdJLF92Nw2hm9CC";

            holder.itemView.setTag(players.get(position));
            holder.itemView.setOnClickListener(mOnClickListener);
            try {
//                if (imageName != null) {
//                    final long ONE_MEGABYTE = 1024 * 1024;
////                    this.storageReference.child(imageName).getBytes(ONE_MEGABYTE).addOnSuccessListener(bytes -> {
////                        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
////                        holder.playerIcon.setImageBitmap(bitmap);
////                    });
//                }
//                Uri filePath = Uri.parse("android.resource://"+ R.class.getPackage().getName()+"/" + R.drawable.mtg);
                String gender = players.get(position).getGender();
                switch(gender) {
                    case "Female":
                        holder.playerIcon.setImageResource(R.drawable.lilana);
                        return;
                    case "Male":
                        holder.playerIcon.setImageResource(R.drawable.jace);
                        return;
                    default:
                        holder.playerIcon.setImageResource(R.drawable.mtg);
                        return;
                }

            } catch (Exception e) {
                Log.e(TAG, e.getMessage());
            }

            Log.i(TAG, "View within RecyclerViewList created successfully");
        }

        @Override
        public int getItemCount() {
            return players.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            TextView tvName;
            ImageView playerIcon;

            ViewHolder(View view) {
                super(view);
                tvName = view.findViewById(R.id.tv_member_name);
                playerIcon = view.findViewById(R.id.ivProfilePicture);
            }
        }
    }
}
