package com.github.rasmussaks.akenajalukku.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.github.rasmussaks.akenajalukku.R;
import com.github.rasmussaks.akenajalukku.fragment.DrawerFragment;
import com.github.rasmussaks.akenajalukku.fragment.JourneySelectionDrawerFragment;
import com.github.rasmussaks.akenajalukku.fragment.POIDrawerFragment;
import com.github.rasmussaks.akenajalukku.layout.NoTouchSlidingUpPanelLayout;
import com.github.rasmussaks.akenajalukku.model.Data;
import com.github.rasmussaks.akenajalukku.model.PointOfInterest;
import com.github.rasmussaks.akenajalukku.util.DirectionsTaskListener;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import java.util.ArrayList;
import java.util.List;

import static com.github.rasmussaks.akenajalukku.util.Constants.TAG;

public abstract class AbstractMapActivity extends AppCompatActivity implements LocationListener,
        OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, DrawerFragment.DrawerFragmentListener,
        GoogleMap.OnMarkerClickListener, DirectionsTaskListener {

    private static final int REQUEST_TO_SETUP_MAP = 1;
    private static boolean visible;
    private boolean locationEnabled = false;
    private GoogleMap map;
    private GoogleApiClient googleApiClient;
    private ArrayList<PointOfInterest> intentPois;
    private Location lastLocation;
    private Polyline currentPolyline;
    private SlidingUpPanelLayout drawerLayout;

    public static boolean isVisible() {
        return visible;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView();
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("pois")) {
            intentPois = intent.getParcelableArrayListExtra("pois");
        }

        Fragment drawerFragment = getSupportFragmentManager().findFragmentById(R.id.drawer_container);
        if (drawerFragment != null) {
            ((DrawerFragment) drawerFragment).setDrawerFragmentListener(this);
        }

        drawerLayout = (NoTouchSlidingUpPanelLayout) findViewById(R.id.sliding_layout);
        drawerLayout.setPanelHeight(0);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_TO_SETUP_MAP);
        } else {
            locationEnabled = true;
            loadMap();
        }
    }

    public abstract void setContentView();

    @Override
    protected void onResume() {
        super.onResume();
        visible = true;
        Log.d(TAG, "Resumed");
    }

    @Override
    protected void onPause() {
        super.onPause();
        visible = false;
        Log.d(TAG, "Paused");
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (googleApiClient != null) {
            googleApiClient.connect();
        }
    }

    public boolean isLocationEnabled() {
        return locationEnabled;
    }

    public SlidingUpPanelLayout getDrawerLayout() {
        return drawerLayout;
    }

    public GoogleApiClient getGoogleApiClient() {
        return googleApiClient;
    }

    private void loadMap() {
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        map.setOnMarkerClickListener(this);
        Log.i(TAG, "Map is ready");
        setupMap();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_TO_SETUP_MAP:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    locationEnabled = true;
                }
                loadMap();
                break;
        }
    }

    public void setCurrentPoi(PointOfInterest poi) {
        highlightPoiMarker(poi);
        showPoi(poi);
    }

    @SuppressWarnings("MissingPermission")
    private void setupMap() {
        if (locationEnabled) map.setMyLocationEnabled(true);
        map.setPadding(0, getResources().getDimensionPixelSize(R.dimen.mapview_top_padding), 0, 0);
        map.getUiSettings().setMyLocationButtonEnabled(false);
        for (PointOfInterest poi : Data.instance.getPois()) {
            resetPoiMarker(poi);
        }
        //If location is enabled, start the Google API client
        if (googleApiClient == null && locationEnabled) {
            googleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
            googleApiClient.connect();
        }
        map.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
            @Override
            public void onMapLoaded() {
                AbstractMapActivity.this.onMapLoaded();
            }
        });
    }

    @SuppressWarnings("MissingPermission")
    public void resetCamera(boolean animate) {
        CameraUpdate update = null;
        if (map != null) {
            LatLngBounds.Builder bounds = LatLngBounds.builder();
            ArrayList<PointOfInterest> pois = Data.instance.getPois();

            //Show the PoIs the intent wanted to show us
            if (intentPois != null) {
                pois = intentPois;
                intentPois = null;
                if (pois.size() == 1) {
                    setCurrentPoi(Data.instance.getPoiById(pois.get(0).getId()));
                }
            }
            for (PointOfInterest poi : pois) {
                bounds.include(poi.getLocation());
            }
            if (lastLocation != null) {
                bounds.include(new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude()));
            }
            update = CameraUpdateFactory.newLatLngBounds(bounds.build(), 200);

        }
        if (update != null) {
            if (animate) {
                map.animateCamera(update);
            } else {
                map.moveCamera(update);
            }
        }
    }

    public Location getLastLocation() {
        return lastLocation;
    }

    public void removePolyline() {
        if (currentPolyline != null) {
            currentPolyline.remove();
            currentPolyline = null;
        }
    }

    public void showPoi(PointOfInterest poi) {
        if (poi == null) resetCamera(true);
        else {
            map.animateCamera(CameraUpdateFactory.newLatLngZoom(poi.getLocation(), 15.5f));
        }
    }

    protected LocationRequest createLocationRequest() {
        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(10000);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        return mLocationRequest;
    }

    @SuppressWarnings("MissingPermission")
    private void setupGoogleApiClient() {
        LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, createLocationRequest(), this);
        lastLocation = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);

    }

    public void highlightPoiMarker(PointOfInterest poi) {
        if (poi != null) {
            if (poi.getMarker() != null) poi.getMarker().remove();
            poi.setMarker(map.addMarker(poi.getMarkerOptions().icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE))));
        }
    }

    public void resetPoiMarker(PointOfInterest poi) {
        if (poi != null) {
            if (poi.getMarker() != null) poi.getMarker().remove();
            poi.setMarker(map.addMarker(poi.getMarkerOptions()));
        }
    }

    @Override
    public void onDirectionsPolyline(PolylineOptions polylineOptions) {
        if (currentPolyline != null) {
            currentPolyline.remove();
        }
        Log.v(TAG, "Got directions");
        if (polylineOptions != null) {
            List<LatLng> points = polylineOptions.getPoints();
            LatLngBounds.Builder bnds = LatLngBounds.builder().include(new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude()));
            for (LatLng point : points) {
                bnds.include(point);
            }
            map.animateCamera(CameraUpdateFactory.newLatLngBounds(bnds.build(), 200));
            currentPolyline = map.addPolyline(polylineOptions);
        }
    }

    public void openPoiDetailDrawer(PointOfInterest poi) {
        POIDrawerFragment fragment = new POIDrawerFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable("poi", poi);
        fragment.setDrawerFragmentListener(this);
        fragment.setArguments(bundle);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.drawer_container, fragment);
        transaction.commit();
        drawerLayout.setPanelState(SlidingUpPanelLayout.PanelState.EXPANDED);
    }

    public void openJourneySelectionDrawer() {
        JourneySelectionDrawerFragment fragment = new JourneySelectionDrawerFragment();
        fragment.setDrawerFragmentListener(this);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.drawer_container, fragment);
        transaction.commit();
        drawerLayout.setPanelState(SlidingUpPanelLayout.PanelState.EXPANDED);
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        setupGoogleApiClient();
        updateMap();
        Log.d(TAG, "Registered location updates listener");
    }

    public void updateMap() {
        resetCamera(false);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    public void onMapLoaded() {
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onCloseDrawer() {
        drawerLayout.setPanelState(SlidingUpPanelLayout.PanelState.HIDDEN);
    }

    @Override
    public void onLocationChanged(Location location) {
        lastLocation = location;
        Log.v(TAG, "Location changed");
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        return true;
    }

    public void onVideoImageClick(View view) {
        POIDrawerFragment fragment = (POIDrawerFragment) getSupportFragmentManager().findFragmentById(R.id.drawer_container);
        Intent intent = new Intent(this, VideoPlayerActivity.class);
        intent.putExtra("url", fragment.getPoi().getVideoUrl());
        intent.putExtra("title", fragment.getPoi().getTitle());
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.getPanelState() == SlidingUpPanelLayout.PanelState.EXPANDED) {
            onCloseDrawer();
        } else {
            finish();
        }
    }
}
