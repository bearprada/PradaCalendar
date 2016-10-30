package example.prada.lab.pradaoutlook;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;

import com.github.sundeepk.compactcalendarview.CompactCalendarView;

import org.zakariya.stickyheaders.StickyHeaderLayoutManager;

import java.util.Calendar;
import java.util.Date;

import example.prada.lab.pradaoutlook.model.MockEventStore;
import example.prada.lab.pradaoutlook.utils.Utility;

public class CalendarActivity extends AppCompatActivity implements AgendaAdapter.OnAgendaScrolledListener,
        CompactCalendarView.CompactCalendarViewListener {

    private AgendaAdapter mAdapter;
    private CompactCalendarView mCalendarView;
    private RecyclerView mAgendaView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mAgendaView = (RecyclerView) findViewById(R.id.agenda_list);
        StickyHeaderLayoutManager lm = new StickyHeaderLayoutManager();
        mAgendaView.setLayoutManager(lm);

        MockEventStore store = MockEventStore.getInstance(this);
        Calendar from = store.getFirstEventTime();
        Calendar to = store.getLatestEventTime();

        mAdapter = new AgendaAdapter(this, from, to);
        mAdapter.setListener(this);
        mAgendaView.setAdapter(mAdapter);

        mCalendarView = (CompactCalendarView) findViewById(R.id.calendar_view);

        mCalendarView.setListener(this);
        setTitle(getTitleString(Calendar.getInstance()));
//        mCalendarView.addEvents(new ArrayList<Event>());
    }

    private String getTitleString(Calendar calendar) {
        return calendar.get(Calendar.YEAR) + "" + Utility.convertMonthStr(calendar.get(Calendar.MONTH));
    }

    @Override
    public void onAgendaMoveToDate(Date date) {
        // TODO
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_calendar, menu);
        return true;
    }

    @Override
    public void onDayClick(Date dateClicked) {
        // TODO change the agenda view
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
}
