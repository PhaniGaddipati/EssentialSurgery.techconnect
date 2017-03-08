package org.techconnect.misc;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Phani on 4/16/2016.
 * <p/>
 * Manages downloaded resources for offline capability.
 * Essentially maintains a mapping of URL --> local file
 */
public class ResourceHandler {

    public static final String RESOURCE_HANDLER_PREFS = "org.techconnect.resourcehandler";
    private static ResourceHandler instance = null;
    private SharedPreferences prefs;

    private ResourceHandler(Context context) {
        prefs = context.getSharedPreferences(RESOURCE_HANDLER_PREFS, Context.MODE_PRIVATE);
    }

    public static ResourceHandler get(Context context) {
        if (instance == null) {
            instance = new ResourceHandler(context);
        }
        return instance;
    }

    public boolean hasStringResource(String tag) {
        return prefs.getString(tag, null) != null;
    }

    public String getStringResource(String tag) {
        return prefs.getString(tag, null);
    }

    public void addStringResource(String tag, String s) {
        prefs.edit().putString(tag, s).apply();
    }

    public void clear() {
        prefs.edit().clear().apply();
    }
}
