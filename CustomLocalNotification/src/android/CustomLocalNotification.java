package custom.plugin.local.notification;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.json.JSONArray;
import org.json.JSONException;

import java.util.Calendar;

public class CustomLocalNotification extends CordovaPlugin {

    private final String ACTION_TYPE_ADD = "addNotification";
    private final String ACTION_TYPE_CLEAR = "clearNotification";
    private final int REQUEST_CODE = 0;
    private final int INTENT_FLAG = PendingIntent.FLAG_UPDATE_CURRENT;

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        boolean result = false;
        if (action.equals(ACTION_TYPE_ADD)) {
            String title = args.getString(0);
            String subText = args.getString(1);
            String text = args.getString(2);
            int hour = args.getInt(3);
            int minute = args.getInt(4);

            this.addNotification(title, subText, text, hour, minute, callbackContext);
            result = true;
        } else if (action.equals(ACTION_TYPE_CLEAR)) {
            this.clearNotification(callbackContext);
            result = true;
        } else {
            callbackContext.error("Failed to find action " + action);
        }

        return result;
    }

    private void addNotification(String title, String subText, String text, int hour, int minute,
            CallbackContext callbackContext) {
        try {
            this.registerNotification(title, subText, text, hour, minute);
            callbackContext.success();
        } catch (Exception e) {
            callbackContext.error(e.getMessage());
        }
    }

    private void registerNotification(String title, String subText, String text, int hour, int minute) {
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this.cordova.getContext(), this.REQUEST_CODE,
                this.getIntent(title, subText, text), this.INTENT_FLAG);
        AlarmManager alarmManager = (AlarmManager) this.cordova.getContext().getSystemService(Context.ALARM_SERVICE);

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, 0);
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.add(Calendar.SECOND, 10);

        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY,
                pendingIntent);

    }

    private void clearNotification(CallbackContext callbackContext) {
        try {
            PendingIntent pendingIntent = PendingIntent.getBroadcast(this.cordova.getContext(), this.REQUEST_CODE,
                    this.getIntent(), this.INTENT_FLAG);
            AlarmManager alarmManager = (AlarmManager) this.cordova.getContext()
                    .getSystemService(Context.ALARM_SERVICE);
            alarmManager.cancel(pendingIntent);
            callbackContext.success();
        } catch (Exception e) {
            callbackContext.error(e.getMessage());
        }
    }

    private Intent getIntent() {
        return new Intent(this.cordova.getContext(), NotificationReceiver.class);
    }

    private Intent getIntent(String title, String subText, String text) {
        Intent intent = new Intent(this.cordova.getContext(), NotificationReceiver.class);
        intent.putExtra("title", title);
        intent.putExtra("subText", subText);
        intent.putExtra("text", text);

        return intent;
    }
}
