package com.example.ashwani.rxcountry;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final TextView textView = findViewById(R.id.hello);

        Call<Worldpopulation> call = CountryApi.getClient().getWorld();

        call.enqueue(new Callback<Worldpopulation>() {
            @Override
            public void onResponse(Call<Worldpopulation> call, Response<Worldpopulation> response) {
                Log.d("Main", "onResponse: " + response);

                if (response.body() != null) {

                    Log.d("MainActivity", "onResponse: " + response.body().getCountryList());
                    try {
                        List<Country> countryList = response.body().getCountryList();
                        for (Country c : countryList) {
                            textView.append(c.getRank()+". "+ c.getCountry() + "\t" + c.getFlag() + "\n" + c.getPopulation()+"\n");
                        }
                    } catch (Exception e) {
                        textView.setText("Exception occurred");
                        e.printStackTrace();
                    }
                }

            }


            @Override
            public void onFailure(Call<Worldpopulation> call, Throwable t) {
                textView.setText("Failed");
            }
        });
    }
}
