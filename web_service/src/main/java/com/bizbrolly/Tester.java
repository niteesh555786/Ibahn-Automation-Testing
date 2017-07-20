package com.bizbrolly;

import com.bizbrolly.entities.GetDbDetailsResponse;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Tester {

    public static void main(String... args){
        Tester tester = new Tester();
        tester.testGetDb();
    }

    private void testGetDb(){
        log("Akash");
        log("Testing GetDb");
        WebServiceRequests.getInstance().getDb(
                "rachit.gupta@bizbrolly.com",
                "biz@123",
                new Callback<GetDbDetailsResponse>() {
                    @Override
                    public void onResponse(Call<GetDbDetailsResponse> call, Response<GetDbDetailsResponse> response) {
                        log("Got response");
                        JsonElement jsonObject = new JsonParser().parse(response.body().getGetDBDetailsResult().getData().getDBScript());
                        log(jsonObject.toString());
                    }

                    @Override
                    public void onFailure(Call<GetDbDetailsResponse> call, Throwable t) {
                        log("Failure");
                        log(t.getMessage());
                    }
                }
        );
    }

    private void log(String message){
        System.out.println("WebServiceTest: "+message);
    }

}
