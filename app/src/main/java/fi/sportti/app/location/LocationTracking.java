package fi.sportti.app.location;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

import fi.sportti.app.R;
import fi.sportti.app.ui.activities.StartExerciseActivity;

/**
 *@author Jukka-Pekka Jaakkola
 */

@RequiresApi(api = Build.VERSION_CODES.O)
public class LocationTracking extends Service {
    private static final String TAG = "TESTI"; // TAG for Log.d
    public static final String STOP_TRACKING = "fi.sportti.action.STOP_TRACKING";
    public static final String START_TRACKING = "fi.sportti.action.START_TRACKING";
    public static boolean serviceRunning = false;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private LocationRequest locationRequest;
    private LocationCallback locationCallBack;
    private double currentLat = 0;
    private double currentLon = 0;


    private Notification notification;
    private String trackingOnMessage = "Tracking location";
    private String trackingPausedMessage = "Tracking location is paused.";


    @Override
    public void onCreate(){
        super.onCreate();
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        locationRequest = LocationRequest.create();
        locationRequest.setInterval(5000);
        locationRequest.setFastestInterval(5000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        //Create callback which will be called everytime FusedLocationProviderClient updates phones location
        //based on interval times set.
        locationCallBack = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                super.onLocationResult(locationResult);
                processResult(locationResult);
            }
        };
        Log.d(TAG, "onCreate: Location tracking service created");
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        stopTracking();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId){
        if(intent != null && intent.getAction() != null){
            String action = intent.getAction();
            if(action.equals(START_TRACKING)){
                serviceRunning = true;
                startTracking();
                createNotification(trackingOnMessage);
                startForeground(1001, notification);
            }
            else if(action.equals(STOP_TRACKING)){
                serviceRunning = true;
                stopTracking();
                createNotification(trackingPausedMessage);
                startForeground(1001, notification);
            }
        }
        return START_NOT_STICKY;
    }

    private void startTracking(){
        Log.d("TESTI", "onStartCommand: STARTING TRACKING ");
        //Check that app has permission to location before requesting location updates.
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallBack, null);
        }
    }

    private void stopTracking(){
        if(fusedLocationProviderClient != null){
            fusedLocationProviderClient.removeLocationUpdates(locationCallBack);
        }
    }

    private void createNotification(String message){
        //Location tracking service will be started as foreground service. This service requires Notification.
        //In the code below, this Notification is built.

        //With Android Oreo versions or higher, notification has to be made with notification channel.
        String notificationChannelID = "Sportti Notification Channel";
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            String channelName = "Sportti foreground tracking channel";
            NotificationChannel channel = new NotificationChannel(notificationChannelID, channelName, NotificationManager.IMPORTANCE_MIN);
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }

        //Create Pending Intent which is passed to notification so user can open correct activity by pressing notification.
        Intent notificationIntent = new Intent(this, StartExerciseActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);

        //Build Notification. Notification Channel ID is passed to constructor to support newer versions of Android (Oreo and newer).
        //On older versions this ID is simply ignored.
        notification= new NotificationCompat.Builder(this, notificationChannelID)
                .setOngoing(true)
                .setSmallIcon(R.drawable.ic_baseline_location_on_24)
                .setContentTitle("Sportti")
                .setContentText(message)
                .setPriority(NotificationManager.IMPORTANCE_MIN)
                .setContentIntent(pendingIntent)
                .build();
    }

    private void processResult(LocationResult locationResult){
        Log.d(TAG, "processResult: ");
        Location location = locationResult.getLastLocation();
        double newLat = location.getLatitude();
        double newLon = location.getLongitude();
        if(newLat != currentLat || newLon != currentLon){
            currentLat = newLat;
            currentLon = newLon;
            RouteContainer.getInstance().addLocation(location);
            Log.d(TAG, "Added new location to route");
        }
//        Log.d("TESTI", "lat: " + String.valueOf(location.getLatitude()) + ", lon: " + String.valueOf(location.getLongitude()));
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
