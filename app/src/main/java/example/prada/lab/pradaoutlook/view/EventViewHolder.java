package example.prada.lab.pradaoutlook.view;

import android.view.View;
import android.widget.TextView;

import org.zakariya.stickyheaders.SectioningAdapter;

import example.prada.lab.pradaoutlook.R;
import example.prada.lab.pradaoutlook.model.POEvent;
import example.prada.lab.pradaoutlook.utils.Utility;

/**
 * Created by prada on 10/29/16.
 */

public class EventViewHolder extends SectioningAdapter.ItemViewHolder {
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
        mTxtTime.setText("8:00 PM"); // FIXME
        mTxtDuration.setText(Utility.timestampTo(event.getDuration()));
    }
}
