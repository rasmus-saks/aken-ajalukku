package com.github.rasmussaks.akenajalukku.activity;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.util.Log;

import com.github.rasmussaks.akenajalukku.R;
import com.github.rasmussaks.akenajalukku.fragment.JourneyFragment;
import com.github.rasmussaks.akenajalukku.manager.GeofenceManager;
import com.github.rasmussaks.akenajalukku.model.Data;
import com.github.rasmussaks.akenajalukku.model.PointOfInterest;
import com.github.rasmussaks.akenajalukku.util.Constants;
import com.github.rasmussaks.akenajalukku.util.DirectionsTask;
import com.github.rasmussaks.akenajalukku.util.DirectionsTaskResponse;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import static com.github.rasmussaks.akenajalukku.util.Constants.TAG;

public class MapActivity extends AbstractMapActivity {

    private PointOfInterest currentPoi;
    private GeofenceManager geofenceManager;
    private SharedPreferenceChangeListener preferenceChangeListener = new SharedPreferenceChangeListener();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null) {
            unbundle(savedInstanceState);
        }

        NotificationManager notifMgr =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notifMgr.cancel(Constants.NOTIFICATION_ID);
        geofenceManager = new GeofenceManager(this);

    }

    @Override
    public void setContentView() {
        setContentView(R.layout.activity_map);
    }


    public void unbundle(Bundle bundle) {
        int curIdx = bundle.getInt("currentPoi");
        if (curIdx != -1) {
            currentPoi = Data.instance.getPois().get(curIdx);
        }
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("currentPoi", Data.instance.getPois().indexOf(currentPoi));
    }


    @Override
    public void onMapLoaded() {
        if (currentPoi == null) {
            if (!isLocationEnabled()) updateMap();
        } else {
            setCurrentPoi(currentPoi);
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        super.onConnected(bundle);
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
        pref.registerOnSharedPreferenceChangeListener(preferenceChangeListener);
        if (pref.getBoolean("pref_notifications", true)) {
            geofenceManager.addGeofences();
        }
    }


    public void onJourneyDetailSelect(int journeyId) {
        openJourneyDetailDrawer(journeyId, JourneyFragment.START);
    }

    @Override
    public void onJourneyDetailButtonClick(int journeyId) {
        Intent intent = new Intent(this, JourneyActivity.class);
        intent.putExtra("journey", journeyId);
        startActivity(intent);
    }


    @Override
    public void onDirectionsTaskResponse(DirectionsTaskResponse response) {
        super.onDirectionsTaskResponse(response);
        highlightPoiMarker(currentPoi);
    }

    @Override
    public void onBackPressed() {
        if (getDrawerLayout().getPanelState() == SlidingUpPanelLayout.PanelState.EXPANDED) {
            onCloseDrawer();
        } else if (currentPoi != null) {
            setCurrentPoi(null);
        } else {
            finish();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (getMap() != null) setupMap();
    }

    @Override
    public void setCurrentPoi(PointOfInterest poi) {
        resetPoiMarker(currentPoi);
        currentPoi = poi;
        if (currentPoi == null) {
            removePolyline();
            resetCamera(true);
            return;
        }
        if (getLastLocation() != null) {
            new DirectionsTask(this, this).execute(
                    new LatLng(getLastLocation().getLatitude(), getLastLocation().getLongitude()),
                    poi.getLocation()
            );
        } else { //No location available at the moment, just focus the marker
            if (currentPoi.getMarker() != null) {
                currentPoi.getMarker().remove();
                currentPoi.setMarker(null);
            }
            removePolyline();
            Log.v(TAG, "Setting focused POI");
        }
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        for (PointOfInterest poi : getPois()) {
            if (marker.equals(poi.getMarker())) {
                if (poi.equals(currentPoi)) {
                    openPoiDetailDrawer(poi);
                } else {
                    setCurrentPoi(poi);
                }
                return true;
            }
        }
        return true;
    }

    private class SharedPreferenceChangeListener implements SharedPreferences.OnSharedPreferenceChangeListener {
        @Override
        public void onSharedPreferenceChanged(SharedPreferences pref, String key) {
            if (key.equals("pref_notifications")) {
                if (pref.getBoolean("pref_notifications", true)) {
                    geofenceManager.addGeofences();
                } else {
                    geofenceManager.removeGeofences();
                }
            }
        }
    }
}