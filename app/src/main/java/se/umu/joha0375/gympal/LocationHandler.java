package se.umu.joha0375.gympal;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.location.Location;

import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.Date;

/**
 *
 *  @author Johan Hörnblad
 *  @version 1.0
 *  2019-09-10
 *
 *  this class is an subject that a observer listens to.
 *  This class is setting up for a repeating update of l
 */
public class LocationHandler implements Subject {

    private LocationRequest locationRequest;
    private Boolean mGpsIsRunning = false;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private float mTotalDistance = 0;
    private Location mCurrentLocation;
    private LocationCallback locationCallback;
    private Date mOldDate;
    private Date mNewDate;
    private Activity mActivity;
    private RepositoryObserver observer;

    /**
     * The class's constructor, runs the methods that is required for location updates,
     * The last location i saved, and a location request is created and the LocationCallback
     * is executed here.
     * @param context - the activity that the GPS will run on.
     */
    public LocationHandler(Activity context) {
        this.mActivity = context;
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context);
        getCurrentLocation();
        createLocationRequest();
        setUpLocationCallback();

    }

    /**
     * Creates a Location Callback, on the override method, the callback will update the distance.
     */
    private void setUpLocationCallback() {
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {

                if (locationResult == null) {
                    return;
                }
                Location newLocation = locationResult.getLastLocation();
                storeNewDistance(newLocation, mCurrentLocation);
                mCurrentLocation = newLocation;

            }
        };
    }

    public void setDistance(float distance) {
        mTotalDistance = distance;
    }

    /**
     * Updates the distance by computing the distance between the latest and the new location
     * and add the distance to the total distance.
     * @param newLocation - the new location
     * @param lastLocation -  the old location
     */
    private void storeNewDistance(Location newLocation, Location lastLocation) {
        try {
            float oldDistance = mTotalDistance;
            float newDistance = mTotalDistance + lastLocation.distanceTo(newLocation);
            if (oldDistance != newDistance) {
                mTotalDistance += newDistance;
                notifyObservers();
            }
        } catch (NullPointerException e) {

        }
    }

    /**
     * This method will stop the location updates.
     */
    public void stopLocationUpdates() {
        mGpsIsRunning = false;
        fusedLocationProviderClient.removeLocationUpdates(locationCallback);
        mNewDate = new Date();
        mTotalDistance = 0;
    }

    /**
     * Creates a location request with relevant settings.
     */
    protected void createLocationRequest() {
        locationRequest = LocationRequest.create();
        locationRequest.setInterval(5000);
        locationRequest.setFastestInterval(2000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest);

    }

    /**
     * Starts the location updates.
     */
    public void startLocationUpdates() {
        if (ContextCompat.checkSelfPermission(mActivity, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mOldDate = new Date();
            mGpsIsRunning = true;
            fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, null);
        }
    }

    /**
     * Gets the current location by calling getLastLocation.
     */
    private void getCurrentLocation() {
        if (ContextCompat.checkSelfPermission(mActivity, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationProviderClient.getLastLocation()
                    .addOnSuccessListener(mActivity, new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            if (location != null) {
                                mCurrentLocation = location;
                            }
                        }
                    });
        }

    }

    /**
     * Register a observer to observe this class.
     * @param repositoryObserver -  the observer Object.
     */
    @Override
    public void registerObserver(RepositoryObserver repositoryObserver) {
        observer = repositoryObserver;
    }

    /**
     * Removes A Observer
     * @param repositoryObserver - the observer Object
     */
    @Override
    public void removeObserver(RepositoryObserver repositoryObserver) {
        observer = null;
    }

    /**
     * Notifies changes to the observers.
     */
    @Override
    public void notifyObservers() {
        observer.onDistanceUpdate(mTotalDistance);
    }
}
