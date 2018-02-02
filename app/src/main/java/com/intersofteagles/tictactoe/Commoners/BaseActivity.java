package com.intersofteagles.tictactoe.Commoners;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.intersofteagles.tictactoe.Activities.MainActivity;
import com.intersofteagles.tictactoe.Activities.SignIn;
import com.intersofteagles.tictactoe.R;
import com.mikepenz.iconics.context.IconicsContextWrapper;

import butterknife.ButterKnife;

/**
 * Created by Monroe on 4/12/2017.
 */
public class BaseActivity extends AppCompatActivity {

    public FirebaseUser user;
    public boolean connected = false,isWifi = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        user = FirebaseAuth.getInstance().getCurrentUser();
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(IconicsContextWrapper.wrap(newBase));
    }

    @Override
    protected void onPause() {
        super.onPause();
        try {
            unregisterReceiver(networkStateReceiver);
        }catch (Exception e){}
    }


    @Override
    protected void onResume() {
        super.onResume();
        user = FirebaseAuth.getInstance().getCurrentUser();
        try {
            this.registerReceiver(networkStateReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
        }catch (Exception e){}

        if (user == null && MainActivity.splashed){
//            startActivity(new Intent(this, SignIn.class));
        }
    }

    private BroadcastReceiver networkStateReceiver= new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo ni = manager.getActiveNetworkInfo();
            connected = (ni == null ? false : ni.isConnected());
            isWifi =  (connected && ni.getType() == ConnectivityManager.TYPE_WIFI);
        }
    };
}
