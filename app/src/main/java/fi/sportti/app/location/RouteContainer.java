package fi.sportti.app.location;

import android.location.Location;
import android.util.Log;

import com.mapbox.mapboxsdk.geometry.LatLng;

import java.util.ArrayList;

public class RouteContainer {
    private static final String TAG = "TESTI";
    private static final RouteContainer instance = new RouteContainer();
    private ArrayList<LatLng> locationList;
    private StringBuilder sb;
    private double currentLat;
    private double currentLon;
    private double distance;
    private float[] results;

    private RouteContainer(){
        sb = new StringBuilder();
        locationList = new ArrayList<>();
        distance = 0;
        currentLat = 0;
        currentLon = 0;
        results = new float[3];
    }

    public static RouteContainer getInstance(){
        return instance;
    }

    public void resetRoute(){
        sb = new StringBuilder();
        locationList = new ArrayList<>();
        distance = 0;
        currentLat = 0;
        currentLon = 0;
    }

    public String getRouteAsText(){
        if(locationList.isEmpty()) {
            return "";
        }
        return sb.toString();
    }

    public double getDistance(){
        return distance;
    }

    public ArrayList<LatLng> getRouteAsList(){
        return locationList;
    }


    public ArrayList<LatLng> convertTextRouteToList(String text){
        ArrayList<LatLng> result = new ArrayList<>();
        if(text != null && !text.isEmpty()){
            String[] locations = text.split("_");
            for(String location : locations){
                String[] coordinates = location.split("&");
                double lat = Double.parseDouble(coordinates[0]);
                double lon = Double.parseDouble(coordinates[1]);
                result.add(new LatLng(lat, lon));
            }
        }
        return result;
    }

    public void addLocation(Location location){
        double newLat = location.getLatitude();
        double newLon = location.getLongitude();
        if(currentLat != 0 && currentLat != 0){
            Location.distanceBetween(currentLat, currentLon, newLat, newLon, results);
            // results[0] contains distance between two location in meters. Change it to kilometers.
            distance += results[0] / 1000;
        }
        Log.d(TAG, "addLocation: distance: " + distance);
        sb.append(newLat + "&" + newLon + "_");
        locationList.add(new LatLng(newLat, newLon));
        currentLat = newLat;
        currentLon = newLon;
    }

    public boolean hasRoute(){
        return !locationList.isEmpty();
    }
}
