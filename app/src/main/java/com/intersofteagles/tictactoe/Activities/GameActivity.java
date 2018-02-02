package com.intersofteagles.tictactoe.Activities;

import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.res.Resources;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.view.animation.ScaleAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.intersofteagles.tictactoe.Adapters.GameMessagesAdapter;
import com.intersofteagles.tictactoe.Adapters.MovesAdapter;
import com.intersofteagles.tictactoe.Commoners.BaseActivity;
import com.intersofteagles.tictactoe.Commoners.Bot;
import com.intersofteagles.tictactoe.Commoners.MyBounceInter;
import com.intersofteagles.tictactoe.POJOs.Game;
import com.intersofteagles.tictactoe.POJOs.Message;
import com.intersofteagles.tictactoe.POJOs.Move;
import com.intersofteagles.tictactoe.POJOs.Player;
import com.intersofteagles.tictactoe.R;
import com.mikhaellopez.circularimageview.CircularImageView;

import butterknife.BindView;
import butterknife.ButterKnife;
import xyz.hanks.library.SmallBang;

public class GameActivity extends BaseActivity implements MovesAdapter.GameListener,Bot.BotListener{

    @BindView(R.id.recycler)RecyclerView moves_recycler;
    @BindView(R.id.chat_recycler)RecyclerView chat_recycler;
    @BindView(R.id.textArea)View textArea;
    @BindView(R.id.game_over)TextView game_over;
    @BindView(R.id.player1)TextView player1_name;
    @BindView(R.id.player2)TextView player2_name;
    @BindView(R.id.winner)TextView winner;
    @BindView(R.id.score1)TextView score1;
    @BindView(R.id.score2)TextView score2;
    @BindView(R.id.send)TextView send;
    @BindView(R.id.new_game)Button new_game;
    @BindView(R.id.text)EditText text;
    @BindView(R.id.player1_pic)ImageView player1_pic;
    @BindView(R.id.player2_pic)ImageView player2_pic;
    @BindView(R.id.bottom_sheet)RelativeLayout bottom_sheet;

    BottomSheetBehavior behavior;
    private int navbar_height = 0;
    private long out_duration = 500;
    private Intent intent;
    SmallBang bang;
    private boolean gameOver = false;

    MovesAdapter movesAdapter;
    GameMessagesAdapter messagesAdapter;
    private int symbol = -1,oneWins = 0,twoWins = 0;
    MediaPlayer player;

    Player player1,player2;
    Bot bot;
    private boolean online_game = false,p2p = true;
    private ChildEventListener onlineListener,chatListener;
    private DatabaseReference database;
    private Query onlineQuery,chatQuery;
    private String game_id;
    private Game game;
    private ProgressDialog progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_activity);
        ButterKnife.bind(this);
        intent = getIntent();
        online_game = intent.getBooleanExtra("online", false);
        p2p = intent.getBooleanExtra("p2p", true);
        navbar_height = getNavigationBarHeight();
        database = FirebaseDatabase.getInstance().getReference();
        bang = SmallBang.attach2Window(this);
        game_id = intent.getStringExtra("game_id");
       if (game_id == null)game_id = String.valueOf(System.currentTimeMillis());
        if (intent.getBooleanExtra("bot",false))bot = new Bot(this);

        initGame();
        initChat();
        if (bot != null){
            player1 = new Player();
            player1.setUsername("ME");
            player2_name.setText(bot.getUsername());
            player2_pic.setImageResource(R.drawable.bender);
            disableChat();
            bot.attachAdapter(movesAdapter);
            player = MediaPlayer.create(this, Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + getPackageName() + "/raw/" + "show_me"));
            player.setVolume(0.2f,0.2f);
        }
        if (online_game){
            disableChat();
            initOnlineGame();
        }
        if (p2p){
            initP2P();
        }
    }

    public void initOnlineGame(){
        onlineQuery = database.child("online_games").child(game_id).child("moves");
        onlineListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                if (dataSnapshot.exists()){
                    Move move = dataSnapshot.getValue(Move.class);
                    movesAdapter.commitMove(move);
                    animate(moves_recycler.getChildAt(move.getIndex()));
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
        onlineQuery.addChildEventListener(onlineListener);
        database.child("online_games").child(game_id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    game = dataSnapshot.getValue(Game.class);
                    player1 = game.getPlayer_1();
                    player2 = game.getPlayer_2();
                    player1_name.setText(player1.getUsername());
                    player2_name.setText(player2.getUsername());
                    symbol = player1.getSymbol();
                    player1_pic.setImageResource(getBig(symbol));
                    if (player2.getSymbol() != -1)player2_pic.setImageResource(getBig(player2.getSymbol()));

                    if (user.getUid().equals(player2.getUid())){
                        startActivityForResult(new Intent(getBaseContext(),SymbolPicker.class).putExtra("player",player1.getUsername())
                                .putExtra("exclude",player1.getSymbol()),1);
                    }
                } else {
                    Toast.makeText(GameActivity.this, "This game was deleted", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    public void initP2P(){
        disableChat();
        player1 = new Player();
        player1.setUsername("Player 1");
        player2 = new Player();
        player2.setUsername("Player 2");
    }

    public void initGame(){
        progress = new ProgressDialog(this);
        progress.setCancelable(false);
        progress.setMessage("Loading...");


        movesAdapter = new MovesAdapter(this);
        moves_recycler.setHasFixedSize(true);
        moves_recycler.setAdapter(movesAdapter);
        moves_recycler.setLayoutManager(new GridLayoutManager(this, 3));
        moves_recycler.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                doNothing();
            }
        });

        View bottomSheet = findViewById( R.id.bottom_sheet );
        behavior = BottomSheetBehavior.from(bottomSheet);
        behavior.setPeekHeight((50 + (navbar_height > 70 ? (navbar_height - 7) : 8)));
        behavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
            @Override
            public void onStateChanged(View bottomSheet, int newState) {
                if (newState == BottomSheetBehavior.STATE_COLLAPSED) {
                    textArea.setVisibility(View.VISIBLE);
                } else if (newState == BottomSheetBehavior.STATE_EXPANDED) {
                    textArea.setVisibility(View.GONE);
                }
            }

            @Override
            public void onSlide(View bottomSheet, float slideOffset) {

            }
        });

    }


    public void initChat(){
        chatListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                if (dataSnapshot.exists()){
                    Message message = dataSnapshot.getValue(Message.class);
                    messagesAdapter.addMessage(message);
                    if (!user.getUid().equals(message.getPlayer_id()))Toast.makeText(GameActivity.this, message.toString(), Toast.LENGTH_LONG).show();
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
        messagesAdapter = new GameMessagesAdapter(this);
        chat_recycler.setHasFixedSize(true);
        chat_recycler.setAdapter(messagesAdapter);
        chat_recycler.setLayoutManager(new LinearLayoutManager(this));

        chatQuery = database.child("online_games").child(game_id).child("messages");
        chatQuery.addChildEventListener(chatListener);
    }

    public void disableChat(){
        bottom_sheet.setEnabled(false);
        bottom_sheet.setVisibility(View.GONE);
        textArea.setVisibility(View.GONE);
    }

    public void sendMessage(View v){
        String key = database.child("online_games").child(game_id).child("messages").push().getKey();
        Player p = user.getUid().equals(player1.getUid())?player1:player2;
        Message msg = new Message(key,p.getUsername(),p.getUid(),text.getText().toString(),0,System.currentTimeMillis());
        chatQuery.getRef().child(key).setValue(msg);
        if ("start".equals(text.getText().toString().toLowerCase()) && bot != null)bot.play();
        text.setText(null);
    }


    @Override
    public void onMoveMade(Move move) {//THIS SHOULD BE CALLED BY HUMANS ONLY
        if (move.getSymbol() == -1){
            move.setSymbol(symbol);
            move.setTime(System.currentTimeMillis());
            move.setPlayer_id(user.getUid());
        }
        movesAdapter.commitMove(move);
        animate(moves_recycler.getChildAt(move.getIndex()));

        if (bot != null && !gameOver)bot.play();

        if (p2p){
            if (symbol == player1.getSymbol()){
                symbol = player2.getSymbol();
                switchPlayer(false);
            }else {
                symbol = player1.getSymbol();
                switchPlayer(true);
            }
        }


    }


    @Override
    public void onGameWon(final int symbol, int ... indices) {
        gameOver = true;
        for(int i = 0;i<indices.length;i++){
            animate(moves_recycler.getChildAt(indices[i]));
        }
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                animateMovesOut();
            }
        }, 1000);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (player1.getSymbol() == symbol){
                    oneWins++;
                }else {
                    twoWins++;
                }
                score1.setText(String.format("%d wins",oneWins));
                score2.setText(String.format("%d wins",twoWins));
                animateGameOver(player1.getSymbol() == symbol ? "PLayer 1 wins" : "Player 2 wins", true);
            }
        }, out_duration + 500);
    }


    @Override
    public void onBotDecided(Move move) {
        move.setSymbol(bot.getSymbol());
        move.setTime(System.currentTimeMillis());
        move.setPlayer_id(bot.getUid());
        movesAdapter.commitMove(move);
        animate(moves_recycler.getChildAt(move.getIndex()));
        Log.e("PLAYING", move.toString());
    }

    public void newGame(View view){
        gameOver = false;
        movesAdapter.resetGame();
        animateGameOverOut();
        if (moves_recycler.getChildAt(0).getTranslationY()<1f){
            animateMovesIn(true);
        }
    }

    public void animate(View v){
        ScaleAnimation scale = new ScaleAnimation(0.5f,1f,0.5f,1f,50f,50f);
        scale.setInterpolator(new MyBounceInter(0.1, 20));
        scale.setDuration(2000);
        v.startAnimation(scale);
    }

    public void switchPlayer(boolean one){
        if (one){
            player1_pic.animate().alpha(1.2f).setDuration(2000).setInterpolator(new MyBounceInter(0.1,10));
        }else {
            player2_pic.animate().alpha(1.2f).setDuration(2000).setInterpolator(new MyBounceInter(0.1,10));
        }
    }

    public void animateMovesOut(){
        moves_recycler.getChildAt(0).animate().alpha(0f).translationX(-50f).translationY(-50f).setDuration(out_duration).setInterpolator(new DecelerateInterpolator());
        moves_recycler.getChildAt(1).animate().alpha(0f).translationY(-50f).setDuration(out_duration).setInterpolator(new DecelerateInterpolator());
        moves_recycler.getChildAt(2).animate().alpha(0f).translationX(50f).translationY(-50f).setDuration(out_duration).setInterpolator(new DecelerateInterpolator());
        moves_recycler.getChildAt(3).animate().alpha(0f).translationX(-50f).setDuration(out_duration).setInterpolator(new DecelerateInterpolator());
        moves_recycler.getChildAt(4).animate().alpha(0f).scaleX(0f).scaleY(0f).setDuration(out_duration).setInterpolator(new DecelerateInterpolator());
        moves_recycler.getChildAt(5).animate().alpha(0f).translationX(50f).setDuration(out_duration).setInterpolator(new DecelerateInterpolator());
        moves_recycler.getChildAt(6).animate().alpha(0f).translationX(-50f).translationY(50f).setDuration(out_duration).setInterpolator(new DecelerateInterpolator());
        moves_recycler.getChildAt(7).animate().alpha(0f).translationY(50f).setDuration(out_duration).setInterpolator(new DecelerateInterpolator());
        moves_recycler.getChildAt(8).animate().alpha(0f).translationX(50f).translationY(50f).setDuration(out_duration).setInterpolator(new DecelerateInterpolator());
    }

    public void animateMovesIn(boolean reset){
        if (reset){
            setPosition(moves_recycler.getChildAt(0), -50f, -50f, 1f);
            setPosition(moves_recycler.getChildAt(1), 1f, -50f, 1f);
            setPosition(moves_recycler.getChildAt(2), 50f, -50f, 1f);
            setPosition(moves_recycler.getChildAt(3), -50f, -50f, 1f);
            setPosition(moves_recycler.getChildAt(4), 1f, 1f, 0f);
            setPosition(moves_recycler.getChildAt(5), 50f, 1f, 1f);
            setPosition(moves_recycler.getChildAt(6), -50f, 50f, 1f);
            setPosition(moves_recycler.getChildAt(7), 1f, 50f, 1f);
            setPosition(moves_recycler.getChildAt(8), 50f, 50f, 1f);
        }

        for (int i = 0; i<moves_recycler.getChildCount();i++){
            View v = moves_recycler.getChildAt(i);
            v.animate().translationY(1f).translationX(1f).alpha(1f).scaleY(1f).scaleX(1f).setDuration(1000).setInterpolator(new DecelerateInterpolator());
        }
    }

    public void animateGameOver(String result,boolean newGame){
        new_game.setVisibility(newGame?View.VISIBLE:View.GONE);
        winner.setText(result);
        game_over.setTranslationY(-200f);
        game_over.setScaleX(0.5f);
        game_over.setScaleY(0.5f);
        winner.setTranslationY(200f);
        winner.setScaleX(0.5f);
        winner.setScaleY(0.5f);
        game_over.animate().alpha(1f).scaleX(1f).scaleY(1f).translationY(1f).setDuration(1000).setInterpolator(new OvershootInterpolator());
        winner.animate().alpha(1f).scaleX(1f).scaleY(1f).translationY(1f).setDuration(1000).setInterpolator(new OvershootInterpolator());
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                bang.bang(winner);//TODO if not draw
            }
        }, 1300);
    }

    public void animateGameOverOut(){
        game_over.animate().alpha(0f).translationY(-200f).setDuration(500);
        winner.animate().alpha(0f).translationY(200f).setDuration(500);
        new_game.setVisibility(View.GONE);
    }

    public void setPosition(View v,float X,float Y,float scale){
        v.setAlpha(0f);
        v.setTranslationX(X);
        v.setTranslationY(Y);
        v.setScaleX(scale);
        v.setScaleY(scale);
    }

    public void doNothing(){moves_recycler.setVisibility(View.VISIBLE);}

    private int getNavigationBarHeight(){
        Resources r = getResources();
        int resourceId = r.getIdentifier("navigation_bar_height","dimen","android");
        if (resourceId > 0){
            return r.getDimensionPixelSize(resourceId);
        }
        return 0;
    }


    @Override
    protected void onPause() {
        super.onPause();
        if (player != null){
            player.release();
            player = null;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (onlineQuery != null)onlineQuery.removeEventListener(onlineListener);
        if (chatQuery != null)chatQuery.removeEventListener(chatListener);
    }


    @Override
    protected void onResume() {
        super.onResume();

        if (bot == null){
            if (player1.getSymbol() == -1){
                startActivityForResult(new Intent(this,SymbolPicker.class).putExtra("player","Player 1"),1);
            }else if (player2.getSymbol() == -1){
                startActivityForResult(new Intent(this,SymbolPicker.class).putExtra("player","Player 2").putExtra("exclude",player1.getSymbol()),2);
            }
        }else {
            if (player1.getSymbol() == -1){
                startActivityForResult(new Intent(this,SymbolPicker.class).putExtra("player","Player 1"),1);
            }else {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (player != null) player.start();
                    }
                }, 1000);
            }
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1){
            int s = data.getIntExtra("symbol",R.drawable.rick);
            if (player1 != null)player1.setSymbol(s);
            player1_pic.setImageResource(getBig(s));
            symbol = s;
        }else if (requestCode == 2){
            int s = data.getIntExtra("symbol",R.drawable.face);
            if (player2 != null)player2.setSymbol(s);
            player2_pic.setImageResource(getBig(s));
        }
    }



    public int getBig(int s){
        switch (s){
            case R.drawable.face:
                return R.drawable.morty_hip;
            case R.drawable.goldenfold:
                return R.drawable.goldenfold_big;
            case R.drawable.scary_terry:
                return R.drawable.scary_terry_big;
            case R.drawable.meeseeks:
                return R.drawable.meeseeks_big;
            default:
                return R.drawable.rick_big;
        }
    }

}
