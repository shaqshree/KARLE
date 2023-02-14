package com.example.karle;

import android.annotation.SuppressLint;
import android.content.Context;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textfield.TextInputEditText;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
public class WeatherRVAdapter extends RecyclerView.Adapter<WeatherRVAdapter.ViewHolder>{
    private Context context;
    private ArrayList<WeatherRVModal> weatherRVModalArrayList;

    public WeatherRVAdapter(Context context, ArrayList<WeatherRVModal> weatherRVModalArrayList) {
        this.context = context;
        this.weatherRVModalArrayList = weatherRVModalArrayList;
    }

    @NonNull
    @Override
    public WeatherRVAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view=LayoutInflater.from(context).inflate(R.layout.weather_rv_item,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WeatherRVAdapter.ViewHolder holder, int position) {
        WeatherRVModal modal = weatherRVModalArrayList.get(position);
        holder.temperatureTV.setText(modal.getTemperature() + "°C");
        holder.windTV.setText(modal.getWindSpeed() + "km/h");
        Picasso.get().load("http:".concat(modal.getIcon())).into(holder.conditionIV);
        SimpleDateFormat input = new SimpleDateFormat("yyyy-MM-dd hh:mm");
        SimpleDateFormat output = new SimpleDateFormat("hh:mm aa");
        try {
            Date t = input.parse(modal.getTime());
            holder.timeTV.setText(output.format(t));

        } catch (ParseException e){
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount(){
        return weatherRVModalArrayList.size();

    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView windTV,timeTV,temperatureTV;
        private ImageView conditionIV;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            windTV=itemView.findViewById(R.id.idTVWindSpeed);
            timeTV=itemView.findViewById(R.id.idTVTime);
            temperatureTV=itemView.findViewById(R.id.idTVTemperature);
            conditionIV=itemView.findViewById(R.id.idIVCondition);

        }
    }
}