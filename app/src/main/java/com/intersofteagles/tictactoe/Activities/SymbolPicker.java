package com.intersofteagles.tictactoe.Activities;

import android.content.ContentResolver;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.intersofteagles.tictactoe.Adapters.SymbolAdapter;
import com.intersofteagles.tictactoe.POJOs.Symbol;
import com.intersofteagles.tictactoe.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SymbolPicker extends AppCompatActivity implements SymbolAdapter.Listener{

    @BindView(R.id.recycler)RecyclerView recycler;
    @BindView(R.id.title)TextView title;


    SymbolAdapter adapter;
    MediaPlayer player;
    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_symbol_picker);
        ButterKnife.bind(this);
        intent = getIntent();
        int exclude = intent.getIntExtra("exclude",-1);

        adapter = new SymbolAdapter(this,exclude);
        recycler.setHasFixedSize(true);
        recycler.setLayoutManager(new GridLayoutManager(this, 3));
        recycler.setAdapter(adapter);

        if (intent.getStringExtra("player") != null){
            title.setText("Select a character ["+getIntent().getStringExtra("player")+"]");
        }
    }


    @Override
    public void onClick(final Symbol symbol,int index) {
        disable(index);
        player = MediaPlayer.create(this, Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + getPackageName() + "/raw/" + symbol.getRaw()));
        player.start();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                closeIt(symbol);
            }
        },1500);
    }

    public void disable(int index){
        adapter.disable(index);
    }

    public void closeIt(Symbol symbol){
        Intent i = new Intent();
        i.putExtra("symbol",symbol.getSymbol());
        setResult(RESULT_OK,i);
        finish();
    }


}
