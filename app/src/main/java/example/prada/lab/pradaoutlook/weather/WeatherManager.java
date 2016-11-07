package example.prada.lab.pradaoutlook.weather;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.DrawableRes;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.RequestFuture;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;

import bolts.Continuation;
import bolts.Task;
import example.prada.lab.pradaoutlook.R;
import example.prada.lab.pradaoutlook.model.WeatherItem;
import example.prada.lab.pradaoutlook.model.WeatherResponse;
import example.prada.lab.pradaoutlook.utils.Utility;

/**
 * Created by prada on 11/6/16.
 */

public class WeatherManager {
    private static final String TOKEN = "b1b5215fe9d0c1bd29ce15b6c8088be0";
    private static final String URL = "https://api.darksky.net/forecast/%s/%f,%f?exclude=currently,hourly,minutely,alerts,flags";

    private static WeatherManager sInstance;
    private final RequestQueue mQueue;
    private Task<WeatherResponse> mQueryWeatherTask;

    public static WeatherManager getInstance(Context ctx) {
        if (sInstance == null) {
            sInstance = new WeatherManager(ctx);
        }
        return sInstance;
    }

    private WeatherManager(Context ctx) {
        mQueue = Volley.newRequestQueue(ctx);
    }

    public static @DrawableRes int getIcon(String iconStr) {
        switch (iconStr == null ? "" : iconStr) {
            case "rain":
                return R.drawable.rain;
            case "snow":
                return R.drawable.snow;
            case "sleet":
                return R.drawable.sleet;
            case "fog":
                return R.drawable.fog;
            case "wind":
            case "cloudy":
                return R.drawable.cloudy;
            case "partly-cloudy-day":
            case "partly-cloudy-night":
                return R.drawable.partlycloudy;
            case "clear-day":
            case "clear-night":
            default:
                return R.drawable.clear;
        }
    }

    public Task<WeatherResponse> handleLocation(final double lat, final double lng) {
        mQueryWeatherTask = Task.callInBackground(new Callable<WeatherResponse>() {
            @Override
            public WeatherResponse call() throws Exception {
                return queryWeather(mQueue, lat, lng);
            }
        });
        return mQueryWeatherTask;
    }

    private Exception mNoWeatherResultException = new Exception("no any weather result yet");
    private Exception mWeatherNotFoundException = new Exception("no get any weather");
    public Task<WeatherItem> fetchWeather(final long timeInMillis) {
        if (mQueryWeatherTask == null) {
            return Task.forError(mNoWeatherResultException);
        }
        return mQueryWeatherTask.onSuccess(new Continuation<WeatherResponse, WeatherItem>() {
            @Override
            public WeatherItem then(Task<WeatherResponse> task) throws Exception {
                List<WeatherItem> weathers = task.getResult().getWeathers();
                for (WeatherItem w : weathers) {
                    if (Math.abs(timeInMillis - (w.time * 1000)) <= Utility.MILL_SECONDS_A_DAY) {
                        return w;
                    }
                }
                throw mWeatherNotFoundException;
            }
        });
    }

    @SuppressLint("DefaultLocale")
    private WeatherResponse queryWeather(RequestQueue queue, double latitude, double longitude)
        throws ExecutionException, InterruptedException {
        String uri = String.format(URL, TOKEN, latitude, longitude);
        RequestFuture<String> future = RequestFuture.newFuture();
        queue.add(new StringRequest(uri, future, future));
        return new GsonBuilder()
            .registerTypeAdapter(WeatherResponse.class, new WeatherResponse.WeatherResponseReader())
            .create()
            .fromJson(future.get(), WeatherResponse.class);
    }
}
