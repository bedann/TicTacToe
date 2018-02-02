package com.intersofteagles.tictactoe.Activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.intersofteagles.tictactoe.Commoners.BaseActivity;
import com.intersofteagles.tictactoe.Commoners.MyBounceInter;
import com.intersofteagles.tictactoe.POJOs.Player;
import com.intersofteagles.tictactoe.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends BaseActivity {

    @BindView(R.id.content)LinearLayout content;

    public static boolean splashed = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        splashed = false;
        startActivity(new Intent(this, Splash.class));

    }


    @Override
    protected void onResume() {
        super.onResume();
        animateIn();
    }

    public void animateIn(){
        for(int i = 0;i<content.getChildCount();i++){
            content.getChildAt(i).setAlpha(0f);
            content.getChildAt(i).setTranslationY(300f);
            content.getChildAt(i).setScaleX(0f);
        }
        for(int i = 0;i<content.getChildCount();i++){
            content.getChildAt(i).animate().alpha(1f).scaleX(1f).translationY(1f).setDuration(2000).setStartDelay(i*100).setInterpolator(new MyBounceInter(0.1,20));
        }
    }

    public void openGameBot(View v){
        startActivity(new Intent(this, GameActivity.class).putExtra("bot", true));
    }

    public void openGameBt(View v){
        startActivity(new Intent(this, BluetoothGame.class));
    }

    public void openGame(View v){
        startActivity(new Intent(this,GameActivity.class));
    }

    public void openOnlineGame(View v){
//        startActivity(new Intent(this,GameActivity.class).putExtra("online",true));
        startActivity(new Intent(this,CreateGame.class).putExtra("online",true));
    }



}
