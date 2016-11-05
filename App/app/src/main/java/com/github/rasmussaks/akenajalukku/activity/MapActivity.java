package com.github.rasmussaks.akenajalukku.activity;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.view.View;

import com.github.rasmussaks.akenajalukku.R;
import com.github.rasmussaks.akenajalukku.manager.GeofenceManager;
import com.github.rasmussaks.akenajalukku.model.Data;
import com.github.rasmussaks.akenajalukku.model.PointOfInterest;
import com.github.rasmussaks.akenajalukku.util.Constants;
import com.github.rasmussaks.akenajalukku.util.DirectionsTask;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.PolylineOptions;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import static com.github.rasmussaks.akenajalukku.util.Constants.TAG;

public class MapActivity extends AbstractMapActivity {

    private PointOfInterest currentPoi;
    private GeofenceManager geofenceManager;
    private SharedPreferenceChangeListener preferenceChangeListener = new SharedPreferenceChangeListener();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBar bar = getSupportActionBar();
        if (bar != null) bar.hide();

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
        Data.instance = bundle.getParcelable("data");
        int curIdx = bundle.getInt("currentPoi");
        if (curIdx != -1) {
            currentPoi = Data.instance.getPois().get(curIdx);
        }
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable("data", Data.instance);
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

    public void onSettingsButtonClick(View view) {
        openSettings();
    }

    public void onJourneyButtonClick(View view) {
        Log.i(TAG, Data.instance.getJourneys().toString());
        openJourneySelectionDrawer();
    }

    private void openSettings() {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }

    @Override
    public void onDirectionsPolyline(PolylineOptions polylineOptions) {
        super.onDirectionsPolyline(polylineOptions);
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
            highlightPoiMarker(currentPoi);
        }
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        for (PointOfInterest poi : Data.instance.getPois()) {
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