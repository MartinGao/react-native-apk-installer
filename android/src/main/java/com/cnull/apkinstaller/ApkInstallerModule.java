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
  public void install(String path, String activity) {
    Log.i("APK_INSTALLER", "install " + path);
    String cmd = "chmod 777 " + path;
    try {
        Runtime.getRuntime().exec(cmd);
    } catch (Exception e) {
        e.printStackTrace();
    }

    String cmd = "su pm install -r " + path + " && " + "su am start -n " + activity
    Log.i("APK_INSTALLER", cmd);
    ProcessBuilder pb = new ProcessBuilder(cmd);
    pb.redirectOutput(Redirect.INHERIT);
    pb.redirectError(Redirect.INHERIT);
    Process process = pb.start();

    // try {

    //   Process processInstall = Runtime.getRuntime().exec(cmd);
    //   processInstall.waitFor();
    //   Log.i("APK_INSTALLER", "Finish");
      
    // } catch (Exception ex) {
    //   Log.i("APK_INSTALLER", "ERROR Could not pm install", ex);
    // }
  }
}

// pm install -r data/data/com.ruijiahospitalnativev4/cache/com.example.app.1553600357911.apk && am start -n com.ruijiahospitalnativev4/com.ruijiahospitalnativev4.MainActivity
// am start -n com.ruijiahospitalnativev4/com.ruijiahospitalnativev4.MainActivity