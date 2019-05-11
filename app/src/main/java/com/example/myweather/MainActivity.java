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
import android.widget.Button;
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

import static com.example.myweather.WeatherActivity.isNetworkAvailable;

public class MainActivity extends AppCompatActivity {

    protected final String INPUT_ERROR = "Wpisz lokalizację!";
    protected Context act;
    TextView inputCity;
    Button checkWeatherButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
        inputCity = findViewById(R.id.inputCity);
        checkWeatherButton = findViewById(R.id.checkWeatherButton);
        SharedPreferences sharedPreferences = getSharedPreferences("shared pref",MODE_PRIVATE);
        String defaultCity = sharedPreferences.getString("CITY", "");
        inputCity.setText(defaultCity);
        act = this.getApplicationContext();
        if(!isNetworkAvailable(this)){
            checkWeatherButton.setEnabled(false);
            showAlert("Brak połączenia z internetem!",this);
        }else{
            checkWeatherButton.setEnabled(true);
        }
    }

    public void checkWeather(View view) {
        String inputedCity = inputCity.getText().toString();
        final Intent intent = new Intent(this, WeatherActivity.class);
        if (inputedCity.equals("")) {
            showAlert(INPUT_ERROR,act);
        }else {
                       intent.putExtra("CITY", inputedCity);
                       startActivity(intent);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(isNetworkAvailable(this)) {
            checkWeatherButton.setEnabled(true);
        }
        else{
            checkWeatherButton.setEnabled(false);
            showAlert("Brak połączenia z internetem!",this);
        }

    }

    public static void showAlert(String txt,Context context) {
        Toast errtoast = Toast.makeText(context, "Blad", Toast.LENGTH_LONG);
        errtoast.setGravity(Gravity.TOP, 0, 0);
                errtoast.setText(txt);
                errtoast.show();
        }
    }