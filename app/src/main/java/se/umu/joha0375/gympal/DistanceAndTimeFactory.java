package se.umu.joha0375.gympal;


/**
 *  @author Johan Hörnblad
 *  @version 1.0
 *  2019-9-13
 *
 *  This class converts distance and time values to value that can be represented in the GUI.
 */

public class DistanceAndTimeFactory {

    private int mMeters;
    private int mKilometers;
    private int mHours;
    private int mMinutes;
    private int mSeconds;

    public DistanceAndTimeFactory() {
    }

    /**
     * @return - Distance in kilometers
     */
    public int getKilometers() {

        return mKilometers;
    }

    /**
     * @return - Distance in meters
     */
    public int getMeters() {
        return mMeters;
    }

    /**
     * Converts distance in to kilometers and meters.
     * @param distance - total distance in meters.
     */
    public void convertDistance(float distance) {
        int totalMeters = (int) distance;
        int meters;
        if(totalMeters>1000){
            meters = totalMeters % 1000;
            mKilometers = (totalMeters - meters)/1000;
            mMeters = meters;
        } else{
            mKilometers = 0;
            mMeters = totalMeters;
        }

    }

    /**
     * converts time to hours, minutes and seconds
     * @param time - the time in milliseconds
     */
    public float convertTime(long time){
        float totalSeconds = toSeconds(time);
        float hours = setHours(totalSeconds);
        if(hours%1 != 0){
            float minutesLeft = ((hours%1)*60);
            if(minutesLeft%1 != 0){
                mSeconds = (int)((minutesLeft%1)*60);
            }
            setMinutes(minutesLeft);
        } else {
          mSeconds = 0;
          setMinutes(0);
        }

        return (((float)time/1000));

    }

    /**
     * Sets the member variable that will hold minutes
     * @param time - the time in minutes.
     */
    private void setMinutes(float time){
        if(time<1){
            mMinutes = 0;
        } else{
            mMinutes = (int)time;
        }
    }

    /**
     * Sets the member variable that will hold the hours.
     * converts seconds to hours.
     * @param time - the time in seconds.
     * @return hours as a float.
     */
    private float setHours(float time){
        float hours = (time/60)/60;
        if(hours<1){
            mHours = 0;
        } else{
            mHours = (int)hours;
        }
        return hours;
    }

    /**
     * Converts milliseconds to seconds.
     * @param time - the time in milliseconds.
     * @return - time in seconds.
     */
    private float toSeconds(long time){
        return ((float)time)/1000;
    }


    /**
     * @return  - hours
     */
    public int getHours() {
        return mHours;
    }

    /**
     * @return - minutes
     */
    public int getMinutes() {
        return mMinutes;
    }

    /**
     * @return - seconds
     */
    public int getSecounds() {
        return mSeconds;
    }
}
