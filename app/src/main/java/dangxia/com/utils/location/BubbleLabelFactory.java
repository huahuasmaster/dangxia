package dangxia.com.utils.location;

import android.content.Context;

import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;

public class BubbleLabelFactory extends MapLabelFactory {
    @Override
    public OverlayOptions getLableOption(String content, LatLng ll, Context context) {
        return null;
    }
}
