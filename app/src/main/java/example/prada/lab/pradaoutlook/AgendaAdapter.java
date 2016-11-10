package example.prada.lab.pradaoutlook;

import android.content.Context;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.v4.util.LruCache;
import android.util.SparseIntArray;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import org.zakariya.stickyheaders.SectioningAdapter;

import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.atomic.AtomicInteger;

import example.prada.lab.pradaoutlook.model.POEvent;
import example.prada.lab.pradaoutlook.store.EventStoreFactory;
import example.prada.lab.pradaoutlook.store.IEventStore;
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

    private final SparseIntArray mNumOfItemOnSectionList = new SparseIntArray();
    private final AtomicInteger mTotalSections = new AtomicInteger(0);

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
        Calendar c = (Calendar) mFrom.clone();
        c.add(Calendar.HOUR, 24 * sectionIndex);
        vh.bind(c, mWeatherMgr.fetchWeather(c.getTimeInMillis()));
    }

    @Override
    public int getNumberOfSections() {
        return mTotalSections.get();
    }

    @Override
    public int getSectionItemUserType(int sectionIndex, int itemIndex) {
        if (mNumOfItemOnSectionList.get(sectionIndex) > 0) {
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
        if (sectionIndex >= mTotalSections.get()) {
            return 0;
        }
        int numEvents = mNumOfItemOnSectionList.get(sectionIndex);
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

    public int getSectionPosition(@NonNull Date date) {
        return getAdapterPositionForSectionHeader(getSectionIndex(date.getTime()));
    }

    public int getSectionIndex(long millSeconds) {
        long diffMillSeconds = millSeconds - mFrom.getTimeInMillis();
        int index =  (int) Math.floor(diffMillSeconds / MILL_SECONDS_IN_A_DAY);
        if (index >= mTotalSections.get()) {
            throw new IndexOutOfBoundsException("the range should be 0 to " +
                mTotalSections + ", but it's " + index);
        }
        return index;
    }

    private void rebuildSectionsMetadata() {
        mEventCache.evictAll();
        mNumOfItemOnSectionList.clear();
        mTotalSections.set(0);
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
        normalizeDate(mFrom);
        normalizeDate(mTo);
        mTo.setTimeInMillis(mTo.getTimeInMillis() + Utility.MILL_SECONDS_A_DAY);
        long days = Utility.getDaysBetween(mFrom, mTo);
        mTotalSections.set((int) days);
        mCursor.moveToFirst();
        do {
            POEvent e = POEvent.createFromCursor(mCursor);
            int sectionIdx = getSectionIndex(e.getFrom().getTime());
            int count = mNumOfItemOnSectionList.get(sectionIdx);
            if (count == 0) {
                mNumOfItemOnSectionList.put(sectionIdx, 1);
            } else {
                mNumOfItemOnSectionList.put(sectionIdx, count + 1);
            }
        } while (mCursor.moveToNext());
    }

    private void normalizeDate(Calendar cal) {
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
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
        if (sectionIndex >= mTotalSections.get()) {
            return -1;
        }
        int cursorIndex = 0;
        if (itemIndex < mNumOfItemOnSectionList.get(sectionIndex)) {
            cursorIndex = itemIndex;
        }
        for (int i = 0; i < mNumOfItemOnSectionList.size(); i++) {
            int k = mNumOfItemOnSectionList.keyAt(i);
            if (k < sectionIndex) {
                cursorIndex += mNumOfItemOnSectionList.get(k);
            } else {
                break;
            }
        }
        return cursorIndex;
    }

    public boolean updateSections(long t1, long t2) {
        if (t1 < 0 || t2 < 0 || t2 <= t1) {
            return false;
        }
        try {
            int idx1 = getSectionIndex(t1);
            int idx2 = getSectionIndex(t2);
            int size = mNumOfItemOnSectionList.size();
            boolean hasChanged = false;
            for (int i = idx1; i <= idx2; i++) {
                if (i < size && i >= 0) {
                    notifyItemChanged(getAdapterPositionForSectionHeader(i));
                    hasChanged = true;
                }
            }
            return hasChanged;
        } catch (IndexOutOfBoundsException e) {
            return false;
        }
    }
}