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

import com.facebook.react.bridge.*;

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
  public void getExtraFromActivity(Promise promise) {
    Activity activity = _context.getCurrentActivity();
    Bundle extras = activity.getIntent().getExtras();
    String path = "";
    String targetActivity = "";
    if (extras != null) {
      if (extras.containsKey ("path") ) {
        path = extras.getString("path");
        Log.i("APK_INSTALLER", "path : " + extras.getString("path"));
      } 
      if (extras.containsKey ("activity")) {
        targetActivity = extras.getString("activity");
        Log.i("APK_INSTALLER", "activity : " + extras.getString("activity"));
      }
    }
    WritableMap map = new WritableNativeMap();
    map.putString("path", path);
    map.putString("activity", targetActivity);
    promise.resolve(map);
  }

  public void execCommandSecond(String cmd) {
    // su -c
    // String finalCmd = "su -c \"" + cmd +"\"";
    String finalCmd = "su -s sh -c /data/data/com.ruijiahospitalnativev4/cache/test.sh";
    // Log.i("APK_INSTALLER", "execCommandSecond cmd -> " + cmd);
    Log.i("APK_INSTALLER", "execCommandSecond finalCmd -> " + finalCmd);
    try {
      Process processSU = Runtime.getRuntime().exec(finalCmd);
      
      String data = null;

      BufferedReader ie = new BufferedReader(new InputStreamReader(processSU.getErrorStream()));
      BufferedReader in = new BufferedReader(new InputStreamReader(processSU.getInputStream()));
      
      String error = null;
      while ((error = ie.readLine()) != null && !error.equals("null")) { data += error + "\n"; }
      String line = null;
      while ((line = in.readLine()) != null && !line.equals("null")) { data += line + "\n"; }
      Log.i("APK_INSTALLER", "data ->  " + data);

      int result = processSU.waitFor();
      Log.i("APK_INSTALLER", "FINISH " + String.valueOf(result));
      if (result == 0) { Log.i("APK_INSTALLER", "FINISH OS error code 0: Success"); }
      else if (result == 1) { Log.i("APK_INSTALLER", "FINISH OS error code 1: Operation not permitted"); }
      else { Log.i("APK_INSTALLER", "FINISH 其他错误"); }
    } catch (Exception ex) {
      Log.i("APK_INSTALLER", "ERROR Could not execCommand", ex);
    }
  }

  public void execCommand(String cmd) {
    // su -c\
    Log.i("APK_INSTALLER", "execCommand origin -> " + cmd);
    String[] cmdArr = new String[] { "su", "-c", cmd };
    // String finalCmd = String.join(" ", cmdArr);
    // Log.i("APK_INSTALLER", "execCommand finalCmd -> " + finalCmd);
    try {
      
      // Process processSU = Runtime.getRuntime().exec(finalCmd);
      Process processSU = Runtime.getRuntime().exec(cmdArr);

      String data = null;

      BufferedReader ie = new BufferedReader(new InputStreamReader(processSU.getErrorStream()));
      BufferedReader in = new BufferedReader(new InputStreamReader(processSU.getInputStream()));
      
      String error = null;
      while ((error = ie.readLine()) != null && !error.equals("null")) { data += error + "\n"; }
      String line = null;
      while ((line = in.readLine()) != null && !line.equals("null")) { data += line + "\n"; }
      Log.i("APK_INSTALLER", "data ->  " + data);
      
      int result = processSU.waitFor();
      Log.i("APK_INSTALLER", "FINISH " + String.valueOf(result));
      if (result == 0) { Log.i("APK_INSTALLER", "FINISH OS error code 0: Success"); }
      else if (result == 1) { Log.i("APK_INSTALLER", "FINISH OS error code 1: Operation not permitted | About to retry"); this.execCommandSecond(cmd); }
      else { Log.i("APK_INSTALLER", "FINISH 其他错误"); }
    } catch (Exception ex) {
      Log.i("APK_INSTALLER", "ERROR Could not execCommand", ex);
    }
  }

  @ReactMethod
  public void installWithoutCallback(String path) {
    Log.i("APK_INSTALLER", "installWithoutCallback " + path);
    String cmd = "chmod 777 " + path;
    try {
        Runtime.getRuntime().exec(cmd);
    } catch (Exception e) {
        e.printStackTrace();
    }
    try {
      String installCommand = "pm install -r -d " + path;
      this.execCommand(installCommand);
      Log.i("APK_INSTALLER", "installWithoutCallback " + "Finished. About to Reboot");
      this.execCommand("reboot");
    } catch (Exception ex) {
      Log.i("APK_INSTALLER", "ERROR Could not pm install", ex);
    }
  }

  @ReactMethod
  public void install(String path, String callbackActivity) {
    Log.i("APK_INSTALLER", "install " + path);
    String cmd = "chmod 777 " + path;
    try {
        Runtime.getRuntime().exec(cmd);
    } catch (Exception e) {
        e.printStackTrace();
    }
    try {
      String installCommand = "pm install -r -d " + path;
      String callbackActivityCommand = "am start -n " + callbackActivity;
      this.execCommand(installCommand);
      Thread.sleep(30000);
      Log.i("APK_INSTALLER", "install " + "Finished. About to Reboot");
      this.execCommand("reboot");
      // this.execCommand(callbackActivityCommand);
    } catch (Exception ex) {
      Log.i("APK_INSTALLER", "ERROR Could not pm install", ex);
    }
  }

  @ReactMethod
  public void openInstaller(String path, String installerActivity, String callbackActivity) {
    Log.i("APK_INSTALLER", "install " + path);
    String cmd = "chmod 777 " + path;
    try {
        Runtime.getRuntime().exec(cmd);
    } catch (Exception e) {
        e.printStackTrace();
    }
    try {
      String pathExtra = "-e path " + path;
      String callbackActivityExtra = "-e activity " + callbackActivity;
      String installerActivityCommand = "am start -n " + installerActivity + " " + pathExtra + " " + callbackActivityExtra;
      // this.execCommand(installCommand);
      this.execCommand(installerActivityCommand);
    } catch (Exception ex) {
      Log.i("APK_INSTALLER", "ERROR Could not pm install", ex);
    }
  }
}

// pm install -r data/data/com.ruijiahospitalnativev4/cache/com.example.app.1553600357911.apk && am start -n com.ruijiahospitalnativev4/com.ruijiahospitalnativev4.MainActivity
// am start -n com.ruijiahospitalnativev4/com.ruijiahospitalnativev4.MainActivity

// adb shell am start -a android.settings.SETTINGS && 
// adb shell am start -n com.ruijiahospitalnativev4/com.ruijiahospitalnativev4.MainActivity -e path data/data/com.ruijiahospitalnativev4/cache/com.example.app.1553600357911.apk -e activity com.ruijiahospitalnativev4/com.ruijiahospitalnativev4.MainActivity
// adb shell am start -n com.ruijiahospitalnativeinstaller/com.ruijiahospitalnativeinstaller.MainActivity -e path data/data/com.ruijiahospitalnativev4/cache/com.example.app.1553600357911.apk -e activity com.ruijiahospitalnativev4/com.ruijiahospitalnativev4.MainActivity


// echo "am start -n com.ruijiahospitalnativeinstaller/com.ruijiahospitalnativeinstaller.MainActivity" > /data/data/com.ruijiahospitalnativev4/cache/haha.sh

// echo "am start -n com.ruijiahospitalnativeinstaller/com.ruijiahospitalnativeinstaller.MainActivity -e path /data/data/com.ruijiahospitalnativev4/cache/com.example.app.1560329820685.apk -e activity com.ruijiahospitalnativev4/com.ruijiahospitalnativev4.MainActivity" > /data/data/com.ruijiahospitalnativev4/cache/test.sh 