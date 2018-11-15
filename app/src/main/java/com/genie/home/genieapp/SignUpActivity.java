package com.genie.home.genieapp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.util.Patterns;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.genie.home.genieapp.auth.FieldValidationError;
import com.genie.home.genieapp.auth.UserDto;
import com.genie.home.genieapp.auth.UserRegistrationError;
import com.genie.home.genieapp.auth.UserService;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SignUpActivity extends AppCompatActivity implements View.OnClickListener {

    @BindView(R.id.tvName)
    TextInputEditText TvName;
    @BindView(R.id.tvEmail)
    TextInputEditText TvEmail;
    @BindView(R.id.tvPassword)
    TextInputEditText TvPassword;
    @BindView(R.id.tvMatchPassword)
    TextInputEditText TvMatchPassword;
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
        public void run(final UserRegistrationError validationError) {
            handler.post(new Runnable() {
                public void run() {
                    onWaitEnd();

                    for (FieldValidationError fve : validationError.getFieldErrors()) {
                        if ("name".equalsIgnoreCase(fve.getField())) {
                            TvName.setError(fve.getMessage());
                        }
                        if ("email".equalsIgnoreCase(fve.getField())) {
                            TvEmail.setError(fve.getMessage());
                        }
                        if ("password".equalsIgnoreCase(fve.getField())) {
                            TvPassword.setError(fve.getMessage());
                        }
                        if ("matchingPassword".equalsIgnoreCase(fve.getField())) {
                            TvMatchPassword.setError(fve.getMessage());
                        }
                    }

                    for (String error : validationError.getObjectErrors()) {
                        if (error.contains("match")) {
                            TvMatchPassword.setError(error);
                        }
                        if (error.contains("email")) {
                            TvEmail.setError(error);
                        }
                    }

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

                if (validate(name, email, password, matchPassword)) {
                    register(name, email, password, matchPassword);
                }
                break;
            case R.id.tvAlreadyHaveAnAccountLogin:
                finish();
                break;
        }
    }

    private boolean validate(String name, String email, String password, String matchPassword) {
        boolean anyErrors = false;

        if (name == null || name.isEmpty() || name.length() < 2) {
            TvName.setError("Enter a name of at least 2 characters");
            anyErrors = true;
        }
        if (email == null || email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            TvEmail.setError("Not a valid email");
            anyErrors = true;
        }
        if (password == null || password.isEmpty()) {
            TvPassword.setError("This field can't be empty");
            anyErrors = true;
        }
        if (matchPassword == null || matchPassword.isEmpty()) {
            TvMatchPassword.setError("This field can't be empty");
            anyErrors = true;
        }

        return !anyErrors;
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
