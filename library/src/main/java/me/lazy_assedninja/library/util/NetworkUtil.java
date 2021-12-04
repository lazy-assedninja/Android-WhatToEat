package me.lazy_assedninja.library.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;

@SuppressWarnings({"RedundantSuppression", "deprecation", "unused"})
public class NetworkUtil {

    private final Context context;

    public NetworkUtil(Context context) {
        this.context = context;
    }

    @SuppressWarnings("MissingPermission")
    public boolean isConnected() {
        ConnectivityManager connectivityManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        Network network = connectivityManager.getActiveNetwork();
        if (network == null) return false;
        NetworkCapabilities networkCapabilities = connectivityManager.getNetworkCapabilities(network);
        return networkCapabilities != null &&
                (networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) ||
                networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) ||
                networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_BLUETOOTH));
    }
}