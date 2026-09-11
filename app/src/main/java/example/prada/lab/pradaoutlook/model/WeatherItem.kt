package example.prada.lab.pradaoutlook.model

import com.google.gson.annotations.SerializedName

open class WeatherItem {
    @JvmField @SerializedName("time") var time: Long = 0
    @JvmField @SerializedName("summary") var summary: String? = null
    @JvmField @SerializedName("icon") var icon: String? = null
    @JvmField @SerializedName("sunriseTime") var sunriseTime: Long = 0
    @JvmField @SerializedName("sunsetTime") var sunsetTime: Long = 0
    @JvmField @SerializedName("temperatureMin") var temperatureMin: Float = 0f
    @JvmField @SerializedName("temperatureMinTime") var temperatureMinTime: Long = 0
    @JvmField @SerializedName("temperatureMax") var temperatureMax: Float = 0f
    @JvmField @SerializedName("temperatureMaxTime") var temperatureMaxTime: Long = 0
    @JvmField @SerializedName("dewPoint") var dewPoint: Float = 0f
}
