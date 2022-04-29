package fi.sportti.app.ui.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;

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
    public static final String TAG = "TESTI";
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

                //Set camera at center of whole route.
                setCamera(mapboxMap, coordinates);
                LatLng startPosition = coordinates.get(0);
                LatLng endPosition = coordinates.get(coordinates.size()-1);

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

    private void setCamera(MapboxMap map, List<LatLng> list){
        //Go through all positions on route and calculate average of all coordinates.
        //Then center camera at that location. At same time, calculate max distance between two points
        //in route. This will determine correct zoom value so whole route shows on map when its opened.
        float [] result = new float[3];
        double maxDistance = 0;
        double latSum = 0;
        double lonSum = 0;
        LatLng position1;
        LatLng position2;
        for(int i = 0; i < list.size() - 1; i++){
            position1 = list.get(i);
            latSum += position1.getLatitude();
            lonSum += position1.getLongitude();
            for(int j = i+1; j < list.size(); j++){
                position2 = list.get(j);
                double lat1 = position1.getLatitude();
                double lon1 = position1.getLongitude();
                double lat2 = position2.getLatitude();
                double lon2 = position2.getLongitude();
                Location.distanceBetween(lat1, lon1, lat2, lon2, result);
                //Location.distanceBetween method saves result in float array at index 0.
                if(result[0] > maxDistance){
                    maxDistance = result[0];
                }
            }
        }
        //Add last position in route to calculations.
        position1 = list.get(list.size()-1);
        latSum += position1.getLatitude();
        lonSum += position1.getLongitude();
        double latAvg = latSum / list.size();
        double lonAvg = lonSum / list.size();

        LatLng cameraPosition = new LatLng(latAvg, lonAvg);
        double zoom = getValueForZoom(maxDistance/1000);
        CameraUpdate newPosition = CameraUpdateFactory.newLatLngZoom(cameraPosition, zoom);
        map.moveCamera(newPosition);

    }

    private double getValueForZoom(double distance){
        //Different zoom values for map based on max distance between two points in route.
        //Bigger distance -> smaller zoom value so whole route shows on map.
        double zoom = 0;
        if(distance <= 1){
            zoom = 13;
        }
        else if(distance <= 5){
            zoom = 12;
        }
        else if(distance <= 10){
            zoom = 9;
        }
        else if(distance <= 100){
            zoom = 8;
        }
        else {
            zoom = 7;
        }
        return zoom;
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