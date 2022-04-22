package fi.sportti.app.location;

import android.location.Location;

import com.mapbox.mapboxsdk.geometry.LatLng;

import java.util.ArrayList;

public class RouteContainer {
    private static final RouteContainer instance = new RouteContainer();
    private ArrayList<LatLng> locationList;
    private StringBuilder sb;

    private RouteContainer(){
        sb = new StringBuilder();
        locationList = new ArrayList<>();
    }

    public static RouteContainer getInstance(){
        return instance;
    }

    public void resetRoute(){
        sb = new StringBuilder();
        locationList = new ArrayList<>();
    }

    public String getRouteAsText(){
        return sb.toString();
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
        sb.append(location.getLatitude() + "&" + location.getLongitude() + "_");
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        locationList.add(latLng);
    }

    public boolean hasRoute(){
        return locationList.isEmpty();
    }
}
