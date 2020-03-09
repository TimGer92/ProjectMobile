package com.example.geritstimmymobile;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.geritstimmymobile.model.Player;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

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
    private FirebaseFirestore db;
    private static final String TAG = "PlayerListActivity :::";
    private List<Player> playerList = new ArrayList<>();

    public PlayerListActivity() {
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player_list);

        Log.i(TAG, "SetContent als eerst?");

//        nog als variabele meegeven
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

        private final View.OnClickListener mOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Player player = (Player) view.getTag();
                if (mTwoPane) {
                    Bundle arguments = new Bundle();
                    arguments.putString("playerId", player.getPlayerId());
                    arguments.putString("firstname", player.getFirstname());
                    arguments.putString("lastname", player.getLastname());
                    arguments.putString("birthdate", player.getBirthdate());
                    arguments.putString("address", player.getAddress());

                    PlayerDetailFragment fragment = new PlayerDetailFragment();
                    fragment.setArguments(arguments);
                    mParentActivity.getSupportFragmentManager().beginTransaction()
                            .replace(R.id.player_detail_landscape, fragment)
                            .commit();
                } else {
                    Context context = view.getContext();
                    Intent intent = new Intent(context, PlayerDetailActivity.class);
                    intent.putExtra("playerId", player.getPlayerId());
                    intent.putExtra("firsname", player.getFirstname());
                    intent.putExtra("lastname", player.getLastname());
                    intent.putExtra("birthdate", player.getBirthdate());
                    intent.putExtra("address", player.getAddress());

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
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.player_list_content, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {
            String name = players.get(position).getFirstname() + " " + players.get(position).getLastname();
            holder.tvName.setText(name);

            holder.itemView.setTag(players.get(position));
            holder.itemView.setOnClickListener(mOnClickListener);

            Log.i(TAG, "View within RecyclerViewList created successfully");
        }

        @Override
        public int getItemCount() {
            return players.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            final TextView tvName;

            ViewHolder(View view) {
                super(view);
                tvName = view.findViewById(R.id.tv_member_name);
            }
        }
    }
}
