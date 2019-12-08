package com.asish.musik;

import com.asish.musik.models.Status;

import retrofit2.Call;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface ApiInterfaceLyrics {

    String BASE_URL = "https://api.audd.io";
    @POST("/findLyrics/")
    Call<Status> getLyrics(@Query("q") String songName, @Query("api_token") String api_token);
//    Call<Status> getLyrics();

}
