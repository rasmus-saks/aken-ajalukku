package com.github.rasmussaks.akenajalukku.model;


import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

public class Journey implements Parcelable{
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
    private String title;
    private String description;
    private boolean initialized;

    public Journey(int id, List<Integer> poiIdList, String title, String description, List<PointOfInterest> allpoi) {
        this.id = id;
        this.poiIdList = poiIdList;
        this.title = title;
        this.description = description;
        initialize(allpoi);
    }

    protected Journey(Parcel in) {
        id = in.readInt();
        title = in.readString();
        description = in.readString();
        in.readList(poiIdList,null);
        initialized=false;
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

    public void setPoiList(List<PointOfInterest> poiList) {
        this.poiList = poiList;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<Integer> getPoiIdList() {
        return poiIdList;
    }

    public void setPoiIdList(List<Integer> poiIdList) {
        this.poiIdList = poiIdList;
    }

    public boolean isInitialized() {
        return initialized;
    }

    public void initialize(List<PointOfInterest> allpoi){
        poiList = new ArrayList<>();
        for(PointOfInterest poi: allpoi){
            if(poiIdList.contains(poi.getId())){
                poiList.add(poi);
            }
        }
        initialized=true;
    }

    public PointOfInterest getFirstPoi(){
        return poiList.get(0);
    }

    @Override
    public String toString() {
        return "Journey{" +
                "title='" + title + '\'' +
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
        dest.writeString(title);
        dest.writeString(description);
        dest.writeList(poiIdList);
    }


}
