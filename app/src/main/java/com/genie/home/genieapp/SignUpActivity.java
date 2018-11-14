package com.genie.home.genieapp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.genie.home.genieapp.auth.UserDto;
import com.genie.home.genieapp.auth.UserRegistrationError;
import com.genie.home.genieapp.auth.UserService;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SignUpActivity extends AppCompatActivity implements View.OnClickListener {

    @BindView(R.id.tvName)
    TextView TvName;
    @BindView(R.id.tvEmail)
    TextView TvEmail;
    @BindView(R.id.tvPassword)
    TextView TvPassword;
    @BindView(R.id.tvMatchPassword)
    TextView TvMatchPassword;
    @BindView(R.id.progressBar)
    ProgressBar progressBar;

    private Context context;
    private Handler handler;
    private MyRunnable<UserDto> onSuccess = new MyRunnable<UserDto>() {
        @Override
        public void run(UserDto userDto) {

            Intent iUsrHome = new Intent(SignUpActivity.this, LoginActivity.class);
            startActivity(iUsrHome);

            handler.post(new Runnable() {
                public void run() {
                    onWaitEnd();
                    Toast.makeText(context,
                            "Sign up successful !!\n" +
                                    "Activation link has been sent to your email.",
                            Toast.LENGTH_LONG).show();
                    finish();
                }
            });
        }
    };
    private MyRunnable<UserRegistrationError> onValidationFailed = new MyRunnable<UserRegistrationError>() {
        @Override
        public void run(UserRegistrationError validationError) {
            handler.post(new Runnable() {
                public void run() {
                    onWaitEnd();
                    Toast.makeText(context,
                            "Form validation failed.\nPlease correct and resubmit.", Toast.LENGTH_LONG).show();
                }
            });
        }
    };
    private MyRunnable<Exception> onFailure = new MyRunnable<Exception>() {
        @Override
        public void run(final Exception e) {
            e.printStackTrace();
            handler.post(new Runnable() {
                public void run() {
                    onWaitEnd();
                    Toast.makeText(context,
                            "Something went wrong. Try again later\n" +
                                    e.getMessage(), Toast.LENGTH_LONG).show();
                }
            });
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        ButterKnife.bind(this);

        context = getApplicationContext();
        handler = new Handler(context.getMainLooper());
    }

    @Override
    @OnClick({R.id.btnSignUp, R.id.tvAlreadyHaveAnAccountLogin})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnSignUp:
                String name = TvName.getText().toString();
                String email = TvEmail.getText().toString();
                String password = TvPassword.getText().toString();
                String matchPassword = TvMatchPassword.getText().toString();

                register(name, email, password, matchPassword);
                break;
            case R.id.tvAlreadyHaveAnAccountLogin:
                finish();
                break;
        }
    }

    public void register(String name, String email, String password, String matchPassword) {
        UserDto userDto = new UserDto();
        userDto.setEmail(email);
        userDto.setName(name);
        userDto.setPassword(password);
        userDto.setMatchingPassword(matchPassword);

        UserService.registerUser(userDto, onSuccess, onValidationFailed, onFailure);
        onWaitStart();
    }

    private void onWaitStart() {
        progressBar.setVisibility(ProgressBar.VISIBLE);
        TvName.setEnabled(false);
        TvName.setEnabled(false);
        TvPassword.setEnabled(false);
        TvMatchPassword.setEnabled(false);
        findViewById(R.id.btnSignUp).setEnabled(false);
        findViewById(R.id.tvAlreadyHaveAnAccountLogin).setEnabled(false);
    }

    private void onWaitEnd() {
        progressBar.setVisibility(ProgressBar.GONE);
        TvName.setEnabled(true);
        TvName.setEnabled(true);
        TvPassword.setEnabled(true);
        TvMatchPassword.setEnabled(true);
        findViewById(R.id.btnSignUp).setEnabled(true);
        findViewById(R.id.tvAlreadyHaveAnAccountLogin).setEnabled(true);
    }
}
