package com.github.rasmussaks.akenajalukku.util;

import com.github.rasmussaks.akenajalukku.model.PointOfInterest;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

public class Constants {
    public static final String PACKAGE_NAME = "com.github.rasmussaks.akenajalukku";
    public static final String TAG = "aken-ajalukku";

    public static final String SHARED_PREFERENCES_NAME = PACKAGE_NAME + ".SHARED_PREFERENCES_NAME";
    public static final String GEOFENCES_ADDED_KEY = PACKAGE_NAME + ".GEOFENCES_ADDED_KEY";

    public static final long GEOFENCE_EXPIRATION_IN_HOURS = 12;

    public static final long GEOFENCE_EXPIRATION_IN_MILLISECONDS =
            GEOFENCE_EXPIRATION_IN_HOURS * 60 * 60 * 1000;
    public static final float GEOFENCE_RADIUS_IN_METERS = 200;

    public static final int GEOFENCE_RESPONSIVENESS_IN_MILLISECONDS = 10000;

    public static final List<PointOfInterest> TESTING_POIS = new ArrayList<>();

    static {
        TESTING_POIS.add(new PointOfInterest(new LatLng(58.3824298, 26.7145573), "Baeri ja Jakobi ristmik", "Päris põnev", "http://i.imgur.com/FGCgIB7.jpg"));
        TESTING_POIS.add(new PointOfInterest(new LatLng(58.380144, 26.7223035), "Raekoja plats", "Raekoda on cool", "http://i.imgur.com/ewugjb2.jpg"));
        TESTING_POIS.add(new PointOfInterest(new LatLng(58.3740385, 26.7071558), "Tartu rongijaam", "Choo choo", "http://i.imgur.com/mRFDWKl.jpg"));
    }

}
