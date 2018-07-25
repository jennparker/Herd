package com.booisajerk.vaccinationvisualization;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class FilterActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, OnMapReadyCallback {

    private GoogleMap map;
    private FusedLocationProviderClient mFusedLocationClient;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter);

        //check location permissions
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            Log.d("Parker", "Permissions granted");
        } else {
            Log.d("Parker", "Location not enabled");
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
        }

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        Toolbar toolbar = findViewById(R.id.toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToCurrentLocation();
                Log.d("Parker", "FAB clicked");
            }
        });

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        // See if this is first run (on a new thread)
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                //  Initialize SharedPreferences
                SharedPreferences getPrefs = PreferenceManager
                        .getDefaultSharedPreferences(getBaseContext());

                boolean isFirstStart = getPrefs.getBoolean("firstStart", true);

                if (isFirstStart) {
                    final Intent intent = new Intent(FilterActivity.this, FirstRunActivity.class);

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            startActivity(intent);
                        }
                    });

                    SharedPreferences.Editor e = getPrefs.edit();
                    e.putBoolean("firstStart", false);
                    e.apply();
                }
            }
        });
        t.start();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        addMapMarkers();
        goToCurrentLocation();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.filter, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_home) {

        } else if (id == R.id.nav_alerts) {
            //TODO handle alerts

        } else if (id == R.id.nav_risks) {
            //TODO handle nav risks

        } else if (id == R.id.nav_faq) {
            //TODO handle FAQs

        } else if (id == R.id.nav_get_vac) {
            //TODO handle get vaccinated

        } else if (id == R.id.nav_legend) {
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
            LayoutInflater factory = LayoutInflater.from(FilterActivity.this);
            final View legendView = factory.inflate(R.layout.legend_dialog, null);
            alertDialog.setView(legendView);
            alertDialog.setMessage("Percent coverage by school");
            alertDialog.create();
            alertDialog.show();

        } else if (id == R.id.nav_share) {
            //TODO handle share

        } else if (id == R.id.nav_send) {
            //TODO handle send
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void addMapMarkers() {
        List<School> list = null;

        try {
            list = prepareMarkers(R.raw.immunization);
        } catch (JSONException e) {
            Toast.makeText(this, "Problem reading list of locations.", Toast.LENGTH_LONG).show();
        }

        if (list != null) {
            for (School loc : list) {
                map.addMarker(new MarkerOptions()
                        .position(loc.getLatLng())
                        .title(loc.getName())
                        .icon(BitmapDescriptorFactory.defaultMarker(coverageToColor(loc.getPercentTotal())))
                        .snippet(loc.getPercentTotal() + "% vaccination coverage"));
            }
        }
    }

    @SuppressLint("MissingPermission")
    public void goToCurrentLocation() {
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        Log.d("Parker", "Creating FusedLocationClient");
        mFusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if (location != null) {
                            LatLng currentLocation = new LatLng(location.getLatitude(), location.getLongitude());

                            CameraPosition cameraPosition = new CameraPosition.Builder()
                                    .target(currentLocation)
                                    .zoom(13)
                                    .tilt(30)
                                    .build();
                            map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

                            // Logic to handle location object
                            Log.d("Parker", "Have last location.");
                        } else {
                            Log.d("Parker", "No last location found");
                        }
                    }
                });

    }

    /**
     * Assign a marker color based on location's percent vaccination coverage
     */
    private static float coverageToColor(double coverage) {
        if (coverage > 92.0) {
            return BitmapDescriptorFactory.HUE_GREEN;
        } else if (coverage > 85.5) {
            return BitmapDescriptorFactory.HUE_YELLOW;
        } else if (coverage > 80.5) {
            return BitmapDescriptorFactory.HUE_ORANGE;
        } else {
            return BitmapDescriptorFactory.HUE_RED;
        }
    }

    private ArrayList<School> prepareMarkers(int resource) throws JSONException {
        ArrayList<School> list = new ArrayList<>();
        InputStream inputStream = getResources().openRawResource(resource);
        String json = new Scanner(inputStream).useDelimiter("\\A").next();
        JSONArray array = new JSONArray(json);
        for (int i = 0; i < array.length(); i++) {
            JSONObject object = array.getJSONObject(i);
            double lat = object.getDouble("lat");
            double lng = object.getDouble("lng");
            String name = object.getString("School_Name");
            int percentTotal = (int) object.getDouble("Percent_complete_for_all_immunizations");
            list.add(new School(name, percentTotal, lat, lng));
        }
        return list;
    }
}
