package example.prada.lab.pradaoutlook.weather;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.DrawableRes;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.RequestFuture;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
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
 *
 * this manager class can query and keep the forecasting weather information through the internet.
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

    /**
     * convert the icon resource by the string
     *
     * @param iconStr the string should be "rain", "snow", "wind"...etc
     * @return the icon that showed the weather, the default icon is "clear"
     */
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

    /**
     * query the weather information with the specific latitude and longitude.
     *
     * @param lat the specific location's latitude
     * @param lng the specific location's longitude
     * @return the async task that will response the weather objects from server
     */
    public Task<WeatherResponse> queryWeather(final double lat, final double lng) {
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

    /**
     * fetching the weather that's more close to a day
     *
     * @param timeInMillis the timestamp for query the weather data
     * @return an async task that response a weather that's more close to the timestamp.
     *          but it might return the error state if
     *          1) the loading process is failed or pending.
     *          2) it can't found any weather information that close to your input timestamp.
     */
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

    /**
     * Getting the weather information from the darksky.net.
     *
     * please check more detail in here https://darksky.net/dev/docs/forecast
     *
     * @param queue the networking queue for the volley requests
     * @param latitude the user's current latitude
     * @param longitude the user's current longitude
     * @return the weather result from the api call
     * @throws ExecutionException if the networking task is interrupted
     * @throws InterruptedException if the networking task is interrupted
     */
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
