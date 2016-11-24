package com.github.rasmussaks.akenajalukku.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.github.rasmussaks.akenajalukku.R;
import com.github.rasmussaks.akenajalukku.model.PointOfInterest;

public class POIDrawerFragment extends DrawerFragment implements View.OnClickListener {

    private PointOfInterest poi;
    private TextView title;
    private ImageView img;
    private TextView description;
    private ImageButton downloadButton;
    private TextView downloadText;
    private boolean isClose;

    public POIDrawerFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle arguments = getArguments();
        if (arguments != null) {
            poi = arguments.getParcelable("poi");
            isClose = arguments.getBoolean("close");
        }
    }

    public PointOfInterest getPoi() {
        return poi;
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
        ImageButton close = (ImageButton) view.findViewById(R.id.close_button);
        close.setOnClickListener(this);
        downloadButton = (ImageButton) view.findViewById(R.id.download_button);
        downloadButton.setOnClickListener(this);
        downloadText = (TextView) view.findViewById(R.id.download_text);
        Glide.with(this).load(poi.getImageUrl()).centerCrop().into(img);
        if (isClose) {
            view.findViewById(R.id.playButton).setVisibility(View.VISIBLE);
            view.findViewById(R.id.directionsText).setVisibility(View.GONE);
        } else {
            view.findViewById(R.id.playButton).setVisibility(View.GONE);
            view.findViewById(R.id.directionsText).setVisibility(View.VISIBLE);
        }
        return view;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.close_button) {
            closeDrawer();
        } else if (v.getId() == R.id.download_button) {
            downloadButton.setVisibility(View.GONE);
            downloadText.setVisibility(View.VISIBLE);
            downloadText.setText(String.format(getString(R.string.download_text), 1) + "%");
        }
    }
}
