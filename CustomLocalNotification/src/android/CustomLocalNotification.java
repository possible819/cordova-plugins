package custom.plugin.local.notification;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

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

    private Context context;
    private String title;
    private String subText;
    private String text;
    private int hour;
    private int minute;

    public CustomLocalNotification() {
    }

    public CustomLocalNotification(Context context, String title, String subText, String text, int hour, int minute) {
        this.context = context;
        this.title = title;
        this.subText = subText;
        this.text = text;
        this.hour = hour;
        this.minute = minute;
    }

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        boolean result = false;
        this.context = this.cordova.getContext();

        try {
            if (action.equals(ACTION_TYPE_ADD)) {
                this.title = args.getString(0);
                this.subText = args.getString(1);
                this.text = args.getString(2);
                this.hour = args.getInt(3);
                this.minute = args.getInt(4);

                this.addNotification();
            } else if (action.equals(ACTION_TYPE_CLEAR)) {
                this.clearNotification();
            }
            callbackContext.success();
            result = true;
        } catch (Exception e) {
            callbackContext.error(e.getMessage());
        } finally {
            return result;
        }
    }

    protected void addNotification() {
        this.setSharedPreferences();

        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, this.REQUEST_CODE,
                this.getIntent(this.title, this.subText, this.text), this.INTENT_FLAG);
        AlarmManager alarmManager = (AlarmManager) this.context.getSystemService(Context.ALARM_SERVICE);

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, this.hour);
        calendar.set(Calendar.MINUTE, this.minute);
        calendar.set(Calendar.SECOND, 0);
        calendar.setTimeInMillis(System.currentTimeMillis());

        alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY,
                pendingIntent);
    }

    private void clearNotification() {
        try {
            this.clearSharedPreferences();
            PendingIntent pendingIntent = PendingIntent.getBroadcast(this.context, this.REQUEST_CODE, this.getIntent(),
                    this.INTENT_FLAG);
            AlarmManager alarmManager = (AlarmManager) this.context.getSystemService(Context.ALARM_SERVICE);
            alarmManager.cancel(pendingIntent);
        } catch (Exception e) {
            throw e;
        }
    }

    private Intent getIntent() {
        return new Intent(this.context, NotificationReceiver.class);
    }

    private Intent getIntent(String title, String subText, String text) {
        Intent intent = new Intent(this.context, NotificationReceiver.class);
        intent.putExtra("title", title);
        intent.putExtra("subText", subText);
        intent.putExtra("text", text);

        return intent;
    }

    private void setSharedPreferences() {
        SharedPreferences appPreferences = PreferenceManager.getDefaultSharedPreferences(this.context);
        SharedPreferences.Editor editor = appPreferences.edit();
        editor.putString("title", this.title);
        editor.putString("subText", this.subText);
        editor.putString("text", this.text);
        editor.putInt("hour", this.hour);
        editor.putInt("minute", this.minute);
        editor.commit();
    }

    private void clearSharedPreferences() {
        SharedPreferences appPreferences = PreferenceManager.getDefaultSharedPreferences(this.context);
        appPreferences.edit().clear().commit();
    }
}
