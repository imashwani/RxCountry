package com.example.ashwani.rxcountry;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Worldpopulation {

    @SerializedName("worldpopulation")
    @Expose
    private List<Country> countries = null;

    public List<Country> getCountryList() {
        return countries;
    }

}