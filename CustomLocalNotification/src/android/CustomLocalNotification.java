package custom.plugin.local.notification;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.SystemClock;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.json.JSONArray;
import org.json.JSONException;

import java.util.Calendar;

import androidx.core.app.NotificationCompat;

/**
 * This class echoes a string called from JavaScript.
 */
public class CustomLocalNotification extends CordovaPlugin {

    private final String ACTION_TYPE_ADD = "addNotification";
    private final String ACTION_TYPE_CLEAR = "clearNotification";
    private final int REQUEST_CODE = 19870819;
    private final int NOTIFICATION_ID = 19870819;
    private final String NOTIFICATION_CHANNEL_ID = "CST_LOCAL_NOTIFICATION_CHN_ID";
    private final String CHANNEL_NAME = "CST_LOCAL_NOTIFICATION_NAME";

    private Context context;
    private String contentTitle;
    private String subText;
    private String contentText;
    private int hour;
    private int minute;
    private CallbackContext callbackContext;

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        this.context = this.cordova.getContext();
        boolean result = false;
        if (action.equals(ACTION_TYPE_ADD)) {
            this.contentTitle = args.getString(0);
            this.subText = args.getString(1);
            this.contentText = args.getString(2);
            this.hour = args.getInt(3);
            this.minute = args.getInt(4);
            this.callbackContext = callbackContext;

            this.addNotification();
            result = true;
        } else if (action.equals(ACTION_TYPE_CLEAR)) {
            this.clearNotification();
            result = true;
        } else {
            this.callbackContext.error("Failed to find action " + action);
        }
        return result;
    }

    private void addNotification() {
        try {
            this.registerNotification();
            this.callbackContext.success();
        } catch (Exception e) {
            this.callbackContext.error(e.getMessage());
        }
    }

    private void registerNotification() {
        Intent intent = new Intent(this.context, NotificationReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this.context, this.REQUEST_CODE, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        AlarmManager alarmManager = (AlarmManager) this.context.getSystemService(Context.ALARM_SERVICE);

        // Calendar calendar = Calendar.getInstance();
        // calendar.set(Calendar.HOUR_OF_DAY, this.hour);
        // calendar.set(Calendar.MINUTE, this.minute);
        // calendar.set(Calendar.SECOND, 0);

        // alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,
        // calendar.getTimeInMillis(), 10 * 1000, pendingIntent);

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.add(Calendar.SECOND, 10);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), 10 * 1000, pendingIntent);
    }

    private void clearNotification() {
        try {
            Intent intent = new Intent(this.context, NotificationReceiver.class);
            PendingIntent pendingIntent = PendingIntent.getService(this.context, this.REQUEST_CODE, intent,
                    PendingIntent.FLAG_UPDATE_CURRENT);
            AlarmManager alarmManager = (AlarmManager) this.context.getSystemService(Context.ALARM_SERVICE);
            alarmManager.cancel(pendingIntent);
            this.callbackContext.success();
        } catch (Exception e) {
            this.callbackContext.error(e.getMessage());
        }
    }

    class NotificationReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            NotificationManager notificationManager = (NotificationManager) context
                    .getSystemService(context.NOTIFICATION_SERVICE);

            NotificationCompat.Builder builder = new NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID)
                    .setSmallIcon(android.R.drawable.alert_light_frame).setContentTitle(contentTitle)
                    .setContentText(contentText).setSubText(subText).setPriority(NotificationCompat.PRIORITY_DEFAULT);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                NotificationChannel channel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, CHANNEL_NAME,
                        NotificationManager.IMPORTANCE_DEFAULT);
                notificationManager.createNotificationChannel(channel);
            }

            notificationManager.notify(NOTIFICATION_ID, builder.build());

        }
    }

    class NotificationService extends JobService {
        @Override
        public boolean onStartJob(JobParameters params) {
            boolean result = true;
            try {
                NotificationManager notificationManager = (NotificationManager) context
                        .getSystemService(context.NOTIFICATION_SERVICE);

                NotificationCompat.Builder builder = new NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID)
                        .setSmallIcon(android.R.drawable.alert_light_frame).setContentTitle(contentTitle)
                        .setContentText(contentText).setSubText(subText)
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    final CharSequence CHANNEL_NAME = "CST_LOCAL_NOTIFICATION_NAME";
                    NotificationChannel channel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, CHANNEL_NAME,
                            NotificationManager.IMPORTANCE_DEFAULT);
                    notificationManager.createNotificationChannel(channel);
                }

                notificationManager.notify(NOTIFICATION_ID, builder.build());

            } catch (Exception e) {
                result = false;
            }

            return result;
        }

        @Override
        public boolean onStopJob(JobParameters params) {
            return false;
        }
    }
}
