package com.sourcey.materiallogindemo;

import android.app.Application;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.util.Base64;
import android.util.Log;

import com.sourcey.materiallogindemo.Utils.ParseConstants;
import com.facebook.FacebookSdk;
import com.parse.Parse;
import com.parse.ParseFacebookUtils;
import com.parse.ParseInstallation;
import com.parse.ParseUser;

import com.facebook.appevents.AppEventsLogger;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


/**
 * Created by GuillermoArturo on 20/04/2016.
 */
public class DiggyDogApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        Parse.enableLocalDatastore(this);

        Parse.initialize(new Parse.Configuration.Builder(this).applicationId("SFWajSIP4EtrmT2FwZP1zmLX74qfsBYY4tCcFIEJ").clientKey("UtDxn9R2NqvRXPVNkxinq26a1eVFVgMGq0voR5yW").server("https://parseapi.back4app.com").build());
        ParseInstallation.getCurrentInstallation().saveInBackground();

        ParseFacebookUtils.initialize(this);
        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);

    }

    public static void updateParseInstallation(ParseUser user) {
        ParseInstallation installation = ParseInstallation.getCurrentInstallation();
        installation.put(ParseConstants.KEY_USER_ID, user.getObjectId());
    }
}
