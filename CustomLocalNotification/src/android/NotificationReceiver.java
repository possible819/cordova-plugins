package custom.plugin.local.notification;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.preference.PreferenceManager;

import androidx.core.app.NotificationCompat;
import xyz.timemachine.jaylee.app.MainActivity;

public class NotificationReceiver extends BroadcastReceiver {

  private final int NOTIFICATION_ID = 1491894352;
  private final String NOTIFICATION_CHANNEL_ID = "CST_LOCAL_NOTIFICATION_CHN_ID";
  private final String CHANNEL_NAME = "CST_LOCAL_NOTIFICATION_NAME";

  @Override
  public void onReceive(Context context, Intent intent) {
    String title = intent.getStringExtra("title");
    String subText = intent.getStringExtra("subText");
    String text = intent.getStringExtra("text");

    if (title.isEmpty()) {
      title = this.getSharedPreference(context, "title");
    }

    if (subText.isEmpty()) {
      subText = this.getSharedPreference(context, "subText");
    }

    if (text.isEmpty()) {
      text = this.getSharedPreference(context, "text");
    }

    NotificationManager notificationManager = (NotificationManager) context
        .getSystemService(context.NOTIFICATION_SERVICE);

    Intent contentIntent = new Intent(context, MainActivity.class);
    PendingIntent pContentIntent = PendingIntent.getActivity(context, 0, contentIntent, 0);

    NotificationCompat.Builder builder = new NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID)
        .setSmallIcon(android.R.drawable.ic_menu_edit).setContentTitle(title).setSubText(subText).setContentText(text)
        .setPriority(NotificationCompat.PRIORITY_DEFAULT).setContentIntent(pContentIntent);

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
      NotificationChannel channel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, CHANNEL_NAME,
          NotificationManager.IMPORTANCE_DEFAULT);
      notificationManager.createNotificationChannel(channel);
    }

    notificationManager.notify(NOTIFICATION_ID, builder.build());
  }

  private String getSharedPreference(Context context, String key) {
    SharedPreferences appPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    return appPreferences.getString(key, "");
  }
}
