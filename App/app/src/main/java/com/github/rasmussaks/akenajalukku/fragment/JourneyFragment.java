package com.github.rasmussaks.akenajalukku.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.github.rasmussaks.akenajalukku.R;
import com.github.rasmussaks.akenajalukku.activity.AbstractMapActivity;
import com.github.rasmussaks.akenajalukku.model.Data;
import com.github.rasmussaks.akenajalukku.model.Journey;
import com.github.rasmussaks.akenajalukku.util.Constants;

import static android.view.View.GONE;


public class JourneyFragment extends DrawerFragment implements View.OnClickListener {
    public static final int START = 1;
    public static final int CANCEL = 2;
    TextView title;
    ImageView img;
    TextView description;
    ImageButton downloadButton;
    TextView downloadText;
    int journeyId;
    Journey journey;
    int state;
    Button journeyButton;
    ImageButton backButton;


    @SuppressLint("SetTextI18n")
    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.close_button) {
            closeDrawer();
        } else if (v.getId() == R.id.download_button) {
            downloadButton.setVisibility(GONE);
            downloadText.setVisibility(View.VISIBLE);
            downloadText.setText(String.format(getString(R.string.download_text), 1) + "%");
        } else if (v.getId() == R.id.back_button) {
            ((AbstractMapActivity)getActivity()).openJourneySelectionDrawer();
        } else if (v.getId() == R.id.journey_button) {
            ((AbstractMapActivity)getActivity()).onJourneyDetailButtonClick(journeyId);
        }
    }

    @SuppressLint("SetTextI18n")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_journeydrawer, container, false);
        if (getArguments() != null) {
            journeyId = getArguments().getInt("journeyId");
            state = getArguments().getInt("state");
        } else {
            Log.i(Constants.TAG, "JourneyFragment loaded without arguments. (unexpected)");
            return view;
        }

        journey = Data.instance.getJourneyById(journeyId);
        title = (TextView) view.findViewById(R.id.journey_title);
        img = (ImageView) view.findViewById(R.id.journey_img);
        description = (TextView) view.findViewById(R.id.journey_description);
        downloadButton = (ImageButton) view.findViewById(R.id.download_button);
        ImageButton close = (ImageButton) view.findViewById(R.id.close_button);
        journeyButton = (Button) view.findViewById(R.id.journey_button);
        backButton = (ImageButton) view.findViewById(R.id.back_button);
        downloadText = (TextView) view.findViewById(R.id.download_text);

        if (state == START) {
            journeyButton.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.colorPrimary));
            journeyButton.setText(R.string.start_journey);
        } else if (state == CANCEL) {
            journeyButton.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.colorAccent));
            journeyButton.setText(R.string.cancel_journey);
            backButton.setVisibility(GONE);
        }

        close.setOnClickListener(this);
        downloadButton.setOnClickListener(this);
        journeyButton.setOnClickListener(this);
        backButton.setOnClickListener(this);


        title.setText(journey.getTitle());
        description.setText(journey.getDescription());
        Glide.with(this).load(journey.getFirstPoi().getImageUrl()).centerCrop().into(img);
        return view;
    }
}
