package se.umu.joha0375.gympal;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;


import android.os.SystemClock;
import android.view.KeyEvent;

import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.TextView;

import com.example.gympal.R;

/**
 * @author Johan Hörnblad
 * @version 1.0
 * 2019-9-13
 * This is the Main activity. controls the LocationHandler and the chronometer and updates the GUI.
 * This class is also an observer and will observe on LocationHandler.
 */

public class MainActivity extends AppCompatActivity implements RepositoryObserver {

    public static String EXTRA_SPEED = "extra_speed";
    public static String EXTRA_DISTANCE = "extra_distance";
    public static String EXTRA_TIME = "extra_time";

    private static String KEY_TIME_BASE = "time_base";
    private static String KEY_GPS_IS_RUNNING = "gps_running";
    private static String KEY_TOTAL_DISTANCE = "total_distance";

    private static final int REQUEST_PERMISSIONS_REQUEST_CODE = 34;

    private Button mStartNStopButton;
    private TextView mTextMessage;
    private Boolean mGpsIsRunning = false;
    private Boolean mChronometerIsRunning = false;
    private Chronometer mChronometer;
    private long mTotalTime = 0;
    private long mChronometerBase;
    private TextView mMeterView;
    private TextView mKilometerView;
    private DistanceAndTimeFactory mDistanceAndTimeFactory;
    private float mTotalDistance = 0;
    private int test = 0;

    private LocationHandler mLocationHandler;


    /**
     * The onCreate method will execute every method and set upp the activity so it can run correctly.
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //myReceiver  = new MyReceiver();

        setContentView(R.layout.activity_main);

        mTextMessage = findViewById(R.id.message);
        mMeterView = findViewById(R.id.meter);
        mKilometerView = findViewById(R.id.kilometers);
        mDistanceAndTimeFactory = new DistanceAndTimeFactory();
        getSupportActionBar().setTitle(R.string.home_bar_title);

        mChronometer = findViewById(R.id.stop_watch_1);
        mStartNStopButton = (Button) findViewById(R.id.startNStop);
        mLocationHandler = new LocationHandler(this);
        mLocationHandler.registerObserver(this);
        startNstopButtonManager();
    }

    /**
     * On resume the mGpsIsRunning variable will be checked.
     * If the GPS is or is not running the relevant settings will be set.
     */
    @Override
    protected void onResume() {
        super.onResume();
        if (mGpsIsRunning) {
            gpsIsRunning();
            mStartNStopButton.setBackgroundColor(getResources().getColor(R.color.stop_red));
        } else {
            gpsIsNotRunning();
            mStartNStopButton.setBackgroundColor(getResources().getColor(R.color.start_green));
        }
    }

    /**
     * Starts the chronometer.
     */
    public void startChronometer() {
        if (!mChronometerIsRunning) {
            mChronometer.setBase(SystemClock.elapsedRealtime() - mTotalTime);
            mChronometer.start();
            mChronometerIsRunning = true;
        }

    }


    /**
     * This method stops the chronometer and stores the total time in a member variable.
     */
    public void stopChronometer() {
        if (mChronometerIsRunning) {
            mTotalTime = SystemClock.elapsedRealtime() - mChronometer.getBase();
            mChronometer.stop();
            mChronometerIsRunning = false;
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        mLocationHandler.stopLocationUpdates();
    }


    /**
     * this method manage the Click listener for the start/stop button.
     * The button has two different states, Start or Stop.
     * This method handles the change of the buttons state.
     */
    private void startNstopButtonManager() {
        mStartNStopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!mGpsIsRunning) {
                    if(!checkPermissionAccess()){
                        requestPermissions();
                    }
                    mStartNStopButton.setBackgroundColor(getResources().getColor(R.color.stop_red));
                    mGpsIsRunning = true;
                    gpsIsRunning();

                } else {
                    gpsIsNotRunning();
                    mGpsIsRunning = false;
                    mStartNStopButton.setBackgroundColor(getResources().getColor(R.color.start_green));
                    changeActivity();
                }

            }
        });
    }

    /**
     * Sets all the relevant things in the correct state when the GPS is running
     */
    private void gpsIsRunning() {
        updateDistanceTextViews(mTotalDistance);
        mStartNStopButton.setText(R.string.stop);
        mLocationHandler.startLocationUpdates();
        startChronometer();
    }

    /**
     * this method sets every relevant thing in the correct state when the GPS is not running.
     */
    private void gpsIsNotRunning() {
        mMeterView.setText(R.string.meters_zero);
        mKilometerView.setText(R.string.kilometers_zero);
        mLocationHandler.stopLocationUpdates();
        mStartNStopButton.setText(R.string.start);
        stopChronometer();

    }

    /**
     * This method will create an intent store the relevant data in extras and then
     * start the activity for viewing the result CardioResultActivity.
     */
    private void changeActivity() {
        long speed = ((long) mTotalDistance) / (mTotalTime / 1000);
        Intent intent = new Intent(MainActivity.this, CardioResultActivity.class);
        intent.putExtra(EXTRA_TIME, mTotalTime);
        intent.putExtra(EXTRA_DISTANCE, mTotalDistance);
        intent.putExtra(EXTRA_SPEED, speed);
        startActivity(intent);

    }

    /**
     * This method is the observer method that the observable will use.
     * when this method is used the parameter will change.
     * @param distance - the current total distance.
     */
    @Override
    public void onDistanceUpdate(float distance) {
        mTotalDistance = distance;
        updateDistanceTextViews(distance);
    }

    /**
     * Update the textViews that will present the distance.
     * The method is using DistanceAndTimeFactory present the distance on a readable way.
     * @param distance - the current total distance
     */
    private void updateDistanceTextViews(float distance) {
        mDistanceAndTimeFactory.convertDistance(distance);
        int kilometers = mDistanceAndTimeFactory.getKilometers();
        int meters = mDistanceAndTimeFactory.getMeters();
        if (meters < 10) {
            mMeterView.setText("00" + meters);
        } else if (meters < 100) {
            mMeterView.setText("0" + meters);
        } else {
            mMeterView.setText("" + meters);
        }

        if (kilometers < 10) {
            mKilometerView.setText("0" + kilometers);
        } else {
            mKilometerView.setText("" + kilometers);
        }

    }


    /**
     * When there is somthing stored in the InstanceState this method will store the information in
     * attributes.
     * @param savedInstanceState
     */
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mGpsIsRunning = savedInstanceState.getBoolean(KEY_GPS_IS_RUNNING);
        if (mGpsIsRunning) {
            mChronometerBase = savedInstanceState.getLong(KEY_TIME_BASE);
            mTotalTime = SystemClock.elapsedRealtime() - mChronometerBase;
            mTotalDistance = savedInstanceState.getFloat(KEY_TOTAL_DISTANCE);
            mLocationHandler.setDistance(mTotalDistance);
        }
    }

    /**
     * Saves the instance that is needed to be saved for the applikation to work if the activity
     * is restarting.
     * @param savedInstanceState
     */
    @Override
    protected void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        if (mGpsIsRunning) {
            savedInstanceState.putLong(KEY_TIME_BASE, mChronometer.getBase());
            savedInstanceState.putFloat(KEY_TOTAL_DISTANCE, mTotalDistance);
        }
        savedInstanceState.putBoolean(KEY_GPS_IS_RUNNING, mGpsIsRunning);
    }

    /**
     * This method manage the back button.
     * When this activity is running the back button will take you to your phones home page.
     * @param keyCode
     * @param event
     * @return
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            Intent a = new Intent(Intent.ACTION_MAIN);
            a.addCategory(Intent.CATEGORY_HOME);
            a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(a);
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }

    /**
     * Checks if the application has the permissions approved for GPS.
     * @return - true if the permissions i approved.
     */
   private boolean checkPermissionAccess() {
        return ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    /**
     * Requests permission for GPS
     */
    private void requestPermissions() {
        ActivityCompat.requestPermissions(this, new String[]{
                Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_PERMISSIONS_REQUEST_CODE);

    }
}

