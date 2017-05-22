package ch.ffhs.fs17.esa.locationgetter;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.widget.TextView;

public class MainActivity extends Activity {

    private static final long ONE_MIN = 1000 * 60;
    private static final long TWO_MIN = ONE_MIN * 2;
    private static final long FIVE_MIN = ONE_MIN * 5;
    private static final long MEASURE_TIME = 1000 * 30;
    private static final long POLLING_FREQ = 1000 * 10;
    private static final float MIN_ACCURACY = 25.0f;
    private static final float MIN_LAST_READ_ACCURACY = 500.0f;
    private static final float MIN_DISTANCE = 10.0f;

    // Views for display location information
    private TextView mAccuracyView;
    private TextView mTimeView;
    private TextView mLatView;
    private TextView mLngView;

    private int mTextViewColor = Color.GRAY;

    // Current best location estimate
    private Location mBestReading;

    // Reference to the LocationManager and LocationListener
    private LocationManager mLocationManager;
    private LocationListener mLocationListener;

    private final String TAG = "LocationGetLocationActivity";

    private boolean mFirstUpdate = true;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        mAccuracyView = (TextView) findViewById(R.id.accuracy_view);
        mTimeView = (TextView) findViewById(R.id.time_view);
        mLatView = (TextView) findViewById(R.id.lat_view);
        mLngView = (TextView) findViewById(R.id.lng_view);

        // Acquire reference to the LocationManager
        if (null == (mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE)))
            finish();

        // Get best last location measurement
        mBestReading = bestLastKnownLocation(MIN_LAST_READ_ACCURACY, FIVE_MIN);

        // Display last reading information
        if (null != mBestReading) {

            updateDisplay(mBestReading);

        } else {

            mAccuracyView.setText("No Initial Reading Available");

        }

        // TODO: LocationListener erstellen
        mLocationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {

            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Determine whether initial reading is
        // "good enough". If not, register for
        // further location updates

        if (null == mBestReading
                || mBestReading.getAccuracy() > MIN_LAST_READ_ACCURACY
                || mBestReading.getTime() < System.currentTimeMillis()
                - TWO_MIN) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }

            // TODO: Register for network location updates

            // TODO: Register for GPS location updates

            // Schedule a runnable to unregister location listeners
            Executors.newScheduledThreadPool(1).schedule(new Runnable() {

                @Override
                public void run() {

                    Log.i(TAG, "location updates cancelled");
                    // TODO: unregister location updates.

                }
            }, MEASURE_TIME, TimeUnit.MILLISECONDS);
        }
    }

    // Unregister location listeners
    @Override
    protected void onPause() {
        super.onPause();
        // TODO: unregister location updates.
    }

    // Get the last known location from all providers
    // return best reading that is as accurate as minAccuracy and
    // was taken no longer then minAge milliseconds ago. If none,
    // return null.

    private Location bestLastKnownLocation(float minAccuracy, long maxAge) {

        Location bestResult = null;
        float bestAccuracy = Float.MAX_VALUE;
        long bestAge = Long.MIN_VALUE;

        List<String> matchingProviders = mLocationManager.getAllProviders();

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return null;
        }

        // TODO: from the list of providers, find the one  with best accuracy.

        // Return best reading or null
        if (bestAccuracy > minAccuracy
                || (System.currentTimeMillis() - bestAge) > maxAge) {
            return null;
        } else {
            return bestResult;
        }
    }

    // Update display
    private void updateDisplay(Location location) {

        mAccuracyView.setText("Accuracy:" + location.getAccuracy());

        mTimeView.setText("Time:"
                + new SimpleDateFormat("MM/dd/yyyy HH:mm:ss", Locale
                .getDefault()).format(new Date(location.getTime())));

        mLatView.setText("Longitude:" + location.getLongitude());

        mLngView.setText("Latitude:" + location.getLatitude());

    }

    private void ensureColor() {
        if (mFirstUpdate) {
            setTextViewColor(mTextViewColor);
            mFirstUpdate = false;
        }
    }

    private void setTextViewColor(int color) {

        mAccuracyView.setTextColor(color);
        mTimeView.setTextColor(color);
        mLatView.setTextColor(color);
        mLngView.setTextColor(color);

    }

}