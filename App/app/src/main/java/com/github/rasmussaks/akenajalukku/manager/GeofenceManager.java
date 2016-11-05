package com.github.rasmussaks.akenajalukku.manager;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.util.Log;

import com.github.rasmussaks.akenajalukku.activity.MapActivity;
import com.github.rasmussaks.akenajalukku.model.Data;
import com.github.rasmussaks.akenajalukku.model.PointOfInterest;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;

import java.util.ArrayList;
import java.util.List;

import static com.github.rasmussaks.akenajalukku.util.Constants.GEOFENCES_ADDED_KEY;
import static com.github.rasmussaks.akenajalukku.util.Constants.GEOFENCE_EXPIRATION_IN_MILLISECONDS;
import static com.github.rasmussaks.akenajalukku.util.Constants.GEOFENCE_RADIUS_IN_METERS;
import static com.github.rasmussaks.akenajalukku.util.Constants.GEOFENCE_RESPONSIVENESS_IN_MILLISECONDS;
import static com.github.rasmussaks.akenajalukku.util.Constants.SHARED_PREFERENCES_NAME;
import static com.github.rasmussaks.akenajalukku.util.Constants.TAG;

public class GeofenceManager extends BaseManager implements ResultCallback<Status> {
    private final SharedPreferences sharedPreferences;
    private boolean geofencesAdded;
    private List<Geofence> geofences = new ArrayList<>();
    private PendingIntent pendingIntent;

    public GeofenceManager(MapActivity context) {
        super(context);
        sharedPreferences = context.getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);
        geofencesAdded = sharedPreferences.getBoolean(GEOFENCES_ADDED_KEY, false);

        populateGeofencesList();
    }

    private void populateGeofencesList() {
        geofences = new ArrayList<>();
        for (PointOfInterest poi : Data.instance.getPois()) {
            geofences.add(
                    new Geofence.Builder()
                            .setRequestId(String.valueOf(poi.getId()))
                            .setCircularRegion(poi.getLocation().latitude,
                                    poi.getLocation().longitude,
                                    GEOFENCE_RADIUS_IN_METERS)
                            .setExpirationDuration(GEOFENCE_EXPIRATION_IN_MILLISECONDS)
                            .setNotificationResponsiveness(GEOFENCE_RESPONSIVENESS_IN_MILLISECONDS)
                            .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER | Geofence.GEOFENCE_TRANSITION_EXIT)
                            .build()
            );
        }
    }

    @SuppressWarnings("MissingPermission")
    public void addGeofences() {
        populateGeofencesList();

        LocationServices.GeofencingApi.addGeofences(
                getContext().getGoogleApiClient(),
                getGeofencingRequest(),
                getPendingIntent()
        ).setResultCallback(new ResultCallback<Status>() {
            @Override
            public void onResult(@NonNull Status status) {
                if (status.isSuccess()) {
                    geofencesAdded = true;
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putBoolean(GEOFENCES_ADDED_KEY, geofencesAdded);
                    editor.apply();
                    Log.i(TAG, "Added " + geofences.size() + " geofence(s)");
                } else {
                    Log.e(TAG, "Failed to add geofences: " + status.getStatusCode());
                }
            }
        });
    }

    public void removeGeofences() {
        final List<String> fences = new ArrayList<>();
        for (PointOfInterest poi : Data.instance.getPois()) {
            fences.add(String.valueOf(poi.getId()));
        }
        LocationServices.GeofencingApi.removeGeofences(
                getContext().getGoogleApiClient(),
                fences
        ).setResultCallback(new ResultCallback<Status>() {
            @Override
            public void onResult(@NonNull Status status) {
                if (status.isSuccess()) {
                    geofencesAdded = false;
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putBoolean(GEOFENCES_ADDED_KEY, geofencesAdded);
                    editor.apply();
                    Log.i(TAG, "Removed " + fences.size() + " geofence(s)");
                } else {
                    Log.e(TAG, "Failed to remove geofences: " + status.getStatusCode());
                }
            }
        });
    }

    private PendingIntent getPendingIntent() {
        if (pendingIntent != null) return pendingIntent;
        Intent intent = new Intent("com.aol.android.geofence.ACTION_RECEIVE_GEOFENCE");
        pendingIntent = PendingIntent.getBroadcast(getContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        return pendingIntent;
    }

    private GeofencingRequest getGeofencingRequest() {
        GeofencingRequest.Builder builder = new GeofencingRequest.Builder();
        builder.setInitialTrigger(Geofence.GEOFENCE_TRANSITION_ENTER);
        builder.addGeofences(geofences);
        return builder.build();
    }

    @Override
    public void onResult(@NonNull Status status) {

    }
}
