package com.accessibility.seeit.asl.net;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface LanguageToolApi {
    @FormUrlEncoded
    @POST("/v2/check")
    Call<CheckResponse> check(
            @Field("text") String text,
            @Field("language") String language,
            @Field("level") String level,          // e.g. "default" or "picky"
            @Field("enabledOnly") boolean enabledOnly
    );
}