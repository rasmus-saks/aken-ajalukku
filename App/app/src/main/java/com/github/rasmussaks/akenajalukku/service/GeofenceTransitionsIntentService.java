package com.github.rasmussaks.akenajalukku.service;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;

public class GeofenceTransitionsIntentService extends IntentService {

    public GeofenceTransitionsIntentService() {
        super("GeofenceTransitionsIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        GeofencingEvent event = GeofencingEvent.fromIntent(intent);
        if (event.getGeofenceTransition() == Geofence.GEOFENCE_TRANSITION_ENTER)
            Log.d("aken-ajalukku", "Entered geofence!");
        else
            Log.d("aken-ajalukku", "Exited geofence!");
        Log.d("aken-ajalukku", String.valueOf(event.getTriggeringGeofences()));
    }
}
