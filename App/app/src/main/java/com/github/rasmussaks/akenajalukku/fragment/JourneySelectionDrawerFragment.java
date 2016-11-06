package com.github.rasmussaks.akenajalukku.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ListView;

import com.github.rasmussaks.akenajalukku.R;
import com.github.rasmussaks.akenajalukku.activity.MapActivity;
import com.github.rasmussaks.akenajalukku.model.Data;
import com.github.rasmussaks.akenajalukku.util.JourneyAdapter;


public class JourneySelectionDrawerFragment extends DrawerFragment implements View.OnClickListener {
    JourneyAdapter journeyAdapter;

    public JourneySelectionDrawerFragment() {
        // Required empty constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.close_button) {
            closeDrawer();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_journeyselectiondrawer, container, false);
        ImageButton close = (ImageButton) view.findViewById(R.id.close_button);
        ListView journeyList = (ListView) view.findViewById(R.id.journey_list);
        journeyAdapter = new JourneyAdapter(Data.instance.getJourneys(), inflater, (MapActivity) getActivity());
        journeyList.setAdapter(journeyAdapter);
        close.setOnClickListener(this);
        return view;
    }

}
