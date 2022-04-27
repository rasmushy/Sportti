package fi.sportti.app.ui.activities;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.annotations.PolylineOptions;
import com.mapbox.mapboxsdk.camera.CameraUpdate;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapquest.mapping.MapQuest;
import com.mapquest.mapping.maps.MapView;

import java.util.List;

import fi.sportti.app.R;
import fi.sportti.app.location.RouteContainer;

/**
 *@author Jukka-Pekka Jaakkola
 * Own activity used to display routes on map.
 * Route needs to be added to Intent that starts this activity.
 * Use MapActivity.EXTRA_ROUTE as key.
 */

public class MapActivity extends AppCompatActivity {

    public static final String EXTRA_ROUTE = "fi.sportti.app.route_as_extra_for_map";
    private MapView mapView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MapQuest.start(getApplicationContext());
        Intent intent = getIntent();
        String route = intent.getExtras().getString(EXTRA_ROUTE);
        setContentView(R.layout.activity_map);
        mapView = findViewById(R.id.saveexercise_mapView_map_for_route);


        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(MapboxMap mapboxMap) {
                List<LatLng> coordinates = RouteContainer.getInstance().convertTextRouteToList(route);
                mapView.setStreetMode();

                //Center camera at start location.
                LatLng startPosition = coordinates.get(0);
                LatLng endPosition = coordinates.get(coordinates.size()-1);
                CameraUpdate newPosition = CameraUpdateFactory.newLatLngZoom(startPosition, 12);
                mapboxMap.moveCamera(newPosition);

                //Add markers
                String startMarkerText = getResources().getString(R.string.map_start_marker);
                String endMarkerText = getResources().getString(R.string.map_end_marker);
                MarkerOptions startMarker = new MarkerOptions();
                startMarker.position(startPosition);
                startMarker.setTitle(startMarkerText);
                mapboxMap.addMarker(startMarker);
                MarkerOptions endMarker = new MarkerOptions();
                endMarker.position(endPosition);
                endMarker.setTitle(endMarkerText);
                mapboxMap.addMarker(endMarker);

                //Add route as polyline.
                PolylineOptions polyline = new PolylineOptions()
                        .addAll(coordinates)
                        .width(3)
                        .color(Color.BLUE);
                mapboxMap.addPolyline(polyline);
            }
        });
    }

    //These lifecycle methods have to be implemented because MapQuest requires to call same methods on map.

    @Override
    public void onResume()
    { super.onResume(); mapView.onResume(); }

    @Override
    public void onPause()
    { super.onPause(); mapView.onPause(); }

    @Override
    protected void onDestroy()
    { super.onDestroy(); mapView.onDestroy(); }

    @Override
    protected void onSaveInstanceState(Bundle outState)
    { super.onSaveInstanceState(outState); mapView.onSaveInstanceState(outState); }
}