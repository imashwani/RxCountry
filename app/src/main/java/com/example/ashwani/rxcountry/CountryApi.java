package com.example.ashwani.rxcountry;

import android.content.Context;

import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

import java.util.List;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;

public class CountryApi {
    private final static String BASE_URL="http://www.androidbegin.com/";
    private static CountryService countryService=null;

    public static CountryService getClient() {
        if (countryService == null) {
            Retrofit retrofit = new Retrofit.Builder()
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .baseUrl(BASE_URL)
                    .build();
            countryService=retrofit.create(CountryService.class);
        }
        return countryService;
    }
    public interface CountryService{
        @GET("tutorial/jsonparsetutorial.txt")
        Call<Worldpopulation> getWorld();
    }
}
