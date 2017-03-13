package com.sourcey.materiallogindemo.Utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AlertDialog;

/**
 * Created by GuillermoArturo on 27/04/2016.
 */
public class GeneralConstants {

    public static final String KEY_IMG_PATH = "image/*";
    public static final String KEY_IMG = "img";
    public static final String KEY_PHOTO_FACEBOOK = "picture";
    public static final String KEY_PHOTO_DATA_FACEBOOK = "data";
    public static final String KEY_PHOTO_URL_FACEBOOK = "url";

    public static boolean checkNetwork(Context context) {
        ConnectivityManager cm =
                (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();

        return isConnected;
    }

    public static boolean checkWIFI(Context context) {
        ConnectivityManager cm =
                (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isWiFi = activeNetwork.getType() == ConnectivityManager.TYPE_WIFI;

        return isWiFi;
    }

    public static void showMessageWIFI(Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage("No se encuentra conectado a una Red WIFI")
                .setTitle("WIFI no disponible")
                .setPositiveButton(android.R.string.ok, null);
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public static void showMessageConnection(Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage("No cuenta con conexión a internet!")
                .setTitle("Conexión a internet no disponible")
                .setPositiveButton(android.R.string.ok, null);
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public static boolean checkLong(String message, int longitude){
        if(message.length() < longitude) {
            return false;
        }
        return true;
    }
}
