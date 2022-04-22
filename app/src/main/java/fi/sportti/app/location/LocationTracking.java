package fi.sportti.app.location;

import android.Manifest;
import android.app.Notification;
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

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

import fi.sportti.app.ui.activities.MainActivity;
import fi.sportti.app.R;

@RequiresApi(api = Build.VERSION_CODES.O)
public class LocationTracking extends Service {

    public static final String STOP_TRACKING = "STOP";
    public static final String START_TRACKING = "START";
    public static boolean serviceRunning = false;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private LocationRequest locationRequest;
    private LocationCallback locationCallBack;
    private double currentLat = 0;
    private double currentLon = 0;
    private double distance = 0;
    private float[] results = new float[3];

    @Override
    public int onStartCommand(Intent intent, int flags, int startId){
        if(intent != null && intent.getAction() != null){
            if(intent.getAction().equals(START_TRACKING)){
                startTracking();
            }
            else if(intent.getAction().equals(STOP_TRACKING)){
                stopTracking();
            }
        }
        return super.onStartCommand(intent, flags, startId);
    }

    private void startTracking(){
        serviceRunning = true;
        Log.d("TESTI", "onStartCommand: STARTING FOREGROUND SERVICE ");
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        locationRequest = LocationRequest.create();
        locationRequest.setInterval(4000);
        locationRequest.setFastestInterval(1000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        locationCallBack = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                super.onLocationResult(locationResult);
                processResult(locationResult);
            }
        };

        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallBack, null);
        }

        String channelID = "Foreground Service ID";
        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
        Notification notification =
                new Notification.Builder(this, channelID)
                        .setContentTitle("Sportti")
                        .setContentText("Tracking location")
                        .setSmallIcon(R.drawable.ic_launcher_background)
                        .setContentIntent(pendingIntent)
                        .build();
        startForeground(1001, notification);
    }

    private void stopTracking(){
        serviceRunning = false;
        Log.d("TESTI", "onStartCommand: STOPPING FOREGROUND SERVICE ");
        if(fusedLocationProviderClient != null){
            fusedLocationProviderClient.removeLocationUpdates(locationCallBack);
        }
        stopForeground(true);
        stopSelf();
    }

    private void processResult(LocationResult locationResult){
        Location location = locationResult.getLastLocation();
        if(currentLat != 0 && currentLat != 0){
            Location.distanceBetween(currentLat, currentLon, location.getLatitude(), location.getLongitude(), results);
            distance += results[0];
        }
        if(location.getLatitude() != currentLat && location.getLongitude() != currentLon){
            currentLat = location.getLatitude();
            currentLon = location.getLongitude();
            RouteContainer.getInstance().addLocation(location);
        }
        Log.d("TESTI", "lat: " + String.valueOf(location.getLatitude()) + ", lon: " + String.valueOf(location.getLongitude()));
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
