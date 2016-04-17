package nebo15.eppyk.listeners;

import android.content.Context;
import android.util.Log;

import com.mixpanel.android.mpmetrics.MixpanelAPI;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

/**
 * Created by anton on 17/04/16.
 */
public class EventManager {

    private final static String token = "ee8c37904fb6e9b30bb56b3a945c9dad";
    private static MixpanelAPI mixpanel;

    public static void init(Context context) {
        mixpanel = MixpanelAPI.getInstance(context, token);
    }

    public static void trackEvent(String name, HashMap<String, String> params) {
        try {
            if (params != null) {
                JSONObject props = new JSONObject();
                for (String key : params.keySet())
                    props.put(key, params.get(key));
                mixpanel.track(name, props);

            } else
                mixpanel.track(name);
        } catch (JSONException e) {
            Log.e("EPPYK", "Unable to add properties to JSONObject", e);
        }

    }

}
