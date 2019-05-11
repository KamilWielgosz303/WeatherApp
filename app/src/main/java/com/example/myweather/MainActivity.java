package com.example.myweather;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    protected final String INPUT_ERROR = "Wpisz lokalizację!";
    TextView inputCity;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
        inputCity = findViewById(R.id.inputCity);
        SharedPreferences sharedPreferences = getSharedPreferences("shared pref",MODE_PRIVATE);
        String defaultCity = sharedPreferences.getString("CITY", "");
        inputCity.setText(defaultCity);
    }

    public void checkWeather(View view) {
        String inputedCity = inputCity.getText().toString();
        Retrofit retrofit;
        WeatherInterface weather;
        final Intent intent = new Intent(this, WeatherActivity.class);
        retrofit = new Retrofit.Builder()
                .baseUrl("http://api.openweathermap.org")
                .addConverterFactory(GsonConverterFactory.create()).build();
        if (inputedCity.equals("")) {
            showAlert(INPUT_ERROR);
        }else {
            weather = retrofit.create(WeatherInterface.class);
            Log.d("MyApp",inputedCity);
            Call<WeatherDto> call = weather.getWeather(inputedCity+",pl","1ef0955cc00798dc84e15eb430a9d021","metric");
           call.enqueue(new Callback<WeatherDto>() {
                @Override
                public void onResponse(@NonNull Call<WeatherDto> call, @NonNull Response<WeatherDto> response) {

                   if(!response.isSuccessful()) {
                       showAlert("Błąd: " + response.code());
                       return;
                   }

                       WeatherDto weather = response.body();
                    assert weather != null;
                           Log.d("tag",weather.getName());

                       intent.putExtra("CITY", (Serializable) weather);
                      startActivity(intent);
                   }

                @Override
                public void onFailure(Call<WeatherDto> call, Throwable t) {
                    showAlert("Błąd: " + t.toString());
                }
           });
        }
    }

    public void showAlert(String txt) {
        Context context = getApplicationContext();
        Toast errtoast = Toast.makeText(context, "Blad", Toast.LENGTH_LONG);
        errtoast.setGravity(Gravity.TOP, 0, 0);
                errtoast.setText(txt);
                errtoast.show();
        }
    }