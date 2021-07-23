package com.sanjay.weatherapp;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.sanjay.weatherapp.Connection.InternetCheckService;
import com.sanjay.weatherapp.databinding.ActivityMainBinding;
import com.sanjay.weatherapp.model.Weather;
import com.sanjay.weatherapp.model.WeatherResponse;
import com.sanjay.weatherapp.network.ApiClient;
import com.sanjay.weatherapp.network.ApiInterface;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    final long MIN_TIME = 5000;
    final float MIN_DISTANCE = 1000;
    final int REQUEST_CODE = 101;

    String locationProvider = LocationManager.GPS_PROVIDER;
    LocationManager locationManager;
    LocationListener locationListener;

    ActivityMainBinding binding;
    ApiInterface apiInterface;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //start internet service
        startService(new Intent(getBaseContext(), InternetCheckService.class));

        //get api client
        apiInterface = ApiClient.ApiBaseUrl().create(ApiInterface.class);

        // fetch current weather condition
        getWeatherForCurrentLocation();
    }

    // function to get weather from current location
    private void getWeatherForCurrentLocation() {
        try {
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            locationListener = new LocationListener() {
                @Override
                public void onLocationChanged(@NonNull Location location) {

                    String lat = String.valueOf(location.getLatitude());
                    String lang = String.valueOf(location.getLongitude());
                    Log.e("GPS_CO", lat + "," + lang);
                    getWeatherDetails(lat, lang);
                }
            };
            if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(MainActivity.this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        REQUEST_CODE);
            }
            locationManager.requestLocationUpdates(locationProvider, MIN_TIME, MIN_DISTANCE, locationListener);
        } catch (Exception e) {
            Log.e("getWeather", e.toString());
        }
    }

    // retrofit call
    private void getWeatherDetails(String lat, String lang) {
        try {
            // display a progress dialog
            final ProgressDialog progressDialog = new ProgressDialog(MainActivity.this);
            progressDialog.setCancelable(false); // set cancelable to false
            progressDialog.setMessage("Please Wait"); // set message
            progressDialog.show(); // show progress dialog

            Call<WeatherResponse> call = apiInterface.getWeatherBasedOnGps(lat, lang, BuildConfig.WEATHER_API_KEY);
            call.enqueue(new Callback<WeatherResponse>() {
                @Override
                public void onResponse(Call<WeatherResponse> call, final Response<WeatherResponse> response) {
                    progressDialog.dismiss();
                    try {
                        if (response.body() != null) {
                            binding.cityNameTv.setText(response.body().getCityName());
                            int weatherCondition = response.body().getWeather()[0].getId();
                            String icon = response.body().getWeather()[0].getIcon();
                            icon = WeatherResponse.updateWeatherIcon(weatherCondition);
                            int resourseID = getResources().getIdentifier(icon,
                                    "drawable",getPackageName());
                            binding.weatherIcon.setImageResource(resourseID);
                            //convert temp from kelvin to degree
                            int temp = (int) Math.round(response.body().getMain().getTemp() - 273.15);
                            binding.temperatureTv.setText(temp +"°C");
                            binding.weatherConditionTv.setText(response.body().getWeather()[0].getMain());
                        }
                    } catch (Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(MainActivity.this, e.toString(), Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onFailure(Call<WeatherResponse> call, Throwable t) {
                    progressDialog.dismiss();
                    Log.e("RETROFIT_ERROR", t.toString());
                }
            });
        } catch (Exception e) {
            Log.e("RETROFIT_ERROR", e.toString());
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getWeatherForCurrentLocation();
            } else {
                // user denied permission
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(locationManager!=null){
            locationManager.removeUpdates(locationListener);
        }
    }
}