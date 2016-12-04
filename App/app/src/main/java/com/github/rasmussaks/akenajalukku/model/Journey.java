package com.github.rasmussaks.akenajalukku.model;


import android.annotation.SuppressLint;
import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class Journey implements Parcelable {
    public static final Creator<Journey> CREATOR = new Creator<Journey>() {
        @Override
        public Journey createFromParcel(Parcel in) {
            return new Journey(in);
        }

        @Override
        public Journey[] newArray(int size) {
            return new Journey[size];
        }
    };

    private int id;
    private List<PointOfInterest> poiList;
    private List<Integer> poiIdList;
    private Map<String, String> titles;
    private Map<String, String> descriptions;
    private int distance;

    public Journey(int id, List<Integer> poiIdList, String title, String description, List<PointOfInterest> allPois) {
        this.id = id;
        this.poiIdList = poiIdList;
        titles = new HashMap<>();
        descriptions = new HashMap<>();
        this.titles.put("en", title);
        this.descriptions.put("en", description);
        initialize(allPois);
    }

    protected Journey(Parcel in) {
        id = in.readInt();
        titles = new HashMap<>();
        descriptions = new HashMap<>();
        in.readMap(titles, null);
        in.readMap(descriptions, null);
        poiIdList = new ArrayList<>();
        in.readList(poiIdList, null);
        distance = in.readInt();
    }

    public Journey(JSONObject root, List<PointOfInterest> allPois) throws JSONException {
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
        poiIdList = new ArrayList<>();
        JSONArray pois = root.getJSONArray("pois");
        for (int i = 0; i < pois.length(); i++) {
            poiIdList.add(pois.getInt(i));
        }
        distance = root.getInt("distance");
        initialize(allPois);
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public List<PointOfInterest> getPoiList() {
        return poiList;
    }

    public String getTitle() {
        return getTitle(null);
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


    public List<Integer> getPoiIdList() {
        return poiIdList;
    }

    void initialize(List<PointOfInterest> allPois) {
        poiList = new ArrayList<>();
        for (int poiId : poiIdList) {
            boolean found = false;
            for (PointOfInterest poi : allPois) {
                if (poi.getId() == poiId) {
                    poiList.add(poi);
                    found = true;
                    break;
                }
            }
            if (!found) {
                throw new RuntimeException("PoI not found while initializing journey");
            }
        }

    }

    @SuppressLint("DefaultLocale")
    public String getFriendlyDistance() {
        if (distance < 1000) {
            return String.format("%dm", (distance / 100) * 100);
        }
        return String.format("%.1fkm", (distance / 1000.0));
    }

    public PointOfInterest getFirstPoi() {
        return poiList.get(0);
    }

    @Override
    public String toString() {
        return "Journey{" +
                "title='" + getTitle() + '\'' +
                "lenofpois='" + poiList.size() + '\'' +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeMap(titles);
        dest.writeMap(descriptions);
        dest.writeList(poiIdList);
    }


    public int getDistance() {
        return distance;
    }
}
