package com.geene.home.geeneapp;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int SPLASH_TIME_OUT = 3000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent iLogin = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(iLogin);
                finish();
            }
        }, SPLASH_TIME_OUT);

        findViewById(R.id.btnLoginPg).setOnClickListener(this);
        findViewById(R.id.btnSignUpPg).setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnLoginPg:
                Intent iLogin = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(iLogin);
                break;
            case R.id.btnSignUpPg:
                Intent iSignup = new Intent(MainActivity.this, SignUpActivity.class);
                startActivity(iSignup);
                break;
        }
    }
}
