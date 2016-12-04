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
import com.github.rasmussaks.akenajalukku.activity.AbstractMapActivity;
import com.github.rasmussaks.akenajalukku.model.PointOfInterest;

public class POIDrawerFragment extends DrawerFragment implements View.OnClickListener {

    private PointOfInterest poi;
    private TextView title;
    private ImageView img;
    private TextView description;
    private ImageButton downloadButton;
    private TextView downloadText;
    private boolean isClose;
    private boolean hasNext;
    private boolean journey;
    private View playButton;
    private String nextTitle;

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
            journey = arguments.getBoolean("journey");
            hasNext = arguments.getBoolean("hasNext");
            nextTitle = arguments.getString("nextTitle");
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
        title.setText(poi.getTitle(((AbstractMapActivity) getActivity()).getLocale()));
        description.setText(poi.getDescription(((AbstractMapActivity) getActivity()).getLocale()));
        ImageButton close = (ImageButton) view.findViewById(R.id.close_button);
        close.setOnClickListener(this);
        downloadButton = (ImageButton) view.findViewById(R.id.download_button);
        downloadButton.setOnClickListener(this);
        downloadText = (TextView) view.findViewById(R.id.download_text);
        playButton = view.findViewById(R.id.playButton);
        Glide.with(this).load(poi.getImageUrl()).centerCrop().into(img);
        if (journey) {
            playButton.setVisibility(View.VISIBLE);
            view.findViewById(R.id.directionsText).setVisibility(View.GONE);
        } else {
            if (isClose) {
                playButton.setVisibility(View.VISIBLE);
                view.findViewById(R.id.directionsText).setVisibility(View.GONE);
            } else {
                playButton.setVisibility(View.GONE);
                view.findViewById(R.id.directionsText).setVisibility(View.VISIBLE);
            }
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

    public void onVideoPlayerResult() {
        if (getView() == null || !journey) return;
        playButton.setVisibility(View.GONE);
        if (hasNext) {
            getView().findViewById(R.id.journey_next).setVisibility(View.VISIBLE);
            ((TextView) getView().findViewById(R.id.journey_next_title)).setText(nextTitle);
        } else {
            getView().findViewById(R.id.journey_end).setVisibility(View.VISIBLE);
        }
    }
}
