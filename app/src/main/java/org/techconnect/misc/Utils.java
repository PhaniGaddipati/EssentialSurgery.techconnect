package org.techconnect.misc;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Parcel;
import android.os.Parcelable;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Phani on 10/23/2016.
 */

public class Utils {

    private static DateFormat m_ISO8601Local = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");

    public static boolean isNetworkAvailable(Context c) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) c.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    // For writing to a Parcel
    public static <V extends Parcelable> void writeParcelableMap(
            Parcel parcel, int flags, Map<String, V> map) {
        parcel.writeInt(map.size());
        for (Map.Entry<String, V> e : map.entrySet()) {
            parcel.writeString(e.getKey());
            parcel.writeParcelable(e.getValue(), flags);
        }
    }

    // For reading from a Parcel
    public static <V extends Parcelable> Map<String, V> readParcelableMap(
            Parcel parcel, Class<V> vClass) {
        int size = parcel.readInt();
        Map<String, V> map = new HashMap<>(size);
        for (int i = 0; i < size; i++) {
            map.put(parcel.readString(),
                    vClass.cast(parcel.readParcelable(vClass.getClassLoader())));
        }
        return map;
    }

    public static Date parseISO8601Date(String date) throws ParseException {
        return m_ISO8601Local.parse(date);
    }

    public static String formatTimeMillis(long time) {
        return m_ISO8601Local.format(new Date(time));
    }

    public static String formatAttachmentName(String att) {
        String name = att.substring(att.lastIndexOf("/") + 1);
        if (name.contains("?")) {
            name = name.substring(0, name.indexOf('?'));
        }
        return name;
    }
}
