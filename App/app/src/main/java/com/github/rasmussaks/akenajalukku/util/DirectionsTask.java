package com.github.rasmussaks.akenajalukku.util;

import android.content.Context;
import android.os.AsyncTask;
import android.text.TextUtils;

import com.github.rasmussaks.akenajalukku.R;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

public class DirectionsTask extends AsyncTask<LatLng, Void, DirectionsTaskResponse> {

    private Context context;
    private DirectionsTaskListener listener;

    public DirectionsTask(Context context, DirectionsTaskListener listener) {
        this.context = context;
        this.listener = listener;
    }

    @Override
    protected DirectionsTaskResponse doInBackground(LatLng... params) {
        try {
            LatLng start = params[0];
            LatLng end = params[params.length - 1];
            String url = "https://maps.googleapis.com/maps/api/directions/json" +
                    "?key=" + context.getString(R.string.google_maps_key)
                    + "&origin=" + start.latitude + "," + start.longitude
                    + "&destination=" + end.latitude + "," + end.longitude
                    + "&units=metric";
            if (params.length > 2) {
                List<String> waypoints = new ArrayList<>();
                for (int i = 1; i < params.length - 1; i++) {
                    LatLng param = params[i];
                    waypoints.add(param.latitude + "," + param.longitude);
                }
                url += "&waypoints=" + TextUtils.join("|", waypoints);
            }
            URLConnection connection =
                    new URL(url).openConnection();
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()), 1024 * 16);
            StringBuilder builder = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {
                builder.append(line).append("\n");
            }
            JSONObject object = new JSONObject(builder.toString());
            return new DirectionsTaskResponse(object);
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(DirectionsTaskResponse polyline) {
        if (listener != null) listener.onDirectionsTaskResponse(polyline);
        super.onPostExecute(polyline);
    }
}
