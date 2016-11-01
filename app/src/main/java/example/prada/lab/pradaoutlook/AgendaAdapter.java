package example.prada.lab.pradaoutlook;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import org.zakariya.stickyheaders.SectioningAdapter;

import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import example.prada.lab.pradaoutlook.store.EventStoreFactory;
import example.prada.lab.pradaoutlook.store.IEventStore;
import example.prada.lab.pradaoutlook.model.POEvent;
import example.prada.lab.pradaoutlook.utils.Utility;
import example.prada.lab.pradaoutlook.view.DayViewHolder;
import example.prada.lab.pradaoutlook.view.EventViewHolder;
import example.prada.lab.pradaoutlook.view.NoEventViewHolder;

/**
 * Created by prada on 10/27/16.
 */
public class AgendaAdapter extends SectioningAdapter {
    private static final int ITEM_TYPE_EVENT = 1;
    private static final int ITEM_TYPE_NO_EVENT = 2;

    private final LayoutInflater mInflater;
    private final Calendar mFrom;
    private final Calendar mTo;
    private final IEventStore mStore;

    public AgendaAdapter(@NonNull Context ctx, @NonNull Calendar from, @NonNull Calendar to) {
        mInflater = LayoutInflater.from(ctx);
        if (to.before(from)) {
            new IllegalArgumentException(
                "the start of date is wrong, it should be before then this time " + to.toString());
        }
        mTo = to;
        mFrom = from;
        mStore = EventStoreFactory.getInstance(ctx);
    }

    @Override
    public void onBindItemViewHolder(ItemViewHolder viewHolder, int sectionIndex, int itemIndex,
                                     int itemUserType) {
        switch (itemUserType) {
            case ITEM_TYPE_EVENT:
                List<POEvent> events = queryEvents(sectionIndex);
                EventViewHolder vh = (EventViewHolder) viewHolder;
                vh.bind(events.get(itemIndex));
                break;
            case ITEM_TYPE_NO_EVENT:
                break; // DO NOTHING
            default:
                super.onBindItemViewHolder(viewHolder, sectionIndex, itemIndex, itemUserType);
                break;
        }
    }

    @Override
    public ItemViewHolder onCreateItemViewHolder(ViewGroup parent, int itemUserType) {
        switch (itemUserType) {
            case ITEM_TYPE_NO_EVENT:
                return new NoEventViewHolder(
                    mInflater.inflate(R.layout.item_no_event, parent, false));
            case ITEM_TYPE_EVENT:
            default:
                return new EventViewHolder(mInflater.inflate(R.layout.item_event, parent, false));
        }
    }

    @Override
    public HeaderViewHolder onCreateHeaderViewHolder(ViewGroup parent, int headerUserType) {
        return new DayViewHolder(mInflater.inflate(R.layout.item_day_header, parent, false));
    }

    @Override
    public void onBindHeaderViewHolder(HeaderViewHolder viewHolder, int sectionIndex,
                                       int headerUserType) {
        DayViewHolder vh = (DayViewHolder) viewHolder;
        Calendar c = (Calendar) mFrom.clone();
        c.add(Calendar.HOUR, 24 * sectionIndex);
        vh.bind(c);
    }

    @Override
    public int getNumberOfSections() {
        return Utility.getDaysBetween(mFrom, mTo);
    }

    @Override
    public int getSectionItemUserType(int sectionIndex, int itemIndex) {
        if (hasEvent(sectionIndex)) {
            return ITEM_TYPE_EVENT;
        } else {
            return ITEM_TYPE_NO_EVENT;
        }
    }

    // TODO reuse the array with long values
    private long[] convertSectionIdxToTimestampRange(int sectionIdx) {
        Calendar c = (Calendar) mFrom.clone();
        c.add(Calendar.HOUR, 24 * sectionIdx);
        long t1 = c.getTimeInMillis();
        c.add(Calendar.HOUR, 24);
        return new long[]{t1, c.getTimeInMillis()};
    }

    private boolean hasEvent(int sectionIndex) {
        long[] range = convertSectionIdxToTimestampRange(sectionIndex);
        return mStore.hasEvents(range[0], range[1]);
    }

    private List<POEvent> queryEvents(int sectionIndex) {
        long[] range = convertSectionIdxToTimestampRange(sectionIndex);
        return mStore.queryEvents(range[0], range[1]);
    }

    @Override
    public boolean doesSectionHaveHeader(int sectionIndex) {
        return true;
    }

    @Override
    public int getNumberOfItemsInSection(int sectionIndex) {
        long[] range = convertSectionIdxToTimestampRange(sectionIndex);
        int numEvents = mStore.countEvents(range[0], range[1]);
        return numEvents == 0 ? 1 : numEvents;
    }

    public void updateEvent(POEvent event) {
        // TODO update the range of the time frame
        notifySectionDataSetChanged(findSectionIndex(event.getFrom()));
    }

    public static final int SECONDS_IN_A_DAY = 3600 * 24;

    private int findSectionIndex(@NonNull Date date) {
        long seconds = (date.getTime() - mFrom.getTimeInMillis()) / 1000;
        return (int) Math.floor(seconds / SECONDS_IN_A_DAY);
    }

    public int findSectionPosition(@NonNull Date date) {
        return getAdapterPositionForSectionHeader(findSectionIndex(date));
    }
}