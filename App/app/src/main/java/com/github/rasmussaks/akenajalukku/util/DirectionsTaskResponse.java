package com.github.rasmussaks.akenajalukku.util;

import android.graphics.Color;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.PolyUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class DirectionsTaskResponse {
    private final PolylineOptions polylineOptions;
    private final String distanceString;
    private final int distance;

    public DirectionsTaskResponse(JSONObject jsonObject) throws JSONException {
        JSONObject route = jsonObject.getJSONArray("routes").getJSONObject(0);
        JSONObject leg = route.getJSONArray("legs").getJSONObject(0);
        List<LatLng> polylinePoints =
                PolyUtil.decode(route.getJSONObject("overview_polyline").getString("points"));
        polylineOptions = new PolylineOptions().addAll(polylinePoints).width(5f).color(Color.RED);
        distanceString = leg.getJSONObject("distance").getString("text");
        distance = leg.getJSONObject("distance").getInt("value");
    }

    public PolylineOptions getPolylineOptions() {
        return polylineOptions;
    }

    public String getDistanceString() {
        return distanceString;
    }

    public int getDistance() {
        return distance;
    }
}
