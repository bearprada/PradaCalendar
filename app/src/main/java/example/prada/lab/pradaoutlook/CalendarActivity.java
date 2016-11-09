package example.prada.lab.pradaoutlook;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;

import com.github.sundeepk.compactcalendarview.CompactCalendarView;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.CompositeMultiplePermissionsListener;
import com.karumi.dexter.listener.multi.DialogOnAnyDeniedMultiplePermissionsListener;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import org.zakariya.stickyheaders.StickyHeaderLayoutManager;

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
import example.prada.lab.pradaoutlook.utils.MockEventsGenerator;
import example.prada.lab.pradaoutlook.utils.Utility;
import example.prada.lab.pradaoutlook.weather.WeatherManager;

public class CalendarActivity extends AppCompatActivity
    implements CompactCalendarView.CompactCalendarViewListener, IEventDataUpdatedListener {

    private AgendaAdapter mAdapter;
    private CompactCalendarView mCalendarView;
    private RecyclerView mAgendaView;
    private CoordinatorLayout mCoordinator;

    final SamplePermissionListener permissionListener = new SamplePermissionListener();

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

        tryMoveAgendaListToDate(new Date());

        Dexter.continuePendingRequestIfPossible(permissionListener);

        Task.callInBackground(new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                if (ActivityCompat.checkSelfPermission(CalendarActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                    Dexter.checkPermissionOnSameThread(permissionListener, Manifest.permission.ACCESS_FINE_LOCATION);
                    return false;
                }
                return true;
            }
        }).continueWith(new Continuation<Boolean, Void>() {
            @Override
            public Void then(Task<Boolean> task) throws Exception {
                queryWeatherWithCurrentLocation();
                return null;
            }
        }, Task.UI_THREAD_EXECUTOR);
    }

    // collect the location information
    private Task<Void> queryWeatherWithCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED) {
            return Task.forError(new IllegalStateException("the location permission doesn't grants"));
        }

        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        // Creating an empty criteria object
        Criteria criteria = new Criteria();
        // Getting the name of the provider that meets the criteria
        String provider = locationManager.getBestProvider(criteria, false);
        if (TextUtils.isEmpty(provider)) {
            return Task.forError(new IllegalStateException("the location provider is empty"));
        }

        Location location = locationManager.getLastKnownLocation(provider);
        if (location == null) {
            return Task.forError(new IllegalStateException("we get the empty result from LocationManager.getLastKnownLocation()"));
        }
        return WeatherManager.getInstance(CalendarActivity.this)
                      .queryWeather(location.getLatitude(), location.getLongitude())
                      .continueWith(new Continuation<WeatherResponse, Void>() {
                          @Override
                          public Void then(Task<WeatherResponse> task) throws Exception {
                              if (task.isFaulted() || task.isCancelled()) {
                                  String msg = "query the weather data is failed : " + task.getError().getMessage();
                                  Snackbar.make(mCoordinator, msg, Snackbar.LENGTH_SHORT).show();
                                  return null;
                              }
                              List<WeatherItem> list = task.getResult().getWeathers();
                              WeatherItem weather1st = list.get(0);
                              WeatherItem weatherLast = list.get(list.size() - 1);
                              mAdapter.updateSections(weather1st.time * 1000, weatherLast.time * 1000);
                              mAgendaView.invalidate();
                              Snackbar.make(mCoordinator, "query the weather data is successful", Snackbar.LENGTH_SHORT).show();
                              return null;
                          }
                      }, Task.UI_THREAD_EXECUTOR);
    }

    private class SamplePermissionListener implements PermissionListener {
        @Override
        public void onPermissionGranted(PermissionGrantedResponse response) {
            queryWeatherWithCurrentLocation();
        }

        @Override
        public void onPermissionDenied(PermissionDeniedResponse response) {
        }

        @Override
        public void onPermissionRationaleShouldBeShown(PermissionRequest permission,
                                                       final PermissionToken token) {
            new AlertDialog.Builder(CalendarActivity.this)
                .setTitle(R.string.permission_rationale_title)
                .setMessage(R.string.permission_rationale_message)
                .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        token.cancelPermissionRequest();
                    }
                })
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        token.continuePermissionRequest();
                    }
                })
                .setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override public void onDismiss(DialogInterface dialog) {
                        token.cancelPermissionRequest();
                    }
                }).show();
        }
    }

    private boolean tryMoveAgendaListToDate(Date date) {
        try {
            mAgendaView.scrollToPosition(mAdapter.getSectionPosition(date));
            return true;
        } catch (IndexOutOfBoundsException e) {
            return false;
        }
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
    public boolean onPrepareOptionsMenu(Menu menu) {
        boolean hasData = EventStoreFactory.getInstance(this).countEvents() > 0;
        menu.findItem(R.id.menuitem_add_items).setVisible(!hasData);
        menu.findItem(R.id.menuitem_clean_items).setVisible(hasData);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        final IEventStore store = EventStoreFactory.getInstance(CalendarActivity.this);
        switch (item.getItemId()) {
            case R.id.menuitem_add_items:
                Task.callInBackground(new Callable<List<POEvent>>() {
                    @Override
                    public List<POEvent> call() throws Exception {
                        List<POEvent> data = MockEventsGenerator.generateEvents();
                        store.addEvents(data);
                        return data;
                    }
                }).onSuccess(new Continuation<List<POEvent>, Void>() {
                    @Override
                    public Void then(Task<List<POEvent>> task) throws Exception {
                        Snackbar.make(mCoordinator, "import the events successful", Snackbar.LENGTH_SHORT).show();
                        invalidateOptionsMenu();
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
                        Snackbar.make(mCoordinator, "cleaning the events successful", Snackbar.LENGTH_SHORT).show();
                        invalidateOptionsMenu();
                        return null;
                    }
                }, Task.UI_THREAD_EXECUTOR);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onDayClick(Date dateClicked) {
        if (!tryMoveAgendaListToDate(dateClicked)) {
            Snackbar.make(mCoordinator, R.string.select_wrong_date, Snackbar.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onMonthScroll(Date firstDayOfNewMonth) {
        tryMoveAgendaListToDate(firstDayOfNewMonth);
        setTitle(getTitleString(firstDayOfNewMonth));
    }

    @Override
    public void onEventsInsert(Cursor cursor) {
        mAdapter.updateEvents();
        updateCalenderView(cursor);
        mAgendaView.invalidate();
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
