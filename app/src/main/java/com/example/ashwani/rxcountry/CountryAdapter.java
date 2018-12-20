package com.example.ashwani.rxcountry;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;

public class CountryAdapter extends RecyclerView.Adapter<CountryAdapter.ViewHolder> {
    Context context = null;
    List<Country> countries;

    public CountryAdapter(List<Country> countryList) {
        countries = countryList;
    }

    @NonNull
    @Override
    public CountryAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.single_item, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CountryAdapter.ViewHolder holder, int position) {
        holder.population.setText(countries.get(position).getPopulation());
        holder.rank.setText(countries.get(position).getRank().toString());
        holder.name.setText(countries.get(position).getCountry());
        Glide.with(context).load(countries.get(position).getFlag()).into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        return countries.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView name, rank, population;
        ImageView imageView;

        public ViewHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name);
            rank = itemView.findViewById(R.id.rank);
            population = itemView.findViewById(R.id.population);
            imageView = itemView.findViewById(R.id.flag_image);
        }
    }
}
