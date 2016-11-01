package example.prada.lab.pradaoutlook;

import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;

import com.github.sundeepk.compactcalendarview.CompactCalendarView;

import org.zakariya.stickyheaders.StickyHeaderLayoutManager;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Callable;

import bolts.Continuation;
import bolts.Task;
import example.prada.lab.pradaoutlook.store.EventStoreFactory;
import example.prada.lab.pradaoutlook.model.IEventDataUpdatedListener;
import example.prada.lab.pradaoutlook.store.IEventStore;
import example.prada.lab.pradaoutlook.model.POEvent;
import example.prada.lab.pradaoutlook.utils.Utility;

public class CalendarActivity extends AppCompatActivity implements CompactCalendarView.CompactCalendarViewListener,
                                                                   IEventDataUpdatedListener {

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

        mCoordinator = (CoordinatorLayout) findViewById(R.id.coordinator);
        mAgendaView = (RecyclerView) findViewById(R.id.agenda_list);
        StickyHeaderLayoutManager lm = new StickyHeaderLayoutManager();
        mAgendaView.setLayoutManager(lm);

        final IEventStore store = EventStoreFactory.getInstance(this);
        Calendar from = store.getFirstEventTime();
        Calendar to = store.getLatestEventTime();
        store.addListener(this);

        mAdapter = new AgendaAdapter(this, from, to);
        mAgendaView.setAdapter(mAdapter);

        mCalendarView = (CompactCalendarView) findViewById(R.id.calendar_view);
        mCalendarView.setListener(this);

        setTitle(getTitleString(Calendar.getInstance()));

        // inject the test data
        // TODO inserting the test data when the store is empty
        Task.callInBackground(new Callable<List<POEvent>>() {
            @Override
            public List<POEvent> call() throws Exception {
                java.util.Random random = new java.util.Random();
                // generate the 200 events between last two months to now
                final int NUM_EVENTS = 200;
                Calendar c1 = Calendar.getInstance();
                c1.set(Calendar.MONTH, c1.get(Calendar.MONTH) - 2);
                Calendar c2 = Calendar.getInstance();
                long t1 = c1.getTimeInMillis();
                long t2 = c2.getTimeInMillis();
                long gap = (t2 - t1) / NUM_EVENTS;
                List<POEvent> datas = new ArrayList<>();
                for (int i = 0; i < NUM_EVENTS ; i++) {
                    long eventT1 = t1 + (i * gap);
                    long eventT2 = eventT1 + (random.nextInt(3600) * 1000);
                    datas.add(new POEvent("Event-" + random.nextInt(10000), "TODO", new Date(eventT1), new Date(eventT2)));
                }
                return datas;
            }
        }).onSuccess(new Continuation<List<POEvent>, Void>() {
            @Override
            public Void then(Task<List<POEvent>> task) throws Exception {
                store.addEvents(task.getResult());
                return null;
            }
        }, Task.UI_THREAD_EXECUTOR);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventStoreFactory.getInstance(this).removeListener(this);
    }

    private String getTitleString(Calendar calendar) {
        return calendar.get(Calendar.YEAR) + "" + Utility.convertMonthStr(calendar.get(Calendar.MONTH));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_calendar, menu);
        return true;
    }

    @Override
    public void onDayClick(Date dateClicked) {
        try {
            // FIXME the scroll position isn't correct it might because the coordinate layout.
            mAgendaView.smoothScrollToPosition(mAdapter.findSectionPosition(dateClicked));
        } catch (IndexOutOfBoundsException e) {
            Snackbar.make(mCoordinator, R.string.select_wrong_date, Snackbar.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onMonthScroll(Date firstDayOfNewMonth) {
        // put the limit month on this library if it's over the end of duration
        // ref : https://github.com/SundeepK/CompactCalendarView/issues/51
//        mCalendarView.shouldScrollMonth();

        // TODO change the agenda view
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(firstDayOfNewMonth);
        setTitle(getTitleString(calendar));
    }

    @Override
    public void onEventsInsert(Collection<POEvent> events) {
        mAdapter.notifyDataSetChanged(); // FIXME only update the sections that's in the range of events
        for (POEvent e : events) {
            mCalendarView.addEvent(e.toEvent(), false);
        }
        mCalendarView.invalidate();
    }

    @Override
    public void onEventInsert(POEvent event) {
        mAdapter.updateEvent(event);
        mCalendarView.addEvent(event.toEvent(), true);
    }

    @Override
    public void onEventUpdate(POEvent event) {
        mAdapter.updateEvent(event);
        // FIXME is it the good way to implement it?
        mCalendarView.removeEvent(event.toEvent(), false);
        mCalendarView.addEvent(event.toEvent(), true);
    }

    @Override
    public void onEventDelete(POEvent event) {
        mAdapter.updateEvent(event);
        mCalendarView.removeEvent(event.toEvent(), true);
    }
}
