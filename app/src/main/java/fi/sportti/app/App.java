package fi.sportti.app;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;

/**
 * @author Jukka-Pekka Jaakkola
 * Only purpose of this class is to create one Notification Channel for application on Android versions Oreo or higher.
 * All other classes where Notifications are used, can use this same channel instead of creating their owns.
 * */

public class App extends Application {
    /** One common TAG that can be used while printing something to Logcat in other classes during development. */
    public static final String TAG = "TESTI";

    /** One Notification channel ID that all classes can use. */
    public static final String NOTIFICATION_CHANNEL_ID = "fi.sportti.app.Sportti_Notification_Channel";
    /** Assigned codes for different permissions that other classes can use. */
    public static final int PERMISSION_CODE_FINE_LOCATION = 100;
    public static final int PERMISSION_CODE_READ_PHONE_STATE = 101;
    public static final int PERMISSION_CODE_ENABLE_LOCATION_SERVICES = 102;

    @Override
    public void onCreate() {
        super.onCreate();
        //Create Notification Channel for application on Android versions Oreo or higher.
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            String channelName = "Sportti exercise recording channel";
            NotificationChannel channel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, channelName, NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }
    }
}
