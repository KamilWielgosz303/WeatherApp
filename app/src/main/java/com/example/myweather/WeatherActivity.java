package com.example.myweather;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import java.sql.Time;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.util.Calendar;
import java.util.List;

public class WeatherActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);
            WeatherDto weather = (WeatherDto) getIntent().getSerializableExtra("CITY");
            TextView city = findViewById(R.id.City);
            TextView time = findViewById(R.id.Time);
            TextView temp = findViewById(R.id.Temperature);
            TextView hum = findViewById(R.id.Humidity);
            TextView temp_min = findViewById(R.id.TempMin);
            TextView temp_max = findViewById(R.id.TempMax);
            TextView press = findViewById(R.id.Pressure);
            TextView wind = findViewById(R.id.Wind);

            city.setText(weather.getName());
            wind.setText(weather.getWind().getSpeed() + " km/h");
            temp.setText(weather.getMain().getTemp() + " °C");
            hum.setText(weather.getMain().getHumidity() + " %");
            temp_max.setText(weather.getMain().getTemp_max() + " °C");
            temp_min.setText(weather.getMain().getTemp_min() + " °C");
            press.setText(weather.getMain().getPressure() + " hPa");
        SimpleDateFormat sdfDate = new SimpleDateFormat("HH:mm:ss");//dd/MM/yyyy
            time.setText(sdfDate.format(Calendar.getInstance().getTime()));
        SharedPreferences sharedPreferences = getSharedPreferences("shared pref",MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("CITY",weather.getName());
        editor.apply();
    }
}
