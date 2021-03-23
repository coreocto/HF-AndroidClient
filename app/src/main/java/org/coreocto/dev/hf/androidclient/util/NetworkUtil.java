package org.coreocto.dev.hf.androidclient.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import org.coreocto.dev.hf.androidclient.AppConstants;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class NetworkUtil {

    private static final String TAG = "NetworkUtil";

    public static boolean isNetworkConnected(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);

        if (connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI)
                .isConnected() || telephonyManager.getDataState() == TelephonyManager.DATA_CONNECTED) {
            return true;
        } else {
            return false;
        }
    }

    public static boolean isHostFound(String hostname) {
        try {
            final InetAddress address = InetAddress.getByName(hostname);
            return !address.equals(AppConstants.EMPTY_STRING);
        } catch (UnknownHostException e) {
            Log.d(TAG, "host:" + hostname + " not found", e);
        }
        return false;
    }
}
