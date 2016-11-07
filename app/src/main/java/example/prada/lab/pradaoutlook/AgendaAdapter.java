package example.prada.lab.pradaoutlook;

import android.content.Context;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.v4.util.LruCache;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import org.zakariya.stickyheaders.SectioningAdapter;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

import example.prada.lab.pradaoutlook.store.EventStoreFactory;
import example.prada.lab.pradaoutlook.store.IEventStore;
import example.prada.lab.pradaoutlook.model.POEvent;
import example.prada.lab.pradaoutlook.utils.Utility;
import example.prada.lab.pradaoutlook.view.DayViewHolder;
import example.prada.lab.pradaoutlook.view.EventViewHolder;
import example.prada.lab.pradaoutlook.view.NoEventViewHolder;
import example.prada.lab.pradaoutlook.weather.WeatherManager;

/**
 * Created by prada on 10/27/16.
 */
public class AgendaAdapter extends SectioningAdapter {
    private static final int MILL_SECONDS_IN_A_DAY = 24 * 60 * 60 * 1000;

    public static final int ITEM_TYPE_EVENT = 1;
    public static final int ITEM_TYPE_NO_EVENT = 2;

    private final LayoutInflater mInflater;
    private final Calendar mFrom = Calendar.getInstance();
    private final Calendar mTo = Calendar.getInstance();
    private final IEventStore mStore;
    private final WeatherManager mWeatherMgr;
    private Cursor mCursor = null;

    private LruCache<Integer, POEvent> mEventCache = new LruCache<>(256);

    private final List<Map.Entry<Calendar, Integer>> mNumOfItemOnSectionList =
        Collections.synchronizedList(new ArrayList<Map.Entry<Calendar, Integer>>());

    public AgendaAdapter(@NonNull Context ctx) {
        mInflater = LayoutInflater.from(ctx);
        mStore = EventStoreFactory.getInstance(ctx);
        mCursor = mStore.getEvents();
        mWeatherMgr = WeatherManager.getInstance(ctx);
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
        Calendar cal = mNumOfItemOnSectionList.get(sectionIndex).getKey();
        vh.bind(cal, mWeatherMgr.fetchWeather(cal.getTimeInMillis()));
    }

    @Override
    public int getNumberOfSections() {
        return mNumOfItemOnSectionList.size();
    }

    @Override
    public int getSectionItemUserType(int sectionIndex, int itemIndex) {
        if (mNumOfItemOnSectionList.get(sectionIndex).getValue() > 0) {
            return ITEM_TYPE_EVENT;
        } else {
            return ITEM_TYPE_NO_EVENT;
        }
    }

    @Override
    public boolean doesSectionHaveHeader(int sectionIndex) {
        return true;
    }

    @Override
    public int getNumberOfItemsInSection(int sectionIndex) {
        if (sectionIndex >= mNumOfItemOnSectionList.size()) {
            return 0;
        }
        int numEvents = mNumOfItemOnSectionList.get(sectionIndex).getValue();
        return numEvents == 0 ? 1 : numEvents;
    }

    public void updateEvents() {
        if (mCursor != null) {
            mCursor.close();
        }
        mCursor = mStore.getEvents();
        rebuildSectionsMetadata();
        notifyAllSectionsDataSetChanged();
    }

    public int findSectionPosition(@NonNull Date date) {
        return getAdapterPositionForSectionHeader(findSectionIndex(date));
    }

    public int findSectionIndex(@NonNull Date date) {
        long millSeconds = date.getTime() - mFrom.getTimeInMillis();
        return (int) Math.floor(millSeconds / MILL_SECONDS_IN_A_DAY);
    }

    private void rebuildSectionsMetadata() {
        mEventCache.evictAll();
        mNumOfItemOnSectionList.clear();

        if (mCursor.getCount() <= 0) {
            Date date = new Date();
            mFrom.setTime(date);
            mTo.setTime(date);
            return;
        }
        mCursor.moveToFirst();
        POEvent firstEvent = POEvent.createFromCursor(mCursor);
        mCursor.moveToLast();
        POEvent latestEvent = POEvent.createFromCursor(mCursor);
        mFrom.setTime(firstEvent.getFrom());
        mTo.setTime(latestEvent.getTo());
        mTo.set(Calendar.DAY_OF_YEAR, mTo.get(Calendar.DAY_OF_YEAR) + 1);
        normalizeDate(mFrom);
        normalizeDate(mTo);

        long days = Utility.getDaysBetween(mFrom, mTo);
        // Initial list
        for (int i = 0; i < days; i++) {
            Calendar c = (Calendar) mFrom.clone();
            c.add(Calendar.HOUR, 24 * i);
            mNumOfItemOnSectionList.add(new AbstractMap.SimpleEntry<>(c, 0));
        }
        mCursor.moveToFirst();
        do {
            POEvent e = POEvent.createFromCursor(mCursor);
            int sectionIdx = findSectionIndex(e.getFrom());
            int count = mNumOfItemOnSectionList.get(sectionIdx).getValue();
            mNumOfItemOnSectionList.get(sectionIdx).setValue(count + 1);
        } while (mCursor.moveToNext());
    }

    private void normalizeDate(Calendar cal) {
        long mill = (long) Math.floor(cal.getTimeInMillis() / MILL_SECONDS_IN_A_DAY);
        cal.setTimeInMillis(mill * MILL_SECONDS_IN_A_DAY);
    }

    // made this method be default method because we want to test it.
    POEvent queryEvent(int sectionIndex, int itemIndex) {
        int cursorIndex = getCursorIndex(sectionIndex, itemIndex);
        if (cursorIndex < 0) {
            return null;
        }
        if (mEventCache.get(cursorIndex) != null) {
            return mEventCache.get(cursorIndex);
        }
        mCursor.moveToPosition(cursorIndex);
        POEvent event = POEvent.createFromCursor(mCursor);
        mEventCache.put(cursorIndex, event);
        return event;
    }

    private int getCursorIndex(int sectionIndex, int itemIndex) {
        if (sectionIndex >= mNumOfItemOnSectionList.size()) {
            return -1;
        }
        int cursorIndex = 0;
        if (itemIndex < mNumOfItemOnSectionList.get(sectionIndex).getValue()) {
            cursorIndex = itemIndex;
        }
        for (int i = 0; i < sectionIndex ; i++) {
            cursorIndex += mNumOfItemOnSectionList.get(i).getValue();
        }
        return cursorIndex;
    }

    public void updateSections(long t1, long t2) {
        if (t1 < 0 || t2 < 0 || t2 <= t1) {
            return;
        }
        int idx1 = findSectionIndex(new Date(t1));
        int idx2 = findSectionIndex(new Date(t2));
        int size = mNumOfItemOnSectionList.size();
        for (int i = idx1; i <= idx2 ; i++) {
            if (i < size && i >= 0) {
                notifyItemChanged(getAdapterPositionForSectionHeader(i));
            }
        }
    }
}