package com.asish.musik;

import com.asish.musik.models.Status;

import retrofit2.Call;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface ApiInterfaceSong {
    String BASE_URL = "https://api.audd.io";
    @POST("/")
    Call<Status> getLyrics(@Query("return") String type, @Query("api_token") String api_token);
}
