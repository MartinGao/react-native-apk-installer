package com.cnull.apkinstaller;

import android.widget.Toast;

import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;

import java.util.HashMap;
import java.util.Map;
import java.io.File;

import java.lang.*; 
import java.io.*; 

import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.os.Bundle;
import android.app.Activity;

public class ApkInstallerModule extends ReactContextBaseJavaModule {
  private ReactApplicationContext _context = null;

  // private static final String DURATION_SHORT_KEY = "SHORT";
  // private static final String DURATION_LONG_KEY = "LONG";

  public ApkInstallerModule(ReactApplicationContext reactContext) {
    super(reactContext);
    _context = reactContext;
  }

  @Override
  public String getName() {
    return "ApkInstaller";
  }

  @Override
  public Map<String, Object> getConstants() {
    final Map<String, Object> constants = new HashMap<>();
    // constants.put(DURATION_SHORT_KEY, Toast.LENGTH_SHORT);
    // constants.put(DURATION_LONG_KEY, Toast.LENGTH_LONG);
    return constants;
  }

  @ReactMethod
  // public void show(String message, int duration) {
  public void test(String message) {
    Toast.makeText(getReactApplicationContext(), message, Toast.LENGTH_LONG).show();
  }

  @ReactMethod
  public void getExtra() {
    Activity activity = _context.getCurrentActivity();
    Bundle extras = activity.getIntent().getExtras();
    if (extras != null) {
      if (extras.containsKey ("path") ) {
        Log.i("APK_INSTALLER", "path : " + extras.getString ("path"));
      } 

      if (extras.containsKey ("activity")) {
        Log.i("APK_INSTALLER", "activity : " + extras.getString ("activity"));
      }
    } else {
    }
  }

  public execCommand(String cmd) {
    Process processSU = Runtime.getRuntime().exec(cmd);
    BufferedReader bufferedReader =new BufferedReader(new InputStreamReader(processSU.getInputStream()));
    String line;
    while ((line=bufferedReader.readLine()) != null) {
      Log.i("APK_INSTALLER", "LOG" + line);
    }
    int result = processSU.waitFor();
    Log.i("APK_INSTALLER", "FINISH " + String.valueOf(result));
  }

  @ReactMethod
  public void install(String path, String activity) {
    Log.i("APK_INSTALLER", "install " + path);
    String cmd = "chmod 777 " + path;
    try {
        Runtime.getRuntime().exec(cmd);
    } catch (Exception e) {
        e.printStackTrace();
    }

    try {
      String installCommand = "su -c pm install -r -d " + path;
      String restartActivityCommand = "su -c am start -n " + activity;
      this.execCommand(installCommand);
      this.execCommand(restartActivityCommand);
    } catch (Exception ex) {
      Log.i("APK_INSTALLER", "ERROR Could not pm install", ex);
    }
    
  }
}

// pm install -r data/data/com.ruijiahospitalnativev4/cache/com.example.app.1553600357911.apk && am start -n com.ruijiahospitalnativev4/com.ruijiahospitalnativev4.MainActivity
// am start -n com.ruijiahospitalnativev4/com.ruijiahospitalnativev4.MainActivity

// adb shell am start -a android.settings.SETTINGS && 
// adb shell am start -n com.ruijiahospitalnativev4/com.ruijiahospitalnativev4.MainActivity -e path data/data/com.ruijiahospitalnativev4/cache/com.example.app.1553600357911.apk -e activity com.ruijiahospitalnativev4/com.ruijiahospitalnativev4.MainActivity