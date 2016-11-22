package com.github.rasmussaks.akenajalukku.activity;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.github.rasmussaks.akenajalukku.R;
import com.github.rasmussaks.akenajalukku.fragment.JourneyFragment;
import com.github.rasmussaks.akenajalukku.model.Data;
import com.github.rasmussaks.akenajalukku.model.Journey;
import com.github.rasmussaks.akenajalukku.model.PointOfInterest;
import com.github.rasmussaks.akenajalukku.util.Constants;
import com.github.rasmussaks.akenajalukku.util.DirectionsTask;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

import java.util.ArrayList;

import static com.github.rasmussaks.akenajalukku.util.Constants.TAG;

public class JourneyActivity extends AbstractMapActivity {
    private Journey journey;
    private PointOfInterest currentPoi;
    private boolean closeToCurrent;
    private boolean moveToNext;

    @Override
    public void setContentView() {
        setContentView(R.layout.activity_map);
    }

    @Override
    public ArrayList<PointOfInterest> getPois() {
        return (ArrayList<PointOfInterest>) journey.getPoiList();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        if (!intent.hasExtra("journey")) {
            Log.e(Constants.TAG, "No journey given");
            finish();
            return;
        }
        journey = Data.instance.getJourneyById(intent.getIntExtra("journey", -1));
        if (intent.hasExtra("currentPoi")) {
            currentPoi = journey.getPoiList().get(intent.getIntExtra("currentPoi", -1));
        } else {
            currentPoi = journey.getPoiList().get(0);
        }
        ((Button)findViewById(R.id.journey_button)).setText(R.string.ongoing_journey_button);
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
    public void onMapLoaded() {
        setCurrentPoi(currentPoi);
        checkCloseness();
    }

    @Override
    public void onJourneyDetailButtonClick(int journeyId) {
        finish();
    }

    @Override
    public void onJourneyButtonClick(View view) {
        openJourneyDetailDrawer(journey.getId(), JourneyFragment.CANCEL);
    }


    @Override
    public void onLocationChanged(Location location) {
        super.onLocationChanged(location);
        checkCloseness();
        Log.d(TAG, "wtf location changed");
    }

    private void checkCloseness() {
        if (isCloseTo(currentPoi)) {
            highlightPoiMarker(currentPoi);
            closeToCurrent = true;
        } else if (closeToCurrent) {
            resetPoiMarker(currentPoi);
            closeToCurrent = false;
        }
    }

    @Override
    public void onCloseDrawer() {
        super.onCloseDrawer();
        if (moveToNext) {
            moveToNext = false;
            int idx = journey.getPoiList().indexOf(currentPoi);
            if (idx == journey.getPoiList().size() - 1) {
                Toast.makeText(this, R.string.journey_finished, Toast.LENGTH_LONG).show();
                finish();
            } else {
                setCurrentPoi(journey.getPoiList().get(idx + 1));
                checkCloseness();
            }
        }
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        if (closeToCurrent && marker.equals(currentPoi.getMarker())) {
            openPoiDetailDrawer(currentPoi);
            moveToNext = true;
        }
        return true;
    }
}
