package com.intersofteagles.tictactoe.Activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.intersofteagles.tictactoe.Adapters.PlayersAdapter;
import com.intersofteagles.tictactoe.Commoners.BaseActivity;
import com.intersofteagles.tictactoe.POJOs.Game;
import com.intersofteagles.tictactoe.POJOs.Player;
import com.intersofteagles.tictactoe.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CreateGame extends BaseActivity {

    @BindView(R.id.recycler)RecyclerView recycler;
    @BindView(R.id.title)EditText title;
    @BindView(R.id.symbol)ImageView symbol;

    Game game = new Game();
    Player me;
    DatabaseReference database;
    ChildEventListener listener;
    ProgressDialog pdiag;
    PlayersAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_game);
        ButterKnife.bind(this);
        database = FirebaseDatabase.getInstance().getReference();
        setSupportActionBar((Toolbar)findViewById(R.id.toolbar));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Create Online Game");
        pdiag = new ProgressDialog(this);
        pdiag.setCancelable(false);
        pdiag.setMessage("Please wait ...");
        pdiag.show();
        adapter = new PlayersAdapter(this);

        database.child("players").child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                pdiag.dismiss();
                if (dataSnapshot.exists()) {
                    me = dataSnapshot.getValue(Player.class);
                    game.setPlayer_1(me);
                } else finish();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                pdiag.dismiss();
            }
        });
        listener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                if (dataSnapshot.exists()){
                    Player p = dataSnapshot.getValue(Player.class);
                    adapter.add(p);
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        database.child("players").addChildEventListener(listener);

        recycler.setAdapter(adapter);
        recycler.setLayoutManager(new LinearLayoutManager(this));
        recycler.setHasFixedSize(true);
    }




    public void changeSymbol(View b){
        startActivityForResult(new Intent(this, SymbolPicker.class).putExtra("player", "ME"), 1);
    }

    public void submit(View b){
        pdiag.show();
        String key = database.child("online_games").push().getKey();
        database.child("online_games").child(key).setValue(game).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(Task<Void> task) {
                pdiag.dismiss();
                finish();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1){
            int s = data.getIntExtra("symbol",R.drawable.rick);
            symbol.setImageResource(s);
            game.getPlayer_1().setSymbol(s);
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        database.child("players").removeEventListener(listener);
    }
}
