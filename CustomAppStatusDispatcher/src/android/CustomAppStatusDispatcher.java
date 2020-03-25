package com.example.jaylee.myapplication;

import org.apache.cordova.CordovaPlugin;
import android.app.Activity;

public class CustomAppStatusDispatcher extends CordovaPlugin {
    @Override
    public void onResume(boolean multitasking) {
        // this.webView.sendJavascript();
    }

    @Override
    public void onPause(boolean multitasking) {

    }

    @Override
    public void onStop() {

    }
}