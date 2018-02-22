package dangxia.com.utils.location;

import android.util.Log;

import java.text.DecimalFormat;

public class DistanceUtil {
    private static double p(double x) {
        return Math.PI / 180 * x;
    }

    public static double distance(double latitude1, double longitude1, double latitude2, double longitude2) {
        return 6370996.81 * Math.acos(
                Math.sin(p(latitude1)) * Math.sin(p(latitude2)) + Math.cos(p(latitude1)) * Math.cos(p(latitude2)) * Math.cos(p(longitude2) - p(longitude1)));
    }

    public static double km(double latitude1, double longitude1, double latitude2, double longitude2) {
        double distance = distance(latitude1, longitude1, latitude2, longitude2);
        double km = distance * 0.001;
        Log.i("distance", "km: " + latitude1 + "," + longitude1 + "与" + latitude2 + "," + longitude2 + "的距离为:" + km);
        DecimalFormat format = new DecimalFormat("#.00");
        return Double.parseDouble(format.format(km));
    }
}
