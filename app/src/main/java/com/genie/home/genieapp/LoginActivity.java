package com.genie.home.genieapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.genie.home.genieapp.auth.LoginCredentials;
import com.genie.home.genieapp.auth.LoginService;

import java.util.concurrent.CountDownLatch;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = LoginActivity.class.getSimpleName();

    public static final String MyPREFERENCES = "LoginPrefs";

    @BindView(R.id.tvUsrName)
    EditText TvUserName;
    @BindView(R.id.tvPassword)
    EditText TvPassword;
    @BindView(R.id.progressBar)
    ProgressBar progressBar;

    private Context context;
    private SharedPreferences sharedPreferences;
    private Handler handler;

    private MyRunnable<LoginCredentials> onSuccess = new MyRunnable<LoginCredentials>() {
        @Override
        public void run(LoginCredentials loginCredentials) {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("username", loginCredentials.getUsername());
            editor.putString("password", loginCredentials.getPassword());
            editor.apply();

            Intent iUsrHome = new Intent(LoginActivity.this, UserHomeActivity.class);
            iUsrHome.putExtra("username", loginCredentials.getUsername());
            startActivity(iUsrHome);

            handler.post(new Runnable() {
                public void run() {
                    onWaitEnd();
                    Toast.makeText(context,
                            "Login successful !!", Toast.LENGTH_SHORT).show();
                    finish();
                }
            });
        }
    };

    private MyRunnable<LoginCredentials> onUnauthorized = new MyRunnable<LoginCredentials>() {
        @Override
        public void run(LoginCredentials loginCredentials) {
            handler.post(new Runnable() {
                public void run() {
                    onWaitEnd();
                    Toast.makeText(context,
                            "Login failed! Invalid credentials supplied", Toast.LENGTH_SHORT).show();
                }
            });
        }
    };

    private MyRunnable<Exception> onFailure = new MyRunnable<Exception>() {
        @Override
        public void run(Exception e) {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("username", null);
            editor.putString("password", null);
            editor.apply();

            e.printStackTrace();
            handler.post(new Runnable() {
                public void run() {
                    onWaitEnd();
                    Toast.makeText(context,
                            "Something went wrong. Try again later", Toast.LENGTH_LONG).show();
                }
            });
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        context = getApplicationContext();
        sharedPreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        handler = new Handler(context.getMainLooper());
    }

    @Override
    protected void onResume() {
        super.onResume();
        final String username = sharedPreferences.getString("username", null);
        final String password = sharedPreferences.getString("password", null);

        if (username != null && password != null) {
            attemptLogin(username, password);
        }
    }

    @Override
    @OnClick({R.id.btnLogin, R.id.btnSignUp})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnLogin:
                final String username = TvUserName.getText().toString();
                final String password = TvPassword.getText().toString();
                attemptLogin(username, password);
                break;
            case R.id.btnSignUp:
                Intent iSignup = new Intent(LoginActivity.this, SignUpActivity.class);
                startActivity(iSignup);
                break;
        }
    }

    private void attemptLogin(String username, String password) {
        LoginCredentials credentials = new LoginCredentials(username, password);

        CountDownLatch countDownLatch = LoginService.attemptLogin(
                credentials,
                onSuccess,
                onUnauthorized,
                onFailure);
        onWaitStart();
    }

    private void onWaitStart() {
        progressBar.setVisibility(ProgressBar.VISIBLE);
        TvUserName.setEnabled(false);
        TvPassword.setEnabled(false);
        findViewById(R.id.btnLogin).setEnabled(false);
        findViewById(R.id.btnSignUp).setEnabled(false);
    }

    private void onWaitEnd() {
        progressBar.setVisibility(ProgressBar.GONE);
        TvUserName.setEnabled(true);
        TvPassword.setEnabled(true);
        findViewById(R.id.btnLogin).setEnabled(true);
        findViewById(R.id.btnSignUp).setEnabled(true);
    }
}
