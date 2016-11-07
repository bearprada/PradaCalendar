package example.prada.lab.pradaoutlook.view;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import org.zakariya.stickyheaders.SectioningAdapter;

import java.util.Calendar;

import bolts.Continuation;
import bolts.Task;
import example.prada.lab.pradaoutlook.R;
import example.prada.lab.pradaoutlook.model.WeatherItem;
import example.prada.lab.pradaoutlook.utils.Utility;
import example.prada.lab.pradaoutlook.weather.WeatherManager;

/**
 * Created by prada on 10/29/16.
 */

public class DayViewHolder extends SectioningAdapter.HeaderViewHolder{
    private final TextView mTxtDate;
    private final ImageView mWeatherIcon;
    private final TextView mTxtTemperature;

    public DayViewHolder(View itemView) {
        super(itemView);
        mTxtDate = (TextView) itemView.findViewById(R.id.text_date);
        mWeatherIcon = (ImageView) itemView.findViewById(R.id.image_weather_icon);
        mTxtTemperature = (TextView) itemView.findViewById(R.id.text_temperature);
    }

    public void bind(Calendar cal, Task<WeatherItem> task) {
        mTxtTemperature.setVisibility(View.INVISIBLE);
        mWeatherIcon.setVisibility(View.INVISIBLE);
        mTxtDate.setText(String.format("%s, %s %2d",
                                       Utility.convertDayStr(cal.get(Calendar.DAY_OF_WEEK)),
                                       Utility.convertMonthStr(cal.get(Calendar.MONTH)),
                                       cal.get(Calendar.DAY_OF_MONTH)));
        task.onSuccess(new Continuation<WeatherItem, Void>() {
            @Override
            public Void then(Task<WeatherItem> task) throws Exception {
                WeatherItem weather = task.getResult();
                mTxtTemperature.setVisibility(View.VISIBLE);
                mWeatherIcon.setVisibility(View.VISIBLE);
                String tempStr = itemView.getContext().getString(R.string.temperature_text, (int) weather.temperatureMin);
                mTxtTemperature.setText(tempStr);
                mWeatherIcon.setImageResource(WeatherManager.getIcon(weather.icon));
                return null;
            }
        }, Task.UI_THREAD_EXECUTOR);
    }
}
