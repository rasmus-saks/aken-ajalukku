package com.github.rasmussaks.akenajalukku.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.github.rasmussaks.akenajalukku.util.Constants;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;


public class Data implements Parcelable {
    public static final Creator<Data> CREATOR = new Creator<Data>() {
        @Override
        public Data createFromParcel(Parcel in) {
            return new Data(in);
        }

        @Override
        public Data[] newArray(int size) {
            return new Data[size];
        }
    };
    public static Data instance = new Data(true);
    private ArrayList<PointOfInterest> pois = new ArrayList<>();
    private ArrayList<Journey> journeys = new ArrayList<>();
    private String json;

    public Data(boolean isDummy) {
        if (isDummy) {
            // Initialize dummy data
            pois.add(new PointOfInterest(1, new LatLng(58.3824298, 26.7145573), "Baeri ja Jakobi ristmik", "Päris põnev", "http://i.imgur.com/FGCgIB7.jpg", "https://s3.eu-central-1.amazonaws.com/aken-ajalukku-media/efa0203_f_vi_03014_k_AVI_Microsoft_DV_PAL.mp4"));
            pois.add(new PointOfInterest(2, new LatLng(58.380144, 26.7223035), "Raekoja plats", "Raekoda on cool", "http://i.imgur.com/ewugjb2.jpg", "https://s3.eu-central-1.amazonaws.com/aken-ajalukku-media/efa0203_f_vi_03014_k_AVI_Microsoft_DV_PAL.mp4"));
            pois.add(new PointOfInterest(3, new LatLng(58.3740385, 26.7071558), "Tartu rongijaam", "Choo choo", "http://i.imgur.com/mRFDWKl.jpg", "https://s3.eu-central-1.amazonaws.com/aken-ajalukku-media/x264_aac_faac.mp4"));
            // Add Journeys associated with newly added pois
            journeys.add(new Journey(1, Arrays.asList(1, 2), "Tartu in the 90's", "I know journeys", pois));
            journeys.add(new Journey(2, Arrays.asList(2, 3), "Tallinn 1993", "I have the best journeys", pois));
            journeys.add(new Journey(3, Arrays.asList(2, 3), "The Baltic Chain", "I have the best journeys", pois));
            journeys.add(new Journey(4, Arrays.asList(2, 3), "Medieval Tallinn", "I have the best journeys", pois));
            journeys.add(new Journey(5, Arrays.asList(2, 3), "Laulev Revolutsioon", "I have the best journeys", pois));
            journeys.add(new Journey(6, Arrays.asList(2, 3), "The Singing Revolution", "I have the best journeys", pois));
            journeys.add(new Journey(7, Arrays.asList(2, 3), "The Singing Revolution", "I have the best journeys", pois));
            journeys.add(new Journey(8, Arrays.asList(2, 3), "The Singing Revolution", "I have the best journeys", pois));
        }

    }


    protected Data(Parcel in) {
        pois = in.createTypedArrayList(PointOfInterest.CREATOR);
        journeys = in.createTypedArrayList(Journey.CREATOR);
        json = in.readString();
        for (Journey journey : journeys) {
            journey.initialize(pois);
        }
    }

    public Data(String json) throws JSONException {
        Log.d(Constants.TAG, "Loading data from json");
        Log.d(Constants.TAG, json);
        JSONObject root = new JSONObject(json);
        JSONArray pois = root.getJSONArray("pois");
        for (int i = 0; i < pois.length(); i++) {
            JSONObject poi = (JSONObject) pois.get(i);
            this.pois.add(new PointOfInterest(poi));
        }
        JSONArray journeys = root.getJSONArray("journeys");
        for (int i = 0; i < journeys.length(); i++) {
            JSONObject journey = (JSONObject) journeys.get(i);
            this.journeys.add(new Journey(journey, this.pois));
        }
        this.json = json;
    }


    public ArrayList<PointOfInterest> getPois() {
        return pois;
    }

    public void setPois(ArrayList<PointOfInterest> pois) {
        this.pois = pois;
    }

    public ArrayList<Journey> getJourneys() {
        return journeys;
    }

    public void setJourneys(ArrayList<Journey> journeys) {
        this.journeys = journeys;
    }

    public PointOfInterest getPoiById(int id) {
        for (PointOfInterest poi : pois) {
            if (poi.getId() == id) return poi;
        }
        return null;
    }

    public Journey getJourneyById(int id) {
        for (Journey journey : journeys) {
            if (journey.getId() == id) return journey;
        }
        return null;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedList(pois);
        dest.writeTypedList(journeys);
        dest.writeString(json);
    }

    public String getJson() {
        return json;
    }
}
