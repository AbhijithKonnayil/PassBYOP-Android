package com.example.abhi.passbyop;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface Api {
    String BASE_URL ="http://192.168.43.95:8000";

    @GET("endpoint")
    Call<RetroUserModel> getUser();
}
