package example.prada.lab.pradaoutlook.view;

import android.view.View;
import android.widget.TextView;

import org.zakariya.stickyheaders.SectioningAdapter;

import java.util.Calendar;

import example.prada.lab.pradaoutlook.R;
import example.prada.lab.pradaoutlook.utils.Utility;

/**
 * Created by prada on 10/29/16.
 */

public class DayViewHolder extends SectioningAdapter.HeaderViewHolder{
    private final TextView mTxtDate;

    public DayViewHolder(View itemView) {
        super(itemView);
        mTxtDate = (TextView) itemView.findViewById(R.id.text_date);
    }

    public void bind(Calendar cal) {
        mTxtDate.setText(String.format("%s, %s %2d",
                                       Utility.convertDayStr(cal.get(Calendar.DAY_OF_WEEK)),
                                       Utility.convertMonthStr(cal.get(Calendar.MONTH)),
                                       cal.get(Calendar.DAY_OF_MONTH)));
    }
}
