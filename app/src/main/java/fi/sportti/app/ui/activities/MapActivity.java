package fi.sportti.app.ui.activities;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

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

@RequiresApi(api = Build.VERSION_CODES.M)
public class MapActivity extends AppCompatActivity {

    private MapView mMapView;
    private MapboxMap mMapboxMap;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MapQuest.start(getApplicationContext());
        setContentView(R.layout.activity_map);
        mMapView = (MapView) findViewById(R.id.saveexercise_mapView_map_for_route);


        mMapView.onCreate(savedInstanceState);
        mMapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(MapboxMap mapboxMap) {
                Log.d("´TESTI", "onMapReady: MAP IS READY");
                List<LatLng> coordinates = RouteContainer.getInstance().getRouteAsList();
                if(coordinates.isEmpty()){
                    coordinates.add(new LatLng(60.2168,24.7104));
                    coordinates.add(new LatLng(60.2144,24.7146));
                    coordinates.add(new LatLng(60.2134,24.7193));
                    coordinates.add(new LatLng(60.2137,24.7214));
                    coordinates.add(new LatLng(60.2130,24.7236));
                }

                mMapboxMap = mapboxMap;
                mMapView.setStreetMode();
                LatLng position = coordinates.get(0);
                CameraUpdate newPosition = CameraUpdateFactory.newLatLngZoom(position, 12);
                mapboxMap.moveCamera(newPosition);
                MarkerOptions startMarker = new MarkerOptions();
                startMarker.position(coordinates.get(0));
                startMarker.setTitle("Alku");
                mapboxMap.addMarker(startMarker);

                MarkerOptions endMarker = new MarkerOptions();
                endMarker.position(coordinates.get(coordinates.size()-1));
                endMarker.setTitle("Loppu");
                mapboxMap.addMarker(endMarker);

                PolylineOptions polyline = new PolylineOptions();
                polyline.addAll(coordinates);
                polyline.width(3);
                polyline.color(Color.BLUE);

                mapboxMap.addPolyline(polyline);
            }
        });
    }


    @Override
    public void onResume()
    { super.onResume(); mMapView.onResume(); }

    @Override
    public void onPause()
    { super.onPause(); mMapView.onPause(); }

    @Override
    protected void onDestroy()
    { super.onDestroy(); mMapView.onDestroy(); }

    @Override
    protected void onSaveInstanceState(Bundle outState)
    { super.onSaveInstanceState(outState); mMapView.onSaveInstanceState(outState); }
}