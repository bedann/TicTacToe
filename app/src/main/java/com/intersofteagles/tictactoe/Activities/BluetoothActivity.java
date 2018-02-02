package com.intersofteagles.tictactoe.Activities;

import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.view.animation.ScaleAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.intersofteagles.tictactoe.Adapters.DeviceAdapter;
import com.intersofteagles.tictactoe.Adapters.MovesAdapter;
import com.intersofteagles.tictactoe.Commoners.MyBounceInter;
import com.intersofteagles.tictactoe.POJOs.Move;
import com.intersofteagles.tictactoe.R;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;
import xyz.hanks.library.SmallBang;

import static com.intersofteagles.tictactoe.R.id.player1;

public class BluetoothActivity extends AppCompatActivity implements MovesAdapter.GameListener{


    @BindView(R.id.recycler)RecyclerView recycler;
    @BindView(R.id.chat_recycler)RecyclerView chat_recycler;
    @BindView(R.id.textArea)View textArea;
    @BindView(R.id.game_over)TextView game_over;
    @BindView(player1)TextView player1_name;
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
    private ProgressDialog pdiag;
    BottomSheetBehavior behavior;
    SmallBang bang;
    private long out_duration = 500;


    private final int REQUEST_ENABLE_BT = 99;
    private boolean stop_server = false,isServer = false,gameOver = false;
    private final String TAG = "BLUETOOTH_ACTIVITY";
    private final String UUID_STRING_WELL_KNOWN_SPP =
            "00001101-0000-1000-8000-00805F9B34FB";
    private UUID myUUID;
    private int navbar_height;

    private int retries = 0,oneWins = 0,twoWins = 0;;

    BluetoothAdapter mBluetoothAdapter;
    ConnectionThread myConnectionThread;
    ConnectToDevice connectToDevice;
    ServerThread serverThread;

    BluetoothDevice player1Device;

    private MovesAdapter dataAdapter;

    private int mySymbol = -1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_activity);
        ButterKnife.bind(this);
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        navbar_height = getNavigationBarHeight();
        initUI();
        Intent intent = getIntent();
        isServer = intent.getBooleanExtra("server",false);
//        if (isServer)mySymbol = R.drawable.rick;
        if (!isServer){
            player1Device = intent.getParcelableExtra("bd");
        }

        myUUID = UUID.fromString(UUID_STRING_WELL_KNOWN_SPP);

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) {
            Toast.makeText(this, "Bluetooth not supported", Toast.LENGTH_SHORT).show();
            finish();
        }

//        initServer();

    }


    public void initServer(){
        if (!isServer)return;
        Intent discoverableIntent =
                new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
        startActivity(discoverableIntent);


        pdiag.setMessage("Waiting for player 2 ...");

        serverThread = new ServerThread();
        serverThread.start();

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mySymbol == -1){
            startActivityForResult(new Intent(this,SymbolPicker.class).putExtra("player",isServer?"Player 1":"Player 2"),1);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    public void initUI(){
        bang = SmallBang.attach2Window(this);

        dataAdapter = new MovesAdapter(this);

        recycler.setHasFixedSize(true);
        recycler.setAdapter(dataAdapter);
        recycler.setLayoutManager(new GridLayoutManager(this, 3));
        recycler.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e("S","d");
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
        disableChat();

        pdiag = new ProgressDialog(this,android.R.style.Theme_Material_Light_Dialog_Alert);
        pdiag.setMessage("Connecting to player 1 ...");

    }

    @Override
    public void onMoveMade(Move move) {
        move.setSymbol(mySymbol);
        if (myConnectionThread != null){
            dataAdapter.commitMove(move);
            myConnectionThread.write(String.format("%d,%d",move.getIndex(),move.getSymbol()).getBytes());
        }else {
            Toast.makeText(this, "Waiting for connection", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onGameWon(final int symbol, int... indices) {
        gameOver = true;
        for(int i = 0;i<indices.length;i++){
            animate(recycler.getChildAt(indices[i]));
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
                if (mySymbol == symbol){
                    if (isServer){
                        oneWins++;
                    }else {
                        twoWins++;
                    }
                }else {
                    if (isServer){
                        twoWins++;
                    }else {
                        oneWins++;
                    }
                }
                score1.setText(String.format("%d wins",oneWins));
                score2.setText(String.format("%d wins",twoWins));
                animateGameOver(mySymbol == symbol? "You win" : "You lose", true);
            }
        }, out_duration + 500);
    }



    public void newGame(View view){
        gameOver = false;
        dataAdapter.resetGame();
        animateGameOverOut();
        if (recycler.getChildAt(0).getTranslationY()<1f){
            animateMovesIn(true);
        }
        myConnectionThread.write(String.valueOf("-1").getBytes());
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1){
            int s = data.getIntExtra("symbol",R.drawable.rick);
            if (s == -1){
                finish();
                return;
            }
            mySymbol = s;
            pdiag.show();
            if(isServer){
                player1_pic.setImageResource(s);
                initServer();
            }else {
                player2_pic.setImageResource(s);
                //connect to server
                Toast.makeText(this, "Connecting to " + player1Device.getName(), Toast.LENGTH_SHORT).show();
                connectToDevice = new ConnectToDevice(player1Device);
                connectToDevice.start();
            }
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Don't forget to unregister the ACTION_FOUND receiver.

        if(connectToDevice!=null){
            connectToDevice.cancel();
        }
        if(myConnectionThread!=null){
            myConnectionThread.cancel();
        }
        if (serverThread != null){
            stop_server = true;
            serverThread.cancel();
        }
        if (mBluetoothAdapter.isDiscovering())mBluetoothAdapter.cancelDiscovery();
    }






    private class ConnectToDevice extends Thread {
        private final BluetoothSocket mmSocket;
        private final BluetoothDevice mmDevice;

        public ConnectToDevice(BluetoothDevice device) {
            // Use a temporary object that is later assigned to mmSocket
            // because mmSocket is final.
            retries++;
            BluetoothSocket tmp = null;
            mmDevice = device;

            try {
                tmp = device.createInsecureRfcommSocketToServiceRecord(myUUID);
            } catch (IOException e) {
                Log.e(TAG, "Socket's create() method failed", e);
            }
            Log.e(TAG,"socket null: "+(tmp == null));
            mmSocket = tmp;
        }

        @Override
        public void run() {
            // Cancel discovery because it otherwise slows down the connection.

            try {
                mmSocket.connect();
            } catch (IOException connectException) {
                // Unable to connect; close the socket and return.
                try {
                    mmSocket.close();
                } catch (IOException closeException) {
                    Log.e(TAG, "Could not close the client socket", closeException);
                }
                Log.e(TAG, connectException.getMessage());
                if (retries < 5){
                    connectToDevice = new ConnectToDevice(player1Device);
                    connectToDevice.start();
                }else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            pdiag.dismiss();
                            Toast.makeText(BluetoothActivity.this, "Failed", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
                return;
            }

            // The connection attempt succeeded. Perform work associated with
            // the connection in a separate thread.
            if (mmSocket.isConnected()){
                myConnectionThread = new ConnectionThread(mmSocket);
                myConnectionThread.start();

                myConnectionThread.write(String.format("%d",mySymbol).getBytes());

            }else {
                Log.e(TAG,"Socket is not connected");
            }

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    pdiag.dismiss();
                }
            });
        }

        // Closes the client socket and causes the thread to finish.
        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) {
                Log.e(TAG, "Could not close the client socket", e);
            }
        }
    }



    private class ConnectionThread extends Thread {
        private final BluetoothSocket connectedBluetoothSocket;
        private final InputStream connectedInputStream;
        private final OutputStream connectedOutputStream;

        public ConnectionThread(BluetoothSocket socket) {
            connectedBluetoothSocket = socket;
            InputStream in = null;
            OutputStream out = null;

            try {
                in = socket.getInputStream();
                out = socket.getOutputStream();
            } catch (IOException e) {
                e.printStackTrace();
            }

            connectedInputStream = in;
            connectedOutputStream = out;
        }

        @Override
        public void run() {
            byte[] buffer = new byte[1024];
            int bytes;

            while (true) {
                try {
                    bytes = connectedInputStream.read(buffer);

                    //TODO DATA RECEIVED
                    final String strReceived = new String(buffer, 0, bytes);
                    if (strReceived.contains(",")){
                        String[] data = strReceived.split(",");

                        int index = Integer.parseInt(data[0]);
                        int symbol = Integer.parseInt(data[1]);
                        final Move move = new Move(index,symbol);

                        runOnUiThread(new Runnable(){
                            @Override
                            public void run() {
                            dataAdapter.commitMove(move);
                        }});
                    }else {
                        final int s = Integer.valueOf(strReceived);
                        if (s == -1){
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    newGame(null);
                                }
                            });
                        }else {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (isServer){
                                        player2_pic.setImageResource(s);
                                    }else {
                                        player1_pic.setImageResource(s);
                                    }
                                }
                            });
                        }
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                    final String msgConnectionLost = "Connection lost:\n"
                            + e.getMessage();
                    runOnUiThread(new Runnable(){
                        @Override
                        public void run() {
                            Toast.makeText(BluetoothActivity.this, "Connection lost", Toast.LENGTH_SHORT).show();
                    }});
                    cancel();
                    break;
                }
            }
        }

        public void write(byte[] buffer) {
            try {
                connectedOutputStream.write(buffer);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public void cancel() {
            try {
                connectedBluetoothSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }



    private class ServerThread extends Thread {
        private final BluetoothServerSocket mmServerSocket;

        public ServerThread() {
            // Use a temporary object that is later assigned to mmServerSocket
            // because mmServerSocket is final.
            BluetoothServerSocket tmp = null;
            try {
                // MY_UUID is the app's UUID string, also used by the client code.
                tmp = mBluetoothAdapter.listenUsingRfcommWithServiceRecord("SmartCow", myUUID);
            } catch (IOException e) {
                Log.e(TAG, "Socket's listen() method failed", e);
            }
            mmServerSocket = tmp;
        }

        @Override
        public void run() {
            mBluetoothAdapter.cancelDiscovery();

            BluetoothSocket socket = null;
            // Keep listening until exception occurs or a socket is returned.
            while (true) {
                if (stop_server){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            pdiag.dismiss();
                            Toast.makeText(BluetoothActivity.this, "Server terminated", Toast.LENGTH_SHORT).show();
                        }
                    });
                    break;
                }
                try {
                    socket = mmServerSocket.accept();
                } catch (IOException e) {
                    Log.e(TAG, "Socket's accept() method failed", e);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            pdiag.dismiss();
                        }
                    });
                    break;
                }

                if (socket != null) {
                    // A connection was accepted. Perform work associated with
                    // the connection in a separate thread.
                    myConnectionThread = new ConnectionThread(socket);
                    myConnectionThread.start();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            pdiag.dismiss();
                            Toast.makeText(BluetoothActivity.this, "Connection established", Toast.LENGTH_SHORT).show();
                            myConnectionThread.write(String.format("%d",mySymbol).getBytes());
                        }
                    });
                    break;
                }
            }
        }

        // Closes the connect socket and causes the thread to finish.
        public void cancel() {
            try {
                mmServerSocket.close();
            } catch (IOException e) {
                Log.e(TAG, "Could not close the connect socket", e);
            }
        }
    }




    private int getNavigationBarHeight(){
        Resources r = getResources();
        int resourceId = r.getIdentifier("navigation_bar_height","dimen","android");
        if (resourceId > 0){
            return r.getDimensionPixelSize(resourceId);
        }
        return 0;
    }


    public void animate(View v){
        ScaleAnimation scale = new ScaleAnimation(0.5f,1f,0.5f,1f,50f,50f);
        scale.setInterpolator(new MyBounceInter(0.1, 20));
        scale.setDuration(2000);
        v.startAnimation(scale);
    }

    public void disableChat(){
        bottom_sheet.setEnabled(false);
        bottom_sheet.setVisibility(View.GONE);
        textArea.setVisibility(View.GONE);
    }


    public void animateMovesOut(){
        recycler.getChildAt(0).animate().alpha(0f).translationX(-50f).translationY(-50f).setDuration(out_duration).setInterpolator(new DecelerateInterpolator());
        recycler.getChildAt(1).animate().alpha(0f).translationY(-50f).setDuration(out_duration).setInterpolator(new DecelerateInterpolator());
        recycler.getChildAt(2).animate().alpha(0f).translationX(50f).translationY(-50f).setDuration(out_duration).setInterpolator(new DecelerateInterpolator());
        recycler.getChildAt(3).animate().alpha(0f).translationX(-50f).setDuration(out_duration).setInterpolator(new DecelerateInterpolator());
        recycler.getChildAt(4).animate().alpha(0f).scaleX(0f).scaleY(0f).setDuration(out_duration).setInterpolator(new DecelerateInterpolator());
        recycler.getChildAt(5).animate().alpha(0f).translationX(50f).setDuration(out_duration).setInterpolator(new DecelerateInterpolator());
        recycler.getChildAt(6).animate().alpha(0f).translationX(-50f).translationY(50f).setDuration(out_duration).setInterpolator(new DecelerateInterpolator());
        recycler.getChildAt(7).animate().alpha(0f).translationY(50f).setDuration(out_duration).setInterpolator(new DecelerateInterpolator());
        recycler.getChildAt(8).animate().alpha(0f).translationX(50f).translationY(50f).setDuration(out_duration).setInterpolator(new DecelerateInterpolator());
    }

    public void animateMovesIn(boolean reset){
        if (reset){
            setPosition(recycler.getChildAt(0), -50f, -50f, 1f);
            setPosition(recycler.getChildAt(1), 1f, -50f, 1f);
            setPosition(recycler.getChildAt(2), 50f, -50f, 1f);
            setPosition(recycler.getChildAt(3), -50f, -50f, 1f);
            setPosition(recycler.getChildAt(4), 1f, 1f, 0f);
            setPosition(recycler.getChildAt(5), 50f, 1f, 1f);
            setPosition(recycler.getChildAt(6), -50f, 50f, 1f);
            setPosition(recycler.getChildAt(7), 1f, 50f, 1f);
            setPosition(recycler.getChildAt(8), 50f, 50f, 1f);
        }

        for (int i = 0; i<recycler.getChildCount();i++){
            View v = recycler.getChildAt(i);
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


}
