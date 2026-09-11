package example.prada.lab.pradaoutlook.model

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonParseException
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type

open class WeatherResponse private constructor() {
    private var weathers: List<WeatherItem> = ArrayList()
    fun getWeathers(): List<WeatherItem> = weathers

    class WeatherResponseReader : JsonDeserializer<WeatherResponse> {
        @Throws(JsonParseException::class)
        override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): WeatherResponse {
            val data = json.asJsonObject.getAsJsonObject("daily").getAsJsonArray("data")
            return WeatherResponse().apply {
                weathers = context.deserialize(data, object : TypeToken<ArrayList<WeatherItem>>() {}.type)
            }
        }
    }
}
