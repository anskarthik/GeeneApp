package com.genie.home.genieapp;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int SPLASH_TIME_OUT = 5000;
    private Handler delayedHandler = new Handler();
    private volatile boolean transitionHappened = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        if (savedInstanceState == null) {
            delayedHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    doTransition();
                }
            }, SPLASH_TIME_OUT);
        }
    }

    @Override
    @OnClick(R.id.imgLogo)
    public void onClick(View view) {
        doTransition();
    }

    private synchronized void doTransition() {
        if (!transitionHappened) {
            Intent iLogin = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(iLogin);
            transitionHappened = true;
            finish();
        }
    }

    @Override
    protected void onDestroy() {
        transitionHappened = true;
        super.onDestroy();
    }
}
