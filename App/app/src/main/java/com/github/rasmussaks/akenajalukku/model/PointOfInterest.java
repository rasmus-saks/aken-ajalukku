package com.github.rasmussaks.akenajalukku.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

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
    private String title;
    private String description;
    private Marker marker;
    private String imageUrl;

    public PointOfInterest(int id,LatLng location, String title, String description, String imageUrl) {
        this.id=id;
        this.location = location;
        this.title = title;
        this.description = description;
        this.imageUrl = imageUrl;
    }

    private PointOfInterest(Parcel in) {
        id = in.readInt();
        location = in.readParcelable(LatLng.class.getClassLoader());
        title = in.readString();
        description = in.readString();
        imageUrl = in.readString();
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

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
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
        return new MarkerOptions().title(title).position(location);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeParcelable(location, flags);
        dest.writeString(title);
        dest.writeString(description);
        dest.writeString(imageUrl);
    }
}
