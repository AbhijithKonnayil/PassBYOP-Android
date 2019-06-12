package com.example.abhi.passbyop;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface Api {
    String BASE_URL ="http://192.168.43.95:8000";

    //@GET("endpoint")
    //Call<RetroUserModel> getUser();

    @POST("endpoint/")
    Call<RetroUserModel> getUser(@Body RetroUserModel userModel);

    @POST("endpoint/register")
    Call<RetroUserModel>createUser(@Body RetroUserModel userModel);
}
