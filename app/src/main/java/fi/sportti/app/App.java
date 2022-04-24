package fi.sportti.app;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;
import android.util.Log;

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
