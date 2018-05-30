package dangxia.com.utils.location;


import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import dangxia.com.application.ContextApplication;

public class LocationUtil {
    private static LocationUtil instance = null;
    private final String TAG = "location";
    private SharedPreferences sp;

    private LocationUtil() {
        sp = ContextApplication.getContext().getSharedPreferences("location_sp", Context.MODE_PRIVATE);
    }

    public static LocationUtil getInstance() {
        if (instance == null) {
            instance = new LocationUtil();
        }
        return instance;
    }

    public double getLatitude() {
        return Double.parseDouble(sp.getString("latitude", "0"));
    }

    public void setLatitude(double latitude) {
        Log.i(TAG, "setLatitude: 录入" + latitude);
        sp.edit().putString("latitude", "" + latitude).apply();
    }

    public double getLongitude() {
        return Double.parseDouble(sp.getString("longitude", "0"));

    }

    public void setLongitude(double longitude) {
        Log.i(TAG, "setLatitude: 录入" + longitude);
        sp.edit().putString("longitude", "" + longitude).apply();
    }

    public boolean isRealLocation() {
        return sp.getBoolean("real_location", false);
    }

    public void setRealLocation(boolean realLocation) {
        sp.edit().putBoolean("real_location", realLocation).apply();
    }
}
