package com.bizbrolly;

import com.bizbrolly.entities.AddDbDetailsResponse;
import com.bizbrolly.entities.AddDetailsResponse;
import com.bizbrolly.entities.GetDbDetailsResponse;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;

/**
 * Created by Akash on 19/05/17.
 */

public class WebServiceRequests {
    private static final WebServiceRequests ourInstance = new WebServiceRequests();

    public static WebServiceRequests getInstance() {
        return ourInstance;
    }

    private WebServiceRequests() {
    }

    public void getDb(String email, String networkPassword, Callback<GetDbDetailsResponse> responseCallback) {
        HashMap<String, Object> params = new HashMap<>();
        params.put(Constants.Keys.APIKey, Constants.API_KEY);
        params.put(Constants.Keys.Email, email);
        params.put(Constants.Keys.NetworkPassword, networkPassword);

        Call<GetDbDetailsResponse> call = ApiClient.getClient().create(ApiInterface.class).getDb(params);
        call.enqueue(responseCallback);
    }

    public void addDbDetails(String email, String dbScript, Callback<AddDbDetailsResponse> responseCallback) {
        HashMap<String, Object> params = new HashMap<>();
        params.put(Constants.Keys.APIKey, Constants.API_KEY);
        params.put(Constants.Keys.Email, email);
        params.put(Constants.Keys.DBScript, dbScript);

        Call<AddDbDetailsResponse> call = ApiClient.getClient().create(ApiInterface.class).saveDb(params);
        call.enqueue(responseCallback);
    }

    public void createUser(String email, String networkPassword, Callback<AddDetailsResponse> responseCallback) {
        HashMap<String, Object> params = new HashMap<>();
        params.put(Constants.Keys.APIKey, Constants.API_KEY);
        params.put(Constants.Keys.Email, email);
        params.put(Constants.Keys.NetworkPassword, networkPassword);

        Call<AddDetailsResponse> call = ApiClient.getClient().create(ApiInterface.class).createUser(params);
        call.enqueue(responseCallback);
    }
}
