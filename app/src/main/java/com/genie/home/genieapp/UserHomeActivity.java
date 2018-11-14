package com.genie.home.genieapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.genie.home.genieapp.auth.LoginCredentials;
import com.genie.home.genieapp.auth.LoginService;

import static com.genie.home.genieapp.LoginActivity.MyPREFERENCES;

public class UserHomeActivity extends AppCompatActivity {

    private String username;

    private Button BtnLogout;
    private SharedPreferences sharedPreferences;
    private Handler handler;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_home);

        context = getApplicationContext();
        sharedPreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        handler = new Handler(context.getMainLooper());

        Intent intent = getIntent();
        username = intent.getStringExtra("username");

        TextView tvUsernameDisplay = findViewById(R.id.tvUsernameDisplay);
        tvUsernameDisplay.setText(getString(R.string.hi_user, username));

        BtnLogout = findViewById(R.id.btnLogout);
        BtnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoginService.attemptLogout(
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
        });
    }
}
