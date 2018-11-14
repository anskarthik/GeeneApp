package com.genie.home.genieapp;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.genie.home.genieapp.auth.LoginCredentials;
import com.genie.home.genieapp.auth.LoginService;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Credentials;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static com.genie.home.genieapp.Commons.COL;
import static com.genie.home.genieapp.Commons.GENIE_HOST;
import static com.genie.home.genieapp.Commons.GENIE_PORT;
import static com.genie.home.genieapp.Commons.HTTP;
import static com.genie.home.genieapp.Commons.LOGIN_PATH;
import static com.genie.home.genieapp.Commons.REGISTRATION_PATH;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = LoginActivity.class.getSimpleName();

    public static final String MyPREFERENCES = "LoginPrefs";

    private EditText TvUserName;
    private EditText TvPassword;

    LoginService loginService;
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
                    finish();
                    Toast.makeText(context,
                            "Login successful !!", Toast.LENGTH_SHORT).show();
                }
            });
        }
    };

    private MyRunnable<LoginCredentials> onUnauthorized = new MyRunnable<LoginCredentials>() {
        @Override
        public void run(LoginCredentials loginCredentials) {
            handler.post(new Runnable() {
                public void run() {
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

        loginService = new LoginService();
        context = getApplicationContext();
        sharedPreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        handler = new Handler(context.getMainLooper());

        String username = sharedPreferences.getString("username", null);
        String password = sharedPreferences.getString("password", null);

        if (username != null && password != null) {
            attemptLogin(username, password);
        }

        TvUserName = findViewById(R.id.tvUsrName);
        TvPassword = findViewById(R.id.tvPassword);

        findViewById(R.id.btnLogin).setOnClickListener(this);
        findViewById(R.id.btnSignUp).setOnClickListener(this);
    }

    @Override
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

    private void attemptLogin(final String username, final String password) {
        LoginCredentials credentials = new LoginCredentials(username, password);

        CountDownLatch countDownLatch = loginService.attemptLogin(
                credentials,
                onSuccess,
                onUnauthorized,
                onFailure);

        ProgressDialog dialog = new ProgressDialog(this); // this = YourActivity
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setMessage("Loading. Please wait...");
        dialog.setIndeterminate(true);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();

        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        dialog.cancel();
    }
}
