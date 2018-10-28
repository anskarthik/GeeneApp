package com.genie.home.genieapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = LoginActivity.class.getSimpleName();

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
                Log.d(TAG, "username: " + TvUserName.getText());
                Log.d(TAG, "password: " + TvPassword.getText());

                Intent iUsrHome = new Intent(LoginActivity.this, UserHomeActivity.class);
                iUsrHome.putExtra("username", TvUserName.getText().toString());
                startActivity(iUsrHome);
                break;
            case R.id.btnSignUp:
                Intent iSignup = new Intent(LoginActivity.this, SignUpActivity.class);
                startActivity(iSignup);
                break;
        }
    }
}
