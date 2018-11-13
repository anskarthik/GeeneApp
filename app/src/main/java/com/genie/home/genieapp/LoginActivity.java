package com.genie.home.genieapp;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.io.IOException;

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

    private OkHttpClient httpClient = new OkHttpClient();

    private EditText TvUserName;
    private EditText TvPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        TvUserName = findViewById(R.id.tvUsrName);
        TvPassword = findViewById(R.id.tvPassword);

        findViewById(R.id.btnLogin).setOnClickListener(this);
        findViewById(R.id.btnSignUp).setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnLogin:
                String username = TvUserName.getText().toString();
                String password = TvPassword.getText().toString();

                RequestBody requestBody = RequestBody.create(null, new byte[0]);
                Request request = new Request.Builder()
                        .url(HTTP + GENIE_HOST + COL + GENIE_PORT + LOGIN_PATH)
                        .header("Authorization", Credentials.basic(username, password))
                        .post(requestBody)
                        .build();

                final Context context = getApplicationContext();
                final Handler handler =  new Handler(context.getMainLooper());
                httpClient.newCall(request)
                        .enqueue(new Callback() {
                            @Override
                            public void onFailure(Call call, IOException e) {
                                e.printStackTrace();
                                handler.post( new Runnable() {
                                    public void run() {
                                        Toast.makeText(context,
                                                "Something went wrong. Try again later", Toast.LENGTH_LONG).show();
                                    }
                                });
                            }

                            @Override
                            public void onResponse(Call call, final Response response) {
                                if (response.isSuccessful()) {
                                    handler.post( new Runnable() {
                                        public void run() {
                                            Toast.makeText(context,
                                                    "Login successful !!", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                    Intent iUsrHome = new Intent(LoginActivity.this, UserHomeActivity.class);
                                    iUsrHome.putExtra("username", TvUserName.getText().toString());
                                    startActivity(iUsrHome);
                                } else if (response.code() == 401) {
                                    handler.post( new Runnable() {
                                        public void run() {
                                            Toast.makeText(context,
                                                    "Login failed! Invalid credentials supplied", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                            }
                        });
                break;
            case R.id.btnSignUp:
                Intent iSignup = new Intent(LoginActivity.this, SignUpActivity.class);
                startActivity(iSignup);
                break;
        }
    }
}
