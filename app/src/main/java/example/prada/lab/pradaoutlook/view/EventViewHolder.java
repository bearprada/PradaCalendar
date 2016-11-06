package example.prada.lab.pradaoutlook.view;

import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import org.zakariya.stickyheaders.SectioningAdapter;

import java.text.SimpleDateFormat;

import example.prada.lab.pradaoutlook.R;
import example.prada.lab.pradaoutlook.model.POEvent;
import example.prada.lab.pradaoutlook.utils.Utility;

/**
 * Created by prada on 10/29/16.
 */

public class EventViewHolder extends SectioningAdapter.ItemViewHolder {
    private static final SimpleDateFormat sDateFormat = new SimpleDateFormat("h:m a");

    private final TextView mTxtDuration;
    private final TextView mTxtTitle;
    private final TextView mTxtLocation;
    private final TextView mTxtTime;

    public EventViewHolder(View itemView) {
        super(itemView);
        mTxtDuration = (TextView) itemView.findViewById(R.id.txt_duration);
        mTxtTitle = (TextView) itemView.findViewById(R.id.txt_event_title);
        mTxtLocation = (TextView) itemView.findViewById(R.id.txt_location);
        mTxtTime = (TextView) itemView.findViewById(R.id.txt_time);
    }

    public void bind(POEvent event) {
        mTxtTitle.setText(event.getTitle());
        mTxtTime.setText(sDateFormat.format(event.getTo()));
        long ms = event.getFrom().getTime() - event.getTo().getTime();
        String durationStr = Utility.getDurationString(ms);
        if (!TextUtils.isEmpty(durationStr)) {
            mTxtDuration.setText(durationStr);
        }
    }
}
