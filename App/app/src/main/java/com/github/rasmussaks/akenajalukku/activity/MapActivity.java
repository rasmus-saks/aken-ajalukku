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
import com.github.rasmussaks.akenajalukku.util.DataFetchListener;
import com.github.rasmussaks.akenajalukku.util.DataFetcherTask;
import com.google.android.gms.maps.model.Marker;

import org.json.JSONException;

public class MapActivity extends AbstractMapActivity implements DataFetchListener {

    private GeofenceManager geofenceManager;
    private SharedPreferenceChangeListener preferenceChangeListener = new SharedPreferenceChangeListener();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        NotificationManager notifMgr =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notifMgr.cancel(Constants.NOTIFICATION_ID);
        geofenceManager = new GeofenceManager(this);
        SharedPreferences pref = getSharedPreferences(Constants.SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);
        String cached = pref.getString(Constants.CACHED_DATA_KEY, null);
        if (cached != null) { //Load cached if we have cached data
            try {
                Data.instance = new Data(cached);
                Log.d(Constants.TAG, "Loaded cached data");
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        }
        new DataFetcherTask().execute(this);
    }

    @Override
    public void setContentView() {
        setContentView(R.layout.activity_map);
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
    protected void onResume() {
        super.onResume();
        if (getMap() != null) setupMap();
    }


    @Override
    public boolean onMarkerClick(Marker marker) {
        for (PointOfInterest poi : getPois()) {
            if (marker.equals(poi.getMarker())) {
                openPoiDetailDrawer(poi);
                return true;
            }
        }
        return true;
    }

    @Override
    public void onDataFetched(String data) {
        SharedPreferences pref = getSharedPreferences(Constants.SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);
        String cached = pref.getString(Constants.CACHED_DATA_KEY, null);
        if (data != null && data.equals(cached)) {
            Log.d(Constants.TAG, "Fetched data is same as loaded data");
            return;
        }
        if (getMap() != null) { //Re-setup the map if it has already been set up
            try {
                Data.instance = new Data(data);
                setupMap();
                pref.edit().putString(Constants.CACHED_DATA_KEY, data).apply();
                Log.d(Constants.TAG, "Updated cached data");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
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