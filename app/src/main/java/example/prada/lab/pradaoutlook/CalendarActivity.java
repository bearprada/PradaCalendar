package example.prada.lab.pradaoutlook;

import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.github.sundeepk.compactcalendarview.CompactCalendarView;

import org.zakariya.stickyheaders.StickyHeaderLayoutManager;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Callable;

import bolts.Continuation;
import bolts.Task;
import example.prada.lab.pradaoutlook.model.IEventDataUpdatedListener;
import example.prada.lab.pradaoutlook.model.POEvent;
import example.prada.lab.pradaoutlook.model.WeatherItem;
import example.prada.lab.pradaoutlook.model.WeatherResponse;
import example.prada.lab.pradaoutlook.store.EventStoreFactory;
import example.prada.lab.pradaoutlook.store.IEventStore;
import example.prada.lab.pradaoutlook.utils.Utility;
import example.prada.lab.pradaoutlook.weather.WeatherManager;

public class CalendarActivity extends AppCompatActivity
    implements CompactCalendarView.CompactCalendarViewListener, IEventDataUpdatedListener {

    private AgendaAdapter mAdapter;
    private CompactCalendarView mCalendarView;
    private RecyclerView mAgendaView;
    private CoordinatorLayout mCoordinator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final IEventStore store = EventStoreFactory.getInstance(this);
        store.addListener(CalendarActivity.this);

        mCoordinator = (CoordinatorLayout) findViewById(R.id.coordinator);
        mAgendaView = (RecyclerView) findViewById(R.id.agenda_list);
        StickyHeaderLayoutManager lm = new StickyHeaderLayoutManager();
        mAgendaView.setLayoutManager(lm);

        mCalendarView = (CompactCalendarView) findViewById(R.id.calendar_view);
        mCalendarView.setListener(this);
        updateCalenderView(store.getEvents());

        setTitle(getTitleString(mCalendarView.getFirstDayOfCurrentMonth()));

        mAdapter = new AgendaAdapter(this);
        mAgendaView.setAdapter(mAdapter);

        // FIXME emulate the location manager
        Task.callInBackground(new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                Thread.sleep(3000);
                return null;
            }
        }).onSuccessTask(new Continuation<Void, Task<WeatherResponse>>() {
            @Override
            public Task<WeatherResponse> then(Task<Void> task) throws Exception {
                return WeatherManager.getInstance(CalendarActivity.this)
                                     .handleLocation(23.6978, 120.9605);
            }
        }).continueWith(new Continuation<WeatherResponse, Void>() {
            @Override
            public Void then(Task<WeatherResponse> task) throws Exception {
                if (task.isFaulted() || task.isCancelled()) {
                    task.getError().printStackTrace();
                    return null;
                }
                List<WeatherItem> list = task.getResult().getWeathers();
                WeatherItem weather1st = list.get(0);
                WeatherItem weatherLast = list.get(list.size() - 1);
                mAdapter.updateSections(weather1st.time * 1000, weatherLast.time * 1000);
                return null;
            }
        }, Task.UI_THREAD_EXECUTOR);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventStoreFactory.getInstance(this).removeListener(this);
    }

    private String getTitleString(Date date) {
        int year = date.getYear() + 1900;
        int month = date.getMonth();
        return String.format("%s %s", year, Utility.convertMonthStr(month).substring(0, 3));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_calendar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        final IEventStore store = EventStoreFactory.getInstance(CalendarActivity.this);
        switch (item.getItemId()) {
            case R.id.menuitem_add_items:
                Task.callInBackground(new Callable<List<POEvent>>() {
                    @Override
                    public List<POEvent> call() throws Exception {
                        // generate the 200 events between last two months to now
                        final int NUM_EVENTS = 100;
                        Calendar c1 = Calendar.getInstance();
                        c1.set(Calendar.DAY_OF_YEAR, c1.get(Calendar.DAY_OF_YEAR) - 30);
                        Calendar c2 = Calendar.getInstance();
                        c2.set(Calendar.DAY_OF_YEAR, c2.get(Calendar.DAY_OF_YEAR) + 30);
                        final long t1 = c1.getTimeInMillis();
                        long t2 = c2.getTimeInMillis();
                        final long gap = (t2 - t1) / NUM_EVENTS;

                        java.util.Random random = new java.util.Random();
                        List<POEvent> data = new ArrayList<>();
                        for (int i = 0; i < NUM_EVENTS ; i++) {
                            long eventT1 = t1 + (i * gap);
                            long eventT2 = eventT1 + (random.nextInt(3600) * 1000);
                            data.add(new POEvent("Event-" + i, "TODO", new Date(eventT1), new Date(eventT2)));
                        }
                        store.addEvents(data);
                        return data;
                    }
                }).onSuccess(new Continuation<List<POEvent>, Void>() {
                    @Override
                    public Void then(Task<List<POEvent>> task) throws Exception {
                        Snackbar.make(mCoordinator, "import the events successful", Snackbar.LENGTH_SHORT).show();
                        return null;
                    }
                }, Task.UI_THREAD_EXECUTOR);
                return true;
            case R.id.menuitem_clean_items:
                Task.callInBackground(new Callable<Void>() {
                    @Override
                    public Void call() throws Exception {
                        store.removeAllRecords();
                        return null;
                    }
                }).continueWith(new Continuation<Void, Void>() {
                    @Override
                    public Void then(Task<Void> task) throws Exception {
                        onEventsInsert(store.getEvents());
                        return null;
                    }
                });
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onDayClick(Date dateClicked) {
        try {
            mAgendaView.scrollToPosition(mAdapter.findSectionPosition(dateClicked));
        } catch (IndexOutOfBoundsException e) {
            Snackbar.make(mCoordinator, R.string.select_wrong_date, Snackbar.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onMonthScroll(Date firstDayOfNewMonth) {
        try {
            mAgendaView.scrollToPosition(mAdapter.findSectionPosition(firstDayOfNewMonth));
        } catch (IndexOutOfBoundsException ignored) {}
        setTitle(getTitleString(firstDayOfNewMonth));
    }

    @Override
    public void onEventsInsert(Cursor cursor) {
        mAdapter.updateEvents();
        updateCalenderView(cursor);
    }

    private void updateCalenderView(@NonNull Cursor cursor) {
        cursor.moveToFirst();
        mCalendarView.removeAllEvents();
        while(cursor.moveToNext()) {
            POEvent e = POEvent.createFromCursor(cursor);
            mCalendarView.addEvent(e.toEvent(), false);
        }
        mCalendarView.invalidate();
    }
}
