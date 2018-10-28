package com.genie.home.genieapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class UserHomeActivity extends AppCompatActivity {

    private String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_home);

        Intent intent = getIntent();
        username = intent.getStringExtra("username");

        TextView tvUsernameDisplay = findViewById(R.id.tvUsernameDisplay);
        tvUsernameDisplay.setText(getString(R.string.hi_user, username));
    }
}