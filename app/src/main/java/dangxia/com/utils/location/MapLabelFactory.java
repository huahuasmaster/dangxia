package dangxia.com.utils.location;

import android.content.Context;

import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;

public abstract class MapLabelFactory {
    public abstract OverlayOptions getLableOption(String content, LatLng ll, Context context);

    public OverlayOptions getLableOption(String content, double latitude, double longitude, Context context) {
        LatLng latLng = new LatLng(latitude, longitude);
        return getLableOption(content, latLng, context);
    }
}
