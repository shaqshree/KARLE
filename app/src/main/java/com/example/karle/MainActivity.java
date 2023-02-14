package com.example.karle;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.textfield.TextInputEditText;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private RelativeLayout homeRL;
    private ProgressBar loadingPB;
    private TextView cityNameTV,temperatureTV,conditionTV,co1,no2,so2;
    private RecyclerView weatherRV;
    private TextInputEditText cityEdt;
    private ImageView backIV,iconIV,searchIV;
    private ArrayList<WeatherRVModal>weatherRVModalArrayList;
    private WeatherRVAdapter weatherRVAdapter;
    private LocationManager locationManager;
    private int PERMISSION_CODE =1;
    private String cityName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        setContentView(R.layout.activity_main);
        homeRL =findViewById(R.id.idRLhome);
        co1=findViewById(R.id.idTVCo);
        so2=findViewById(R.id.idTVSo2);
        no2=findViewById(R.id.idTVno2);
        loadingPB =findViewById(R.id.idPBLoading);
        cityNameTV =findViewById(R.id.idTVCityName);
        temperatureTV =findViewById(R.id.idTVTemperature);
        conditionTV =findViewById(R.id.idTVCondition);
        weatherRV =findViewById(R.id.idRVWeather);
        cityEdt =findViewById(R.id.idEDtCity);
        backIV =findViewById(R.id.id1back);
        iconIV =findViewById(R.id.idIVIcon);
        searchIV =findViewById(R.id.idIVSearch);
        weatherRVModalArrayList = new ArrayList<>();
        weatherRVAdapter=new WeatherRVAdapter(this,weatherRVModalArrayList);
        weatherRV.setAdapter(weatherRVAdapter);

        locationManager=(LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,Manifest.permission.ACCESS_COARSE_LOCATION)!=PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION},PERMISSION_CODE);

        }

        Location location=locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        if(location!=null){
            cityName=getCityName(location.getLongitude(), location.getLatitude());
            getWeatherInfo(cityName);}
        else{
            cityName="London";
            getWeatherInfo(cityName);
        }

        searchIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String city = cityEdt.getText().toString();
                if(city.isEmpty()){
                    Toast.makeText(MainActivity.this, "Please enter city name", Toast.LENGTH_SHORT).show();
                }else{
                    cityNameTV.setText(cityName);
                    getWeatherInfo(city);
                }
            }
        });





    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode==PERMISSION_CODE){
            if(grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED){
                Toast.makeText(this, "Permission granted", Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(this, "Please Provide the Permissions", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    private String getCityName(double longitude, double latitude){
        String cityName ="Not Found";
        Geocoder gcd =new Geocoder(getBaseContext(), Locale.getDefault());
        try{
            List<Address> addresses=gcd.getFromLocation(latitude,longitude,10);
            for(Address adr :addresses){
                if(adr!=null){
                    String city =adr.getLocality();
                    if(city!=null && !city.equals("")){
                        cityName=city;

                    }else{
                        Log.d("TAG","CITY NOT FOUND ");
                        Toast.makeText(this, "User City Not Found..", Toast.LENGTH_SHORT).show();
                    }
                }
            }

        }catch (IOException e){
            e.printStackTrace();
        }
        return  cityName;
    }
    private void getWeatherInfo(String cityName){
        String url="http://api.weatherapi.com/v1/forecast.json?key=fd1a60ddf0154966ad6181959231202&q="+cityName+"&days=1&aqi=yes&alerts=yes";
        cityNameTV.setText(cityName);
        RequestQueue requestQueue = Volley.newRequestQueue(MainActivity.this);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                loadingPB.setVisibility(View.GONE);
                homeRL.setVisibility(View.VISIBLE);
                weatherRVModalArrayList.clear();

                try {
                    String co = response.getJSONObject("current").getJSONObject("air_quality").getString("co");
                    String cosub=co.substring(0,5);
                    co1.setText("co:"+cosub+" (μg/m3)");
                    String so2info = response.getJSONObject("current").getJSONObject("air_quality").getString("so2");
                    String so2sub=so2info.substring(0,4);
                    so2.setText("so2:"+so2sub+" (μg/m3)");
                    String no2info = response.getJSONObject("current").getJSONObject("air_quality").getString("no2");
                    String no2sub=co.substring(0,4);
                    no2.setText("no2:"+no2sub+" (μg/m3)");

                    String temperature = response.getJSONObject("current").getString("temp_c");
                    temperatureTV.setText(temperature+"°C");
                    int isDay = response.getJSONObject("current").getInt("is_day");

                    String condition=response.getJSONObject("current").optJSONObject("condition").getString("text");
                    String conditionIcon=response.getJSONObject("current").optJSONObject("condition").getString("icon");

                    Picasso.get().load("http:".concat(conditionIcon)).into(iconIV);
                    conditionTV.setText(condition);
                    if(isDay==1){
                        Picasso.get().load("https://i.pinimg.com/736x/f6/5b/9f/f65b9f99f8518bd59a4c6849c39b4f0c.jpg").into(backIV);
                    }else{
                        Picasso.get().load("https://th.bing.com/th/id/R.9881c0025b58aa9d367bbd4bc7911a05?rik=71KqlQ0hDwfQ%2bw&riu=http%3a%2f%2fwww.pixelstalk.net%2fwp-content%2fuploads%2f2016%2f06%2fDownlaod-HD-Space-iPhone-Wallpapers.jpg&ehk=zatcaCuYgSHeS3A4seWAS3fQWESJ4Brs3XLpYRcY0uk%3d&risl=&pid=ImgRaw&r=0").into(backIV);
                    }



                    JSONObject forecastObj = response.getJSONObject("forecast");
                    JSONObject forecastO =forecastObj.getJSONArray("forecastday").getJSONObject(0);
                    JSONArray hourArray =forecastO.getJSONArray("hour");
                    for(int i=0;i< hourArray.length();i++){
                        JSONObject hourObj =hourArray.getJSONObject(i);
                        String time = hourObj.getString("time");
                        String temper =hourObj.getString("temp_c");
                        String img = hourObj.getJSONObject("condition").getString("icon");
                        String wind =hourObj.getString("wind_kph");
                        weatherRVModalArrayList.add(new WeatherRVModal(time,temper,img,wind));

                    }
                    weatherRVAdapter.notifyDataSetChanged();

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }


        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(MainActivity.this, "Please enter valid city name", Toast.LENGTH_SHORT).show();
            }
        });
        requestQueue.add(jsonObjectRequest);
    }
}