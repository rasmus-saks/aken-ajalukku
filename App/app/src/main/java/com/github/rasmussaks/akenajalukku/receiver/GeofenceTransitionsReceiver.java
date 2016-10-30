package com.github.rasmussaks.akenajalukku.receiver;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.github.rasmussaks.akenajalukku.R;
import com.github.rasmussaks.akenajalukku.activity.MapActivity;
import com.github.rasmussaks.akenajalukku.model.Data;
import com.github.rasmussaks.akenajalukku.model.PointOfInterest;
import com.github.rasmussaks.akenajalukku.util.Constants;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;

import java.util.ArrayList;
import java.util.List;

public class GeofenceTransitionsReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        GeofencingEvent event = GeofencingEvent.fromIntent(intent);
        if (event.getGeofenceTransition() == Geofence.GEOFENCE_TRANSITION_ENTER)
            Log.d("aken-ajalukku", "Entered geofence!");
        else
            Log.d("aken-ajalukku", "Exited geofence!");
        if (event.getGeofenceTransition() == Geofence.GEOFENCE_TRANSITION_ENTER) {
            List<Geofence> geofences = event.getTriggeringGeofences();
            ArrayList<PointOfInterest> pois = new ArrayList<>();
            for (Geofence gf : geofences) {
                pois.add(Data.instance.getPoiById(Integer.parseInt(gf.getRequestId())));
            }
            createNotification(context, pois);
        }
    }

    private void createNotification(Context context, ArrayList<PointOfInterest> pois) {
        if (MapActivity.isVisible()) return;
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);

        String title;
        if (pois.size() == 1) {
            title = String.format(context.getString(R.string.notification_poi_nearby), pois.get(0).getTitle());
        } else {
            title = String.format(context.getString(R.string.notification_pois_nearby), pois.size());
        }
        String detail = context.getString(R.string.notification_detail);

        Intent intent = new Intent(context, MapActivity.class);
        intent.putParcelableArrayListExtra("pois", pois);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(pendingIntent);

        builder.setContentTitle(title);
        builder.setContentText(detail);
        builder.setSmallIcon(R.mipmap.ic_launcher);

        NotificationManager notifMgr =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notifMgr.notify(Constants.NOTIFICATION_ID, builder.build());
    }
}
