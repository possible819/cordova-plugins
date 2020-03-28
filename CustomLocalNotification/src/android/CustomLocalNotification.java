package custom.plugin.local.notification;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;

import androidx.core.app.NotificationCompat;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.json.JSONArray;
import org.json.JSONException;

/**
 * This class echoes a string called from JavaScript.
 */
public class CustomLocalNotification extends CordovaPlugin {

    private final String ACTION_TYPE_ADD = "addNotification";
    private final String ACTION_TYPE_CLEAR = "clearNotification";

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        boolean result = false;
        if (action.equals(ACTION_TYPE_ADD)) {
            String contentTitle = args.getString(0);
            String subText = args.getString(1);
            String contentText = args.getString(2);
            int hour = args.getInt(3);
            int minute = args.getInt(4);

            this.addNotification(contentTitle, subText, contentText, hour, minute, callbackContext);
            result = true;
        } else if (action.equals(ACTION_TYPE_CLEAR)) {
            this.clearNotification(callbackContext);
            result = true;
        } else {
            callbackContext.error("Failed to find action " + action);
        }
        return result;
    }

    private void addNotification(String contentTitle, String subText, String contentText, int hour, int minute,
            CallbackContext callbackContext) {
        final int NOTIFICATION_ID = 19870819;
        final String NOTIFICATION_CHANNEL_ID = "CST_LOCAL_NOTIFICATION_CHN_ID";
        Context context = this.cordova.getContext();
        NotificationManager notificationManager = (NotificationManager) context
                .getSystemService(context.NOTIFICATION_SERVICE);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID)
                .setContentTitle(contentTitle).setContentText(contentText).setSubText(subText)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            final CharSequence CHANNEL_NAME = "CST_LOCAL_NOTIFICATION_NAME";
            NotificationChannel channel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
        }

        notificationManager.notify(NOTIFICATION_ID, builder.build());
        callbackContext.success();
    }

    private void clearNotification(CallbackContext callbackContext) {

    }
}
