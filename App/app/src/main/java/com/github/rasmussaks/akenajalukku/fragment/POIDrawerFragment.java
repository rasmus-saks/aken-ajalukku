package com.github.rasmussaks.akenajalukku.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.github.rasmussaks.akenajalukku.R;
import com.github.rasmussaks.akenajalukku.model.PointOfInterest;

public class POIDrawerFragment extends Fragment {

    private PointOfInterest poi;
    private TextView title;
    private ImageView img;
    private TextView description;

    public POIDrawerFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            poi = getArguments().getParcelable("poi");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_poidrawer, container, false);
        title = (TextView) view.findViewById(R.id.poi_title);
        img = (ImageView) view.findViewById(R.id.poi_img);
        description = (TextView) view.findViewById(R.id.poi_description);
        title.setText(poi.getTitle());
        description.setText(poi.getDescription());
        Glide.with(this).load(poi.getImageUrl()).centerCrop().into(img);
        return view;
    }

}
