package com.genie.home.genieapp.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.genie.home.genieapp.MyRunnable;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Credentials;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static com.genie.home.genieapp.Commons.COL;
import static com.genie.home.genieapp.Commons.GENIE_HOST;
import static com.genie.home.genieapp.Commons.GENIE_PORT;
import static com.genie.home.genieapp.Commons.HTTP;
import static com.genie.home.genieapp.Commons.LOGIN_PATH;
import static com.genie.home.genieapp.Commons.LOGOUT_PATH;
import static com.genie.home.genieapp.Commons.REGISTRATION_PATH;

public class UserService {

    private static OkHttpClient httpClient = new OkHttpClient();
    private static ObjectMapper mapper = new ObjectMapper();

    private static LoginCredentials credentials;

    public static CountDownLatch registerUser(final UserDto userDto,
                                              final MyRunnable<UserDto> onSuccess,
                                              final MyRunnable<UserRegistrationError> onValidationFailed,
                                              final MyRunnable<Exception> onFailure) {
        RequestBody requestBody = null;
        try {
            requestBody = RequestBody.create(
                    MediaType.parse("application/json"),
                    mapper.writeValueAsString(userDto));
        } catch (IOException e) {
            e.printStackTrace();
        }
        Request request = new Request.Builder()
                .url(HTTP + GENIE_HOST + COL + GENIE_PORT + REGISTRATION_PATH)
                .post(requestBody)
                .build();

        final CountDownLatch countDownLatch = new CountDownLatch(1);
        httpClient.newCall(request)
                .enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        try {
                            onFailure.run(e);
                        } finally {
                            countDownLatch.countDown();
                        }
                    }

                    @Override
                    public void onResponse(Call call, final Response response) {
                        if (response.isSuccessful()) {
                            try {
                                onSuccess.run(userDto);
                            } finally {
                                countDownLatch.countDown();
                            }
                        } else if (response.code() == 400) {
                            try {
                                String responseStr = response.body().string();
                                UserRegistrationError validationError =
                                        mapper.readValue(responseStr, UserRegistrationError.class);
                                onValidationFailed.run(validationError);
                            } catch (IOException e) {
                                onFailure.run(e);
                            } finally {
                                countDownLatch.countDown();
                            }
                        }
                    }
                });

        return countDownLatch;
    }

    public static CountDownLatch attemptLogin(final LoginCredentials credentials,
                              final MyRunnable<LoginCredentials> onSuccess,
                              final MyRunnable<LoginCredentials> onUnauthorized,
                              final MyRunnable<Exception> onFailure) {

        RequestBody requestBody = RequestBody.create(null, new byte[0]);
        Request request = new Request.Builder()
                .url(HTTP + GENIE_HOST + COL + GENIE_PORT + LOGIN_PATH)
                .header("Authorization", Credentials.basic(
                        credentials.getUsername(), credentials.getPassword()))
                .post(requestBody)
                .build();

        final CountDownLatch countDownLatch = new CountDownLatch(1);
        httpClient.newCall(request)
                .enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        try {
                            onFailure.run(e);
                        } finally {
                            countDownLatch.countDown();
                        }
                    }

                    @Override
                    public void onResponse(Call call, final Response response) {
                        if (response.isSuccessful()) {
                            try {
                                UserService.credentials = credentials;
                                onSuccess.run(credentials);
                            } finally {
                                countDownLatch.countDown();
                            }
                        } else if (response.code() == 401) {
                            try {
                                onUnauthorized.run(credentials);
                            } finally {
                                countDownLatch.countDown();
                            }
                        }
                    }
                });

        return countDownLatch;
    }

    public static CountDownLatch attemptLogout(final MyRunnable<LoginCredentials> onSuccess,
                                               final MyRunnable<Exception> onFailure) {
        RequestBody requestBody = RequestBody.create(null, new byte[0]);
        Request request = new Request.Builder()
                .url(HTTP + GENIE_HOST + COL + GENIE_PORT + LOGOUT_PATH)
                .post(requestBody)
                .build();

        final CountDownLatch countDownLatch = new CountDownLatch(1);
        httpClient.newCall(request)
                .enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        try {
                            onFailure.run(e);
                        } finally {
                            countDownLatch.countDown();
                        }
                    }

                    @Override
                    public void onResponse(Call call, final Response response) {
                        if (response.isSuccessful()) {
                            try {
                                onSuccess.run(credentials);
                            } finally {
                                countDownLatch.countDown();
                            }
                        }
                    }
                });

        return countDownLatch;
    }
}
