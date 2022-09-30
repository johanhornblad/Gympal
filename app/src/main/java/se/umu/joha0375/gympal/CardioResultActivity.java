package se.umu.joha0375.gympal;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.widget.TextView;

import com.example.gympal.R;


/**
 *  @author Johan Hörnblad
 *  @version 1.0
 *  2019-09-13
 *
 *  This Activity present result values the GUI.
 */

public class CardioResultActivity extends AppCompatActivity {

    private TextView mResultSpeedView;
    private TextView mResultDistanceView;
    private TextView mResultSecond;
    private TextView mResultMinute;
    private TextView mResultHour;


    private int mSpeed;
    private int mMeter;
    private int mKilometer;
    private long mTime;

    private DistanceAndTimeFactory mDistanceAndTimeFactory;

    /**
     * Sets all the necessary thing so that the activity works correctly.
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cardio_result);
        mDistanceAndTimeFactory = new DistanceAndTimeFactory();
        mResultMinute = findViewById(R.id.result_minute);
        mResultHour = findViewById(R.id.result_hour);
        mResultSecond = findViewById(R.id.result_second);
        mResultDistanceView =  findViewById(R.id.result_distance);
        mResultSpeedView = findViewById(R.id.result_speed);
        getSupportActionBar().setTitle(R.string.result);
        ActionBar actionBar = getSupportActionBar();

        actionBar.setDisplayHomeAsUpEnabled(true);

        getExtras();
        setTextResultTextViews();
    }


    /**
     * Get the extras that has been sent from the MainActivity.
     * And set the extras in member variables.
     */
    private void getExtras(){
        Bundle extras  = getIntent().getExtras();
        mTime = extras.getLong(MainActivity.EXTRA_TIME);
        float distance = extras.getFloat(MainActivity.EXTRA_DISTANCE);
        mDistanceAndTimeFactory.convertDistance(distance);
        mMeter = mDistanceAndTimeFactory.getMeters();
        mKilometer = mDistanceAndTimeFactory.getKilometers();
        mSpeed = (int)(extras.getLong(MainActivity.EXTRA_SPEED)*3.6);

    }

    /**
     * Sets the text in the TextViews.
     */
    private void setTextResultTextViews(){
        setResultTimeTextViews();
        mResultDistanceView.setText(""+mKilometer + " , " + mMeter);
        mResultSpeedView.setText(""+mSpeed);
    }

    /**
     * Sets the text int the texVies for presenting the time.
     * the DistanceAndTimeFactory will be used to get the right time format.
     */
    private void setResultTimeTextViews(){
        mDistanceAndTimeFactory.convertTime(mTime);
        System.out.println(mDistanceAndTimeFactory.getSecounds());
        System.out.println(mDistanceAndTimeFactory.getHours());
        System.out.println(mDistanceAndTimeFactory.getMinutes());
        mResultHour.setText("" + mDistanceAndTimeFactory.getHours());
        mResultMinute.setText(", "+mDistanceAndTimeFactory.getMinutes());
        mResultSecond.setText(", "+mDistanceAndTimeFactory.getSecounds());

    }

    /**
     * Handles the backButton, In this activity the back button will change the acitvity to the
     * MainActivity
     * @param keyCode
     * @param event
     * @return
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            changeActivity();
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }

    /**
     * Listener for the up button in the action bar, that will change tha activity to MainActivity.
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
               changeActivity();
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Changes the activity to MainActivity.
     */
    private void changeActivity(){
        Intent intent = new Intent(CardioResultActivity.this, MainActivity.class);
        startActivity(intent);
    }
}
