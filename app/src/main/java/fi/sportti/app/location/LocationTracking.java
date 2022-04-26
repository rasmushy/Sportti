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

import java.util.Locale;

import fi.sportti.app.App;
import fi.sportti.app.R;
import fi.sportti.app.ui.activities.StartExerciseActivity;

/**
 *@author Jukka-Pekka Jaakkola
 */

@RequiresApi(api = Build.VERSION_CODES.O)
public class LocationTracking extends Service {
    private static final String TAG = "TESTI"; // TAG for Log.d
    public static boolean serviceRunning = false;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private LocationRequest locationRequest;
    private LocationCallback locationCallBack;
    private RouteContainer routeContainer;
    private Notification notification;

    @Override
    public void onCreate(){
        super.onCreate();
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        locationRequest = LocationRequest.create();
        locationRequest.setInterval(2000);
        locationRequest.setFastestInterval(2000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        routeContainer = RouteContainer.getInstance();

        //Create callback which will be called everytime FusedLocationProviderClient updates phones location
        //based on interval times set.
        locationCallBack = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                super.onLocationResult(locationResult);
                Location location = locationResult.getLastLocation();
                routeContainer.addLocation(location);
            }
        };
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId){
        serviceRunning = true;
        startTracking();
        createNotification();
        startForeground(1001, notification);

        //Tells Android System that it has to recreate this Service once it can, if for some reason it has to kill it.
        return START_STICKY;
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        stopTracking();
        serviceRunning = false;
    }

    private void startTracking(){
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

    private void createNotification(){
        //Location tracking service will be started as foreground service which requires Notification.
        //In the code below, this Notification is built.

        //Create Pending Intent which is passed to notification so user can open correct activity by pressing notification.
        Intent notificationIntent = new Intent(this, StartExerciseActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);

        //Build Notification. Notification Channel ID is passed to constructor to support newer versions of Android (Oreo and newer).
        //On older versions this ID is simply ignored.
        //Notification Channel itself is created in App class when application starts.

        String title = getResources().getString(R.string.notification_location_tracking_title);
        String message = getResources().getString(R.string.notification_location_tracking_on_message);
        notification = new NotificationCompat.Builder(this, App.NOTIFICATION_CHANNEL_ID)
                .setOngoing(true)
                .setSmallIcon(R.drawable.ic_baseline_location_on_24)
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationManager.IMPORTANCE_DEFAULT)
                .setContentIntent(pendingIntent)
                .build();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
