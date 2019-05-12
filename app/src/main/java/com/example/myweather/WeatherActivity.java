package com.example.myweather;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

import retrofit2.*;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.example.myweather.MainActivity.showAlert;

public class WeatherActivity extends AppCompatActivity {
    private String cityName;
    protected Context context;
    protected final AppCompatActivity REFERENCE = this;
    private final String ICON_URL = "http://openweathermap.org/img/w/";
    private TextView city;
    private TextView time;
    private TextView temp;
    private TextView hum;
    private TextView temp_min;
    private TextView temp_max;
    private TextView press;
    private TextView wind;
    private ImageView weatherImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this.getApplicationContext();
        setContentView(R.layout.activity_weather);
            cityName = getIntent().getStringExtra("CITY");
            city = findViewById(R.id.City);
            time = findViewById(R.id.Time);
            temp = findViewById(R.id.Temperature);
            hum = findViewById(R.id.Humidity);
            temp_min = findViewById(R.id.TempMin);
            temp_max = findViewById(R.id.TempMax);
            press = findViewById(R.id.Pressure);
            wind = findViewById(R.id.Wind);
            weatherImage = findViewById(R.id.weatherImage);

        SharedPreferences sharedPreferences = getSharedPreferences("shared pref",MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("CITY",cityName);
        editor.apply();
        Timer timer = new Timer();
        TimerTask task = new TimerTask(){
            public void run(){
                if(isNetworkAvailable(REFERENCE)){
                    checkWeather();
                }

            }
        };
        timer.schedule(task, 0, 35000);

        final SwipeRefreshLayout pullToRefresh = findViewById(R.id.pullToRefresh);
        pullToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if(isNetworkAvailable(REFERENCE)){
                    checkWeather();

                }
                pullToRefresh.setRefreshing(false);

            }
        });
    }

    public void checkWeather() {
        Retrofit retrofit;
        WeatherInterface weather;
        retrofit = new Retrofit.Builder()
                .baseUrl("http://api.openweathermap.org")
                .addConverterFactory(GsonConverterFactory.create()).build();
            weather = retrofit.create(WeatherInterface.class);
            Log.d("MyApp",cityName);
            Call<WeatherDto> call = weather.getWeather(cityName+",pl","1ef0955cc00798dc84e15eb430a9d021","metric");
            call.enqueue(new Callback<WeatherDto>() {
                @Override
                public void onResponse(@NonNull Call<WeatherDto> call, @NonNull Response<WeatherDto> response) {

                    if(!response.isSuccessful()) {
                        showAlert("Błąd: " + response.code(),context);
                        final Intent intent = new Intent(REFERENCE, MainActivity.class);
                        startActivity(intent);
                        return;
                    }else {

                        final WeatherDto weather = response.body();
                        assert weather != null;
                        Log.d("tag", weather.getName());
                        city.setText(weather.getName());
                        wind.setText(weather.getWind().getSpeed() + " km/h");
                        temp.setText(weather.getMain().getTemp() + " °C");
                        hum.setText(weather.getMain().getHumidity() + " %");
                        temp_max.setText(weather.getMain().getTemp_max() + " °C");
                        temp_min.setText(weather.getMain().getTemp_min() + " °C");
                        press.setText(weather.getMain().getPressure() + " hPa");
                        SimpleDateFormat sdfDate = new SimpleDateFormat("HH:mm");
                        time.setText(sdfDate.format(Calendar.getInstance().getTime()));

                        Thread thread = new Thread(new Runnable() {

                            @Override
                            public void run() {
                                try {
                                    URL url;
                                    try {
                                        Weather[] temp = weather.getWeather();
                                        String icon_url = ICON_URL + temp[0].getIcon() + ".png";
                                        Log.d("URL", icon_url);
                                        url = new URL(icon_url);
                                        Bitmap bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                                        weatherImage.setImageBitmap(bmp);
                                    } catch (MalformedURLException e) {
                                        e.printStackTrace();
                                        Log.d("URL", "Wyjtatek");
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                        Log.d("URL", "Wyjatek");
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        });

                        thread.start();
                    }
                }

                @Override
                public void onFailure(Call<WeatherDto> call, Throwable t) {
                    showAlert("Błąd: " + t.toString(),context);
                }
            });
        }

    public static boolean isNetworkAvailable(AppCompatActivity apt) {
        ConnectivityManager connectivityManager = (ConnectivityManager) apt.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}

