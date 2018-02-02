package com.intersofteagles.tictactoe.Activities;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.ProgressDialog;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.intersofteagles.tictactoe.Commoners.BaseActivity;
import com.intersofteagles.tictactoe.Commoners.MyBounceInter;
import com.intersofteagles.tictactoe.POJOs.Player;
import com.intersofteagles.tictactoe.R;

import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SignIn extends AppCompatActivity {

    @BindView(R.id.username)TextView username;
    @BindView(R.id.password)TextView password;
    @BindView(R.id.email)AutoCompleteTextView email;
    @BindView(R.id.root)View root;
    @BindView(R.id.details)LinearLayout content;

    private ProgressDialog pdiag;
    private FirebaseAuth auth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private DatabaseReference database;
    private boolean login = false;
    private FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        ButterKnife.bind(this);


        database = FirebaseDatabase.getInstance().getReference();

        pdiag = new ProgressDialog(this,R.style.LightTheme);
        pdiag.setCancelable(false);
        pdiag.setMessage("Please wait ...");
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,R.layout.spinner_item_dark);
        Pattern emailPattern = Patterns.EMAIL_ADDRESS; // API level 8+
        Account[] accounts = AccountManager.get(this).getAccounts();
        for (Account account : accounts) {
            if (emailPattern.matcher(account.name).matches()) {
                String possibleEmail = account.name;
                adapter.add(possibleEmail);
            }
        }
        email.setAdapter(adapter);
        email.setThreshold(1);

        auth = FirebaseAuth.getInstance();

        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(FirebaseAuth firebaseAuth) {
                user = firebaseAuth.getCurrentUser();
                if (user != null){
                    if (login){
                        pdiag.dismiss();
                        finish();
                    }else {
                        Player player = new Player(user.getUid(),username.getText().toString().trim(),email.getText().toString());
                        player.setLast_seen(System.currentTimeMillis());
                        database.child("players").child(user.getUid()).setValue(player);
                        UserProfileChangeRequest request = new UserProfileChangeRequest.Builder().setDisplayName(player.getUsername())
                                .build();
                        user.updateProfile(request).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(Task<Void> task) {
                                pdiag.dismiss();
                                finish();
                            }
                        });
                    }
                }
            }
        };

    }




    public void signUp(View v){
        if (!validated(username,email,password))return;
        login = false;
        pdiag.show();
        auth.createUserWithEmailAndPassword(email.getText().toString().trim(),password.getText().toString().trim()).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(Exception e) {
                pdiag.dismiss();
                handleError(e);
            }
        });

    }


    public void signIn(View v){
        if (!validated(email,password))return;
        login = true;
        pdiag.show();
        auth.signInWithEmailAndPassword(email.getText().toString().trim(), password.getText().toString().trim()).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(Exception e) {
                pdiag.dismiss();
                handleError(e);
            }
        });

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

    public boolean validated(TextView ... tx){
        boolean ok = true;
        for(TextView t:tx){
            if (TextUtils.isEmpty(t.getText().toString())){
                ok = false;
                t.setError("Required");
            }
        }
        if (ok){
            if (password.getText().toString().length()<6){
                ok = false;
                Snackbar.make(root,"Password must be at least 6 characters",Snackbar.LENGTH_LONG).show();
            }
        }
        return ok;
    }


    @Override
    protected void onStart() {
        super.onStart();
        auth.addAuthStateListener(authStateListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        auth.removeAuthStateListener(authStateListener);
    }



    public void handleError(Exception e){
        pdiag.dismiss();
        try {
            throw e;
        } catch (FirebaseAuthUserCollisionException | FirebaseAuthInvalidCredentialsException es) {
            Snackbar.make(root, es.getMessage(), Snackbar.LENGTH_LONG).show();
        } catch (FirebaseNetworkException se) {
            Snackbar.make(root, "You are not connected", Snackbar.LENGTH_SHORT).show();
        } catch (FirebaseAuthInvalidUserException e1) {
            Snackbar.make(root,"User not found",Snackbar.LENGTH_SHORT).show();
        } catch (FirebaseAuthException e1) {
            Toast.makeText(this, e1.getMessage(), Toast.LENGTH_SHORT).show();
        }catch (Exception e1) {
            Toast.makeText(this, "Unknown error", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finishAffinity();
    }
}
