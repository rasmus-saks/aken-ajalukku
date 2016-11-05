package com.github.rasmussaks.akenajalukku.util;

import com.google.android.gms.maps.model.PolylineOptions;

public interface DirectionsTaskListener {
    void onDirectionsPolyline(PolylineOptions polylineOptions);
}
