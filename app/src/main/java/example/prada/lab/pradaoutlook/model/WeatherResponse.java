package example.prada.lab.pradaoutlook.model;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by prada on 11/6/16.
 */

public class WeatherResponse {
    private List<WeatherItem> weathers = new ArrayList<>();

    public List<WeatherItem> getWeathers() {
        return weathers;
    }

    private WeatherResponse() {
    }

    public static class WeatherResponseReader implements JsonDeserializer<WeatherResponse> {

        private static final Type sToken = new TypeToken<ArrayList<WeatherItem>>(){}.getType();

        @Override
        public WeatherResponse deserialize(JsonElement json, Type typeOfT,
                                           JsonDeserializationContext context)
            throws JsonParseException {
            JsonArray arr = json.getAsJsonObject().get("daily")
                                .getAsJsonObject().get("data")
                                .getAsJsonArray();
            WeatherResponse response = new WeatherResponse();
            response.weathers = context.deserialize(arr, sToken);
            return response;
        }
    }
}
