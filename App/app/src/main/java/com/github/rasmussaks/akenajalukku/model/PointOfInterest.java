package com.github.rasmussaks.akenajalukku.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.github.rasmussaks.akenajalukku.R;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class PointOfInterest implements Parcelable {
    public static final Creator<PointOfInterest> CREATOR = new Creator<PointOfInterest>() {
        @Override
        public PointOfInterest createFromParcel(Parcel in) {
            return new PointOfInterest(in);
        }

        @Override
        public PointOfInterest[] newArray(int size) {
            return new PointOfInterest[size];
        }
    };
    private int id;
    private LatLng location;
    private Map<String, String> titles;
    private Map<String, String> descriptions;
    private Marker marker;
    private String imageUrl;
    private String videoUrl;

    public PointOfInterest(int id, LatLng location,
                           String title, String description, String imageUrl) {
        this(id, location, title, description, imageUrl, null);
    }

    public PointOfInterest(int id, LatLng location,
                           String title, String description, String imageUrl, String videoUrl) {
        this.id = id;
        this.location = location;
        titles = new HashMap<>();
        descriptions = new HashMap<>();
        this.titles.put("EN", title);
        this.descriptions.put("EN", description);
        this.imageUrl = imageUrl;
        this.videoUrl = videoUrl;
    }

    private PointOfInterest(Parcel in) {
        id = in.readInt();
        location = in.readParcelable(LatLng.class.getClassLoader());
        titles = new HashMap<>();
        descriptions = new HashMap<>();
        in.readMap(titles, null);
        in.readMap(descriptions, null);
        imageUrl = in.readString();
        videoUrl = in.readString();
    }

    public PointOfInterest(JSONObject root) throws JSONException {
        id = root.getInt("id");
        titles = new HashMap<>();
        JSONObject title = root.getJSONObject("title");
        Iterator<String> titleKeys = title.keys();
        while (titleKeys.hasNext()) {
            String key = titleKeys.next();
            titles.put(key, title.getString(key));
        }
        descriptions = new HashMap<>();
        JSONObject desc = root.getJSONObject("description");
        Iterator<String> descKeys = desc.keys();
        while (descKeys.hasNext()) {
            String key = descKeys.next();
            descriptions.put(key, desc.getString(key));
        }
        this.location = new LatLng(root.getDouble("lat"), root.getDouble("lon"));
        videoUrl = root.getString("video");
        imageUrl = root.getString("img");
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public LatLng getLocation() {
        return location;
    }


    public String getTitle(String locale) {
        if (locale == null || !titles.containsKey(locale)) {
            return titles.get("EN");
        }
        return titles.get(locale);
    }


    public String getDescription(String locale) {
        if (locale == null || !descriptions.containsKey(locale)) {
            return descriptions.get("EN");
        }
        return descriptions.get(locale);
    }


    public String getImageUrl() {
        return imageUrl;
    }

    public Marker getMarker() {
        return marker;
    }

    public void setMarker(Marker marker) {
        this.marker = marker;
    }

    public MarkerOptions getMarkerOptions() {
        Log.d("aken-ajalukku", "New marker for " + getTitle(null));
        return new MarkerOptions().title(getTitle(null)).position(location).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_mapmarker_walk));
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeParcelable(location, flags);
        dest.writeMap(titles);
        dest.writeMap(descriptions);
        dest.writeString(imageUrl);
        dest.writeString(videoUrl);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PointOfInterest that = (PointOfInterest) o;

        return id == that.id;

    }

    @Override
    public int hashCode() {
        return id;
    }
}
