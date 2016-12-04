package com.github.rasmussaks.akenajalukku.util;

import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.github.rasmussaks.akenajalukku.R;
import com.github.rasmussaks.akenajalukku.activity.MapActivity;
import com.github.rasmussaks.akenajalukku.model.Journey;

import java.util.ArrayList;


public class JourneyAdapter extends BaseAdapter {
    private ArrayList<Journey> journeys;
    private LayoutInflater inflater;
    private MapActivity context;
    private String locale;

    public JourneyAdapter(ArrayList<Journey> journeys, LayoutInflater inflater, MapActivity context, String locale) {
        this.journeys = journeys;
        this.inflater = inflater;
        this.context = context;
        this.locale = locale;
    }


    @Override
    public int getCount() {
        return journeys.size();
    }

    @Override
    public Object getItem(int position) {
        return journeys.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        final Journey journey = (Journey) getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.journey_item, parent, false);
        }
        // Lookup view for data population
        TextView journeyTitle = (TextView) convertView.findViewById(R.id.journeyTitle);
        // Populate the data into the template view using the data object
        journeyTitle.setText(journey.getTitle(locale));
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                context.onJourneyDetailSelect(journey.getId());
            }
        });

        TextView distance = (TextView) convertView.findViewById(R.id.journeyDistance);
        distance.setText(journey.getFriendlyDistance());
        // Return the completed view to render on screen
        return convertView;

    }
}
