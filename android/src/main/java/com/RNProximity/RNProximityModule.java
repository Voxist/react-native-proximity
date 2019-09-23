package com.RNProximity;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import androidx.annotation.Nullable;
import android.util.Log;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.modules.core.DeviceEventManagerModule;

import java.util.HashMap;
import java.util.Map;

public class RNProximityModule extends ReactContextBaseJavaModule implements SensorEventListener {
    private static final String TAG = "RNProximityModule";
    private static final String KEY_PROXIMITY = "proximity";
    private static final String KEY_DISTANCE = "distance";
    private static final String KEY_EVENT_ON_SENSOR_CHANGE = "EVENT_ON_SENSOR_CHANGE";
    private static final String EVENT_ON_SENSOR_CHANGE = "onSensorChanged";
    private final ReactApplicationContext reactContext;

    private SensorManager mSensorManager;
    private Sensor mProximity;

    public RNProximityModule(ReactApplicationContext reactContext) {
        super(reactContext);
        this.reactContext = reactContext;
        mSensorManager = (SensorManager) reactContext.getSystemService(Context.SENSOR_SERVICE);
        mProximity = mSensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
        Log.i(TAG, "init");
    }

    public void sendEvent(String eventName, @Nullable WritableMap params) {
        if (this.reactContext.hasActiveCatalystInstance()) {
          Log.i(TAG, params.toHashMap().toString());
          this.reactContext.getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class).emit(eventName, params);
        } else {
          Log.i(TAG, "Waiting for CatalystInstance");
        }
      }

      @ReactMethod
      public void addListener() {
        Log.i(TAG, "add listener");

        mSensorManager.registerListener(this, mProximity, SensorManager.SENSOR_DELAY_NORMAL);
      }

      @ReactMethod
      public void removeListener() {
        mSensorManager.unregisterListener(this);
      }

    @Override
    public String getName() {
        return "RNProximity";
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        WritableMap params = Arguments.createMap();

        double distance = sensorEvent.values[0];
        double maximumRange = mProximity.getMaximumRange();
        boolean isNearDevice = distance < maximumRange;

        params.putBoolean(KEY_PROXIMITY, isNearDevice);
        params.putDouble(KEY_DISTANCE, distance);
        Log.i(TAG, EVENT_ON_SENSOR_CHANGE);

        sendEvent(EVENT_ON_SENSOR_CHANGE, params);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }
}
