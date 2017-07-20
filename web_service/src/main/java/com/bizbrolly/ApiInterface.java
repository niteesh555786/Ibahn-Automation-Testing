package com.bizbrolly;


import com.bizbrolly.entities.AddDbDetailsResponse;
import com.bizbrolly.entities.AddDetailsResponse;
import com.bizbrolly.entities.GetDbDetailsResponse;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

/**
 * Created by Akash on 14/05/17.
 */

public interface ApiInterface {

    @POST(Constants.Partials.GetDBDetails)
    Call<GetDbDetailsResponse> getDb(
            @Body HashMap<String, Object> body
    );

    @POST(Constants.Partials.AddDBDetails)
    Call<AddDbDetailsResponse> saveDb(
            @Body HashMap<String, Object> body
    );

    @POST(Constants.Partials.AddDetails)
    Call<AddDetailsResponse> createUser(
            @Body HashMap<String, Object> body
    );
}
