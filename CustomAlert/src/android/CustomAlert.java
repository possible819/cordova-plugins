package custom.plugin.alert;

import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CallbackContext;
import org.apache.cordova.PluginResult;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

public class CustomAlert extends CordovaPlugin {

    private static final String ACTION_ALERT = "alert";
    private static final String ACTION_DISMISS_ALERT = "dismissAlert";

    private static final String BUTTON_TYPE_DEFAULT = "DEFAULT";
    private static final String BUTTON_TYPE_CANCEL = "CANCEL";
    private static final String BUTTON_TYPE_DESTRUCTIVE = "DESTRUCTIVE";

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        String alertType = args.getString(0);
        String title = args.getString(1);
        String message = args.getString(2);
        JSONArray buttons = args.getJSONArray(3);
        boolean result;

        if (action.equals(ACTION_ALERT)) {
            this.alert(alertType, title, message, buttons, callbackContext);
            result = true;
        } else {
            result = false;
        }

        return result;
    }

    private void alert(String alertType, String title, String message, JSONArray buttons,
            final CallbackContext callbackContext) throws JSONException {
        AlertDialog.Builder builder = new AlertDialog.Builder(this.cordova.getActivity());
        if (!title.isEmpty())
            builder.setTitle(title);
        builder.setMessage(message);

        for (int i = 0; i < buttons.length(); i++) {
            JSONObject button = buttons.getJSONObject(i);
            final String name = button.getString("name");
            String type = button.getString("type");

            switch (type) {
                case BUTTON_TYPE_DEFAULT:
                    builder.setPositiveButton(name, new AlertDialog.OnClickListener() {
                        public void onClick(DialogInterface dialog, int idx) {
                            dialog.dismiss();
                            callbackContext.sendPluginResult(new PluginResult(PluginResult.Status.OK, name));

                        }
                    });
                    break;

                case BUTTON_TYPE_CANCEL:
                    builder.setNegativeButton(name, new AlertDialog.OnClickListener() {
                        public void onClick(DialogInterface dialog, int idx) {
                            dialog.dismiss();
                            callbackContext.sendPluginResult(new PluginResult(PluginResult.Status.OK, name));
                        }
                    });
                    break;

                case BUTTON_TYPE_DESTRUCTIVE:
                    builder.setNegativeButton(name, new AlertDialog.OnClickListener() {
                        public void onClick(DialogInterface dialog, int idx) {
                            dialog.dismiss();
                            callbackContext.sendPluginResult(new PluginResult(PluginResult.Status.OK, name));
                        }
                    });

                    break;

                default:
                    callbackContext.error("Button type " + type + " is available.");
            }
        }

        builder.show();
    }
}