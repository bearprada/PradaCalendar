package example.prada.lab.pradaoutlook;

import android.content.Context;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.v4.util.LruCache;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import org.zakariya.stickyheaders.SectioningAdapter;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
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
    private static final int MILL_SECONDS_IN_A_DAY = 24 * 60 * 60 * 1000;

    private static final int ITEM_TYPE_EVENT = 1;
    private static final int ITEM_TYPE_NO_EVENT = 2;

    private final LayoutInflater mInflater;
    private final Calendar mFrom = Calendar.getInstance();
    private final Calendar mTo = Calendar.getInstance();
    private final IEventStore mStore;
    private Cursor mCursor = null;

    private LruCache<Integer, POEvent> mEventCache = new LruCache<>(256);

    private final List<Integer> mNumOfItemOnSectionList = Collections.synchronizedList(new ArrayList<Integer>());

    public AgendaAdapter(@NonNull Context ctx) {
        mInflater = LayoutInflater.from(ctx);
        mStore = EventStoreFactory.getInstance(ctx);
        mCursor = mStore.getEvents();
        rebuildSectionsMetadata();
    }

    @Override
    public void onBindItemViewHolder(ItemViewHolder viewHolder, int sectionIndex, int itemIndex,
                                     int itemUserType) {
        switch (itemUserType) {
            case ITEM_TYPE_EVENT:
                ((EventViewHolder) viewHolder).bind(queryEvent(sectionIndex, itemIndex));
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
        return mNumOfItemOnSectionList.size();
    }

    @Override
    public int getSectionItemUserType(int sectionIndex, int itemIndex) {
        if (hasEvent(sectionIndex)) {
            return ITEM_TYPE_EVENT;
        } else {
            return ITEM_TYPE_NO_EVENT;
        }
    }

    private void rebuildSectionsMetadata() {
        mEventCache.evictAll();
        mNumOfItemOnSectionList.clear();

        if (mCursor.getCount() <= 0) {
            return;
        }
        if (!mCursor.moveToFirst()) {
            return;
        }
        POEvent firstEvent = POEvent.createFromCursor(mCursor);
        mCursor.moveToLast();
        POEvent latestEvent = POEvent.createFromCursor(mCursor);
        mFrom.setTime(firstEvent.getFrom());
        mTo.setTime(latestEvent.getTo());
        normalizeDate(mFrom);
        normalizeDate(mTo);

        long days = Utility.getDaysBetween(mFrom, mTo) + 1; // FIXME
        // Initial list
        for (int i = 0; i <= days; i++) {
            mNumOfItemOnSectionList.add(0);
        }
        mCursor.moveToFirst();
        while (mCursor.moveToNext()) {
            POEvent e = POEvent.createFromCursor(mCursor);
            int sectionIdx = findSectionIndex(e.getFrom());
            int count = mNumOfItemOnSectionList.get(sectionIdx);
            mNumOfItemOnSectionList.set(sectionIdx, count + 1);
        }
    }

    private void normalizeDate(Calendar cal) {
        long mill = (long) Math.floor(cal.getTimeInMillis() / MILL_SECONDS_IN_A_DAY);
        cal.setTimeInMillis(mill * MILL_SECONDS_IN_A_DAY);
    }

    private boolean hasEvent(int sectionIndex) {
        return mNumOfItemOnSectionList.get(sectionIndex) > 0;
    }

    private POEvent queryEvent(int sectionIndex, int itemIndex) {
        int cursorIndex = getCursorIndex(sectionIndex, itemIndex);
        if (mEventCache.get(cursorIndex) != null) {
            return mEventCache.get(cursorIndex);
        }
        mCursor.moveToPosition(cursorIndex);
        POEvent event = POEvent.createFromCursor(mCursor);
        mEventCache.put(cursorIndex, event);
        return event;
    }

    private int getCursorIndex(int sectionIndex, int itemIndex) {
        int cursorIndex = itemIndex;
        for (int i = 0; i < sectionIndex ; i++) {
            cursorIndex += mNumOfItemOnSectionList.get(i);
        }
        return cursorIndex;
    }

    @Override
    public boolean doesSectionHaveHeader(int sectionIndex) {
        return true;
    }

    @Override
    public int getNumberOfItemsInSection(int sectionIndex) {
        int numEvents = mNumOfItemOnSectionList.get(sectionIndex);
        return numEvents == 0 ? 1 : numEvents;
    }

    void updateEvents() {
        if (mCursor != null) {
            mCursor.close();
        }
        mCursor = mStore.getEvents();
        rebuildSectionsMetadata();
        notifyAllSectionsDataSetChanged();
    }

    private int findSectionIndex(@NonNull Date date) {
        long millSeconds = date.getTime() - mFrom.getTimeInMillis();
        return (int) Math.floor(millSeconds / MILL_SECONDS_IN_A_DAY);
    }

    int findSectionPosition(@NonNull Date date) {
        return getAdapterPositionForSectionHeader(findSectionIndex(date));
    }
}