package com.genie.home.genieapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;
import android.widget.Toast;

import com.genie.home.genieapp.auth.LoginCredentials;
import com.genie.home.genieapp.auth.UserService;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.genie.home.genieapp.LoginActivity.MyPREFERENCES;

public class UserHomeActivity extends AppCompatActivity {

    private String username;

    private SharedPreferences sharedPreferences;
    private Handler handler;
    private Context context;

    @BindView(R.id.tvUsernameDisplay)
    TextView TvUsernameDisplay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_home);
        ButterKnife.bind(this);

        context = getApplicationContext();
        sharedPreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        handler = new Handler(context.getMainLooper());

        Intent intent = getIntent();
        username = intent.getStringExtra("username");
        TvUsernameDisplay.setText(getString(R.string.hi_user, username));
    }

    @OnClick(R.id.btnLogout)
    public void logout() {
        UserService.attemptLogout(
                new MyRunnable<LoginCredentials>() {
                    @Override
                    public void run(LoginCredentials loginCredentials) {
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("username", null);
                        editor.putString("password", null);
                        editor.apply();

                        Intent iUsrHome = new Intent(UserHomeActivity.this, LoginActivity.class);
                        startActivity(iUsrHome);

                        handler.post(new Runnable() {
                            public void run() {
                                Toast.makeText(context,
                                        "Logout successful !!", Toast.LENGTH_SHORT).show();
                                finish();
                            }
                        });
                    }
                },
                new MyRunnable<Exception>() {
                    @Override
                    public void run(Exception e) {
                        handler.post(new Runnable() {
                            public void run() {
                                Toast.makeText(context,
                                        "Logout failed!", Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                }
        );
    }
}
