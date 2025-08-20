package com.accessibility.seeit.asl.net;


import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class LanguageToolClient {
    private final LanguageToolApi api;

    /**
     * @param baseUrl e.g. "https://api.languagetool.org" or "http://10.0.2.2:8081"
     * @param apiKey  nullable; pass if you use paid cloud
     */
    public LanguageToolClient(String baseUrl, String apiKey) {
        OkHttpClient.Builder ok = new OkHttpClient.Builder()
                .callTimeout(10, TimeUnit.SECONDS)
                .connectTimeout(5, TimeUnit.SECONDS)
                .readTimeout(10, TimeUnit.SECONDS);

        if (apiKey != null && !apiKey.isEmpty()) {
            ok.addInterceptor((Interceptor) chain -> {
                Request req = chain.request().newBuilder()
                        .header("Authorization", "Bearer " + apiKey)
                        .header("User-Agent", "SeeIt/1.0 (Android)")
                        .build();
                return chain.proceed(req);
            });
        } else {
            ok.addInterceptor(chain -> chain.proceed(
                    chain.request().newBuilder()
                            .header("User-Agent", "SeeIt/1.0 (Android)")
                            .build()
            ));
        }

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(ensureEndsWithSlash(baseUrl))
                .client(ok.build())
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        this.api = retrofit.create(LanguageToolApi.class);
    }

    public LanguageToolApi api() { return api; }

    private static String ensureEndsWithSlash(String url) {
        return url.endsWith("/") ? url : (url + "/");
    }
}
