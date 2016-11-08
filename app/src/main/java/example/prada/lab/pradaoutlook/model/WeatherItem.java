package example.prada.lab.pradaoutlook.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by prada on 11/6/16.
 *
 * the data object represent the weather information for each days
 * document : https://darksky.net/dev/docs/forecast
 */
public class WeatherItem {

    @SerializedName("time")
    public long time;//: 1478505600,
    @SerializedName("summary")
    public String summary;//: "Mostly cloudy throughout the day.",
    @SerializedName("icon")
    public String icon;//: "partly-cloudy-day",
    @SerializedName("sunriseTime")
    public long sunriseTime;//: 1478529805,
    @SerializedName("sunsetTime")
    public long sunsetTime;//: 1478567163,
    @SerializedName("temperatureMin")
    public float temperatureMin;//: 53.72,
    @SerializedName("temperatureMinTime")
    public long temperatureMinTime;//: 1478527200,
    @SerializedName("temperatureMax")
    public float temperatureMax;//: 69.75,
    @SerializedName("temperatureMaxTime")
    public long temperatureMaxTime;//: 1478559600,
    @SerializedName("dewPoint")
    public float dewPoint;//: 54.66,
}
