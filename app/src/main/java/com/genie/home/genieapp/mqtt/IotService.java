package com.genie.home.genieapp.mqtt;

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
import static com.genie.home.genieapp.Commons.IOT_PATH;
import static com.genie.home.genieapp.auth.UserService.credentials;

public class IotService {

    private static OkHttpClient httpClient = new OkHttpClient();
    private static ObjectMapper mapper = new ObjectMapper();

    public static CountDownLatch publishMessage(String macId, String message,
                                                final MyRunnable<String> onSuccess,
                                                final MyRunnable<String> onUnAuthorized,
                                                final MyRunnable<Exception> onFailure) {
        RequestBody requestBody = RequestBody.create(
                MediaType.parse("application/text"),
                message);
        Request request = new Request.Builder()
                .url(HTTP + GENIE_HOST + COL + GENIE_PORT + IOT_PATH + macId)
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
                                onSuccess.run("send is successful");
                            } finally {
                                countDownLatch.countDown();
                            }
                        } else if (response.code() == 400) {
                            try {
                                onUnAuthorized.run("operation is not authorized");
                            } finally {
                                countDownLatch.countDown();
                            }
                        }
                    }
                });

        return countDownLatch;
    }
}
