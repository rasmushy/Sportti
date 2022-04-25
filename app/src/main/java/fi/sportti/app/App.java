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

    public static final String NOTIFICATION_CHANNEL_ID = "fi.sportti.app.Sportti_Notification_Channel";

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
