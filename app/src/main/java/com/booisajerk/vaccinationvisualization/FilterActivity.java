package com.booisajerk.vaccinationvisualization;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class FilterActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, OnMapReadyCallback {

    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter);


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
        Toolbar toolbar = findViewById(R.id.toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        //TODO add My location functionality here

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        LatLng kingNorthEast = new LatLng(47.659525, -122.255107);
        LatLng kingSouthWest = new LatLng(47.569958, -122.377845);

        LatLngBounds kingCounty = new LatLngBounds(kingSouthWest, kingNorthEast);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(kingCounty.getCenter(), 13));

        addMapMarkers();
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
        List<Location> list = null;

        try {
            list = prepareLocations(R.raw.immunization);
        } catch (JSONException e) {
            Toast.makeText(this, "Problem reading list of locations.", Toast.LENGTH_LONG).show();
        }

        if (list != null) {
            for (Location loc : list) {
                mMap.addMarker(new MarkerOptions()
                        .position(loc.getLatLng())
                        .title(loc.getName())
                        .icon(BitmapDescriptorFactory.defaultMarker(coverageToColor(loc.getPercentTotal())))
                        .snippet("Percent coverage " + loc.getPercentTotal()));
            }
        }
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

    private ArrayList<Location> prepareLocations(int resource) throws JSONException {
        ArrayList<Location> list = new ArrayList<>();
        InputStream inputStream = getResources().openRawResource(resource);
        String json = new Scanner(inputStream).useDelimiter("\\A").next();
        JSONArray array = new JSONArray(json);
        for (int i = 0; i < array.length(); i++) {
            JSONObject object = array.getJSONObject(i);
            double lat = object.getDouble("lat");
            double lng = object.getDouble("lng");
            String name = object.getString("School_Name");
            double percentTotal = object.getDouble("Percent_complete_for_all_immunizations");
            list.add(new Location(name, percentTotal, lat, lng));
        }
        return list;
    }
}
