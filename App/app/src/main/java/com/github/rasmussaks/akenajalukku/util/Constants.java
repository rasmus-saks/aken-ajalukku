package com.github.rasmussaks.akenajalukku.util;

public class Constants {
    public static final String PACKAGE_NAME = "com.github.rasmussaks.akenajalukku";
    public static final String TAG = "aken-ajalukku";

    public static final String SHARED_PREFERENCES_NAME = PACKAGE_NAME + ".SHARED_PREFERENCES_NAME";
    public static final String GEOFENCES_ADDED_KEY = PACKAGE_NAME + ".GEOFENCES_ADDED_KEY";
    public static final String CACHED_DATA_KEY = PACKAGE_NAME + ".CACHED_DATA_KEY";

    public static final long GEOFENCE_EXPIRATION_IN_HOURS = 12;

    public static final long GEOFENCE_EXPIRATION_IN_MILLISECONDS =
            GEOFENCE_EXPIRATION_IN_HOURS * 60 * 60 * 1000;
    public static final float GEOFENCE_RADIUS_IN_METERS = 200;

    public static final int GEOFENCE_RESPONSIVENESS_IN_MILLISECONDS = 10000;

    public static final int NOTIFICATION_ID = 1;

    public static final String DATA_URL = "https://s3.eu-central-1.amazonaws.com/aken-ajalukku-media/data.json";
}
