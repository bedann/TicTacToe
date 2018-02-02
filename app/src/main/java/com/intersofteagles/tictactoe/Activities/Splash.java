package com.intersofteagles.tictactoe.Activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.ContentResolver;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;

import com.intersofteagles.tictactoe.Commoners.MyBounceInter;
import com.intersofteagles.tictactoe.Commoners.SquareRelativeLayout;
import com.intersofteagles.tictactoe.R;

import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;

public class Splash extends AppCompatActivity {

    @BindView(R.id.square)View square;
    @BindView(R.id.portal)ImageView portal;
    @BindView(R.id.rick)ImageView rick;
    @BindView(R.id.morty)ImageView morty;
    @BindView(R.id.app_name)ImageView app_name;
    @BindView(R.id.lights)ImageView lights;


    MediaPlayer player,player2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        ButterKnife.bind(this);
        MainActivity.splashed = true;

        reset();
        animatePortal();

        player = MediaPlayer.create(this, Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + getPackageName() + "/raw/" + "portal_sound"));
        player2 = MediaPlayer.create(this, Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + getPackageName() + "/raw/" + "rick_phrase"));
        player2.setVolume(0.3f,0.3f);
        player.setVolume(0.3f,0.3f);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (player != null)player.start();
            }
        }, 500);
    }

    public void reset(){
        portal.setScaleY(0f);
        portal.setScaleX(0.5f);
        portal.setAlpha(0f);

        app_name.setTranslationY(-100f);
        app_name.setAlpha(0f);
        app_name.setScaleY(0f);
        app_name.setScaleX(0f);

        rick.setScaleY(0.6f);
        rick.setScaleX(0.6f);
        rick.setAlpha(0f);
        rick.setTranslationX(30f);

        morty.setScaleY(0.6f);
        morty.setScaleX(0.6f);
        morty.setAlpha(0f);
        morty.setTranslationX(-30f);

        lights.setAlpha(0f);
    }

    public void animatePortal(){
        portal.animate().rotationBy(360f).alpha(1f).scaleX(1f).scaleY(1f).setDuration(2000).setInterpolator(new MyBounceInter(0.09,20))
                .setStartDelay(500)
        .setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);

            }
        });
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                animateRickAndMorty();
            }
        },1500);
    }


    public void animateRickAndMorty(){
        try {
            player2.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
        lights.animate().alpha(1f).setDuration(500);
        rick.animate().setStartDelay(300).alpha(1f).scaleX(1f).scaleY(1f).translationX(1f).setDuration(500).setInterpolator(new DecelerateInterpolator());
        morty.animate().setStartDelay(300).alpha(1f).scaleX(1f).scaleY(1f).translationX(1f).setDuration(500).setInterpolator(new DecelerateInterpolator());
        app_name.animate().alpha(1f).scaleX(1f).scaleY(1f).translationY(1f).setDuration(500).setStartDelay(1500)
        .setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        finish();
                    }
                }, 800);
            }
        });
    }


    @Override
    protected void onPause() {
        super.onPause();
        if (player != null){
            player.release();
            player = null;
            player2.release();
            player2 = null;
        }
    }
}
