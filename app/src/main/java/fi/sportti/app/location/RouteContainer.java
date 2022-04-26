package fi.sportti.app.location;

import android.location.Location;
import android.util.Log;

import com.mapbox.mapboxsdk.geometry.LatLng;

import java.util.ArrayList;

/**
 *@author Jukka-Pekka Jaakkola
 * Class which holds the route data while user is recording exercise with location tracking on.
 */

public class RouteContainer {
    private static final String TAG = "TESTI";
    private static final RouteContainer instance = new RouteContainer();
    private ArrayList<LatLng> locationList;
    private StringBuilder sb;
    private double currentLat;
    private double currentLon;
    private double routeLength;
    private float[] results;

    private RouteContainer(){
        sb = new StringBuilder();
        locationList = new ArrayList<>();
        routeLength = 0;
        currentLat = 0;
        currentLon = 0;
        results = new float[3];
    }

    /**
     * @return RouteContainer returns instance of this class.
     * */
    public static RouteContainer getInstance(){
        return instance;
    }

    /**
     * Resets all previous route data.
     * */
    public void resetRoute(){
        sb = new StringBuilder();
        locationList = new ArrayList<>();
        routeLength = 0;
        currentLat = 0;
        currentLon = 0;
    }

    /**
     * Returns coordinates of current route in one String.
     * Location's coordinates are separated from each other by & and Locations are separated from each other by _
     * Format: lat1&lon1_lat2&lon2_lat3&lon3... etc
     * @return String Route in string format, returns empty string if there are no location data on route */
    public String getRouteAsText(){
        if(locationList.isEmpty()) {
            return "";
        }
        return sb.toString();
    }

    /**
     * @return double Returns length of the route.
     * */
    public double getRouteLength(){
        return routeLength;
    }

    /**
     * @return ArrayList<LatLng>  Returns ArrayList which contains route's locations.
     * */
    public ArrayList<LatLng> getRouteAsList(){
        return locationList;
    }

    /**
     * Takes in route in String format and converts it to ArrayList containing route's locations as LatLng objects.
     * @param text Route in String format. Recommended to only pass routes that are built by this class.
     * @return ArrayList<LatLng>  Returns ArrayList which contains route's locations.
     * */
    public ArrayList<LatLng> convertTextRouteToList(String text){
        ArrayList<LatLng> result = new ArrayList<>();
        try {
            if(text != null && !text.isEmpty()){
                String[] locations = text.split("_");
                for(String location : locations){
                    String[] coordinates = location.split("&");
                    double lat = Double.parseDouble(coordinates[0]);
                    double lon = Double.parseDouble(coordinates[1]);
                    result.add(new LatLng(lat, lon));
                }
            }
        }
        catch(Exception e){
            // In case there is error with parsing route, create new empty list and return it instead.
            result = new ArrayList<>();
        }
        return result;
    }

    /**
     * Add new Location to route.
     * @param location Location to add to route
     * */
    public void addLocation(Location location){
        double newLat = location.getLatitude();
        double newLon = location.getLongitude();
        if(newLat != currentLat || newLon != currentLon){
            updateRouteLength(newLat, newLon);
            sb.append(newLat + "&" + newLon + "_");
            LatLng newLocation = new LatLng(newLat, newLon);
            locationList.add(newLocation);
            currentLat = newLat;
            currentLon = newLon;
            Log.d(TAG, "Added new location to route (" + locationList.size() + ")");
        }
    }

    /**
     * Check if RouteContainer has route.
     * @return boolean Returns true if route has at least one Location added.
     * */
    public boolean hasRoute(){
        return !locationList.isEmpty();
    }

    private void updateRouteLength(double newLat, double newLon){
        if(currentLat != 0 && currentLat != 0){
            Location.distanceBetween(currentLat, currentLon, newLat, newLon, results);
            // results[0] contains distance between two location in meters. Change it to kilometers.
            routeLength += results[0] / 1000;
        }
    }

    public void setRoute(String route){
        if(route != null && !route.isEmpty()){
            sb.append(route);
            String[] locations = route.split("_");
            for(String location : locations){
                String[] coordinates = location.split("&");
                double lat = Double.parseDouble(coordinates[0]);
                double lon = Double.parseDouble(coordinates[1]);
                locationList.add(new LatLng(lat, lon));
                Log.d(TAG, "Added new location to route (" + locationList.size() + ")");
                if(currentLat == 0 && currentLon == 0){
                    currentLat = lat;
                    currentLon = lon;
                }
                else {
                    updateRouteLength(lat, lon);
                    currentLat = lat;
                    currentLon = lon;
                }

            }
        }
    }
}
