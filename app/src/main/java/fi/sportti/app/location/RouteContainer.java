package fi.sportti.app.location;

import android.location.Location;
import android.util.Log;

import com.mapbox.mapboxsdk.geometry.LatLng;

import java.util.ArrayList;

import fi.sportti.app.App;

/**
 * @author Jukka-Pekka Jaakkola
 * Class which holds the route data while user is recording exercise with location tracking on.
 * Exercise's route is saved in database in String format.
 * Location's coordinates are separated from each other by "&" and Locations are separated from each other by "_"
 * Format: lat1&lon1_lat2&lon2_lat3&lon3... etc
 * It is recommened to only use this class while handling routes to avoid syntax errors.
 */

public class RouteContainer {
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
     * @return String Route in string format, returns empty string if there are no location data on route
     * */
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
     * Check if RouteContainer has route. Returns true if route has at least one Location added.
     * @return boolean
     * */
    public boolean hasRoute(){
        return !locationList.isEmpty();
    }

    /**
     * Takes in route in String format and converts it to ArrayList containing route's locations as LatLng objects.
     * @param text Route in String format. Recommended to only pass routes that are built by this class.
     * @return ArrayList<LatLng>  Returns ArrayList which contains route's locations.
     * */
    public ArrayList<LatLng> convertTextRouteToList(String text){
        ArrayList<LatLng> result = new ArrayList<>();
        //Return empty list if given parameter was not correct.
        if(text == null || text.isEmpty()){
            return result;
        }

        //Try to parse through text and add locations to list.
        try {
            String[] locations = text.split("_");
            for(String location : locations){
                String[] coordinates = location.split("&");
                double lat = Double.parseDouble(coordinates[0]);
                double lon = Double.parseDouble(coordinates[1]);
                result.add(new LatLng(lat, lon));
            }
        }
        // In case there is error with parsing route, create new empty list and return it instead.
        catch(Exception e){
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
        //Only add new location if its different than last saved location.
        if(newLat != currentLat || newLon != currentLon){
            updateRouteLength(newLat, newLon);
            sb.append(newLat + "&" + newLon + "_");
            LatLng newLocation = new LatLng(newLat, newLon);
            locationList.add(newLocation);
            currentLat = newLat;
            currentLon = newLon;
            Log.d(App.TAG, "Added new location to route (" + locationList.size() + ")");
        }
    }

    /**
     * Set route to Route Container by passing complete route in String format.
     * @param route Route as text, Format: lat1&lon1_lat2&lon2_lat3&lon3... etc
     */
    public void setRoute(String route){
        //Don't do anything if parameter is null or empty.
        if(route == null || route.isEmpty()){
            return;
        }
        //Reset old route if there is one.
        if(hasRoute()){
            resetRoute();
        }
        //Try to parse through new route and add it to RouteContainer.
        try {
            //Loop through all locations in route and add them to list.
            String[] locations = route.split("_");
            for(String location : locations){
                String[] coordinates = location.split("&");
                double lat = Double.parseDouble(coordinates[0]);
                double lon = Double.parseDouble(coordinates[1]);
                locationList.add(new LatLng(lat, lon));
                updateRouteLength(lat, lon);
                currentLat = lat;
                currentLon = lon;
            }
            //Add route in string format to StringBuilder.
            sb.append(route);
        }
        //If something goes wrong while parsing route, reset whole route.
        catch(Exception e){
           resetRoute();
        }
    }

    private void updateRouteLength(double newLat, double newLon){
        if(currentLat != 0 && currentLat != 0){
            Location.distanceBetween(currentLat, currentLon, newLat, newLon, results);
            // results[0] contains distance between two location in meters. Change it to kilometers.
            routeLength += results[0] / 1000;
        }
    }
}
