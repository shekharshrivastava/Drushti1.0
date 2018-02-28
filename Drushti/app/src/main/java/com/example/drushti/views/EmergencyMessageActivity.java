package com.example.drushti.views;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.telephony.SmsManager;
import android.widget.TextView;
import android.widget.Toast;

import com.example.drushti.R;
import com.example.drushti.utils.LocalTextToSpeech;

import java.util.ArrayList;
import java.util.List;

public class EmergencyMessageActivity extends AppCompatActivity implements SensorEventListener {

    int count = 0;
    private boolean init;
    private Sensor mAccelerometer;
    private SensorManager mSensorManager;
    private float x1, x2, x3;
    private static final float ERROR = (float) 7.0;

    private TextView counter;
    private ArrayList<String> MobNumber;
    private SharedPreferences sPrefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emergency_message);
        sPrefs = PreferenceManager.getDefaultSharedPreferences(this);

        MobNumber = new ArrayList<>();
        int size = sPrefs.getInt("size", 0);
        if (size > 0) {
            for (int j = 0; j < size; j++) {
                MobNumber.add(sPrefs.getString("val" + j, null));
            }
        }else{
            LocalTextToSpeech._INSTANCE.speak("Add Number to emergency contact first", TextToSpeech.SUCCESS, null);

        }

        counter = (TextView) findViewById(R.id.counter);
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        List listOfSensorsOnDevice = mSensorManager.getSensorList(Sensor.TYPE_ALL);
     /*   for (int i = 0; i < listOfSensorsOnDevice.size(); i++) {
            if (listOfSensorsOnDevice.get(i).getType() == Sensor.TYPE_ACCELEROMETER) {*/

//                Toast.makeText(this, "ACCELEROMETER sensor is available on device", Toast.LENGTH_SHORT).show();


        init = false;

        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);

          /*  } else {

                Toast.makeText(this, "ACCELEROMETER sensor is NOT available on device", Toast.LENGTH_SHORT).show();
            }*/
//        }


    }


    @Override
    public void onSensorChanged(SensorEvent e) {


        //Get x,y and z values
        float x, y, z;
        x = e.values[0];
        y = e.values[1];
        z = e.values[2];


        if (!init) {
            x1 = x;
            x2 = y;
            x3 = z;
            init = true;
        } else {

            float diffX = Math.abs(x1 - x);
            float diffY = Math.abs(x2 - y);
            float diffZ = Math.abs(x3 - z);

            //Handling ACCELEROMETER Noise
            if (diffX < ERROR) {

                diffX = (float) 0.0;
            }
            if (diffY < ERROR) {
                diffY = (float) 0.0;
            }
            if (diffZ < ERROR) {

                diffZ = (float) 0.0;
            }


            x1 = x;
            x2 = y;
            x3 = z;


            //Horizontal Shake Detected!
            if (diffX > diffY) {
                count = count + 1;

                if (count == 4) {
                    counter.setText("Shake Count : " + count);
                    count = 0;
                    for (int i = 0; i < MobNumber.size(); i++) {
                        String message = "I am in trouble please contact me ";
                        String tempMobileNumber = MobNumber.get(i).toString();
                        MultipleSMS(tempMobileNumber, message);
                    }
                    Toast.makeText(EmergencyMessageActivity.this, "Sending message", Toast.LENGTH_SHORT).show();

                }
            }

        }


    }


    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        //Noting to do!!
    }

    //Register the Listener when the Activity is resumed
    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_STATUS_ACCURACY_LOW);
    }

    //Unregister the Listener when the Activity is paused
    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(this);
    }

    private void MultipleSMS(String phoneNumber, String message) {
        String SENT = "SMS_SENT";
        String DELIVERED = "SMS_DELIVERED";

        PendingIntent sentPI = PendingIntent.getBroadcast(this, 0, new Intent(
                SENT), 0);

        PendingIntent deliveredPI = PendingIntent.getBroadcast(this, 0,
                new Intent(DELIVERED), 0);

        // ---when the SMS has been sent---
        registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context arg0, Intent arg1) {
                switch (getResultCode()) {
                    case Activity.RESULT_OK:
                        ContentValues values = new ContentValues();
                        for (int i = 0; i < MobNumber.size() - 1; i++) {
                            values.put("address", MobNumber.get(i).toString());
                            // txtPhoneNo.getText().toString());
                            values.put("body", "I need help please contact me !!!");


                        }
                        getContentResolver().insert(
                                Uri.parse("content://sms/sent"), values);
                        Toast.makeText(getBaseContext(), "SMS sent",
                                Toast.LENGTH_SHORT).show();
                        break;
                    case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                        Toast.makeText(getBaseContext(), "Generic failure",
                                Toast.LENGTH_SHORT).show();
                        break;
                    case SmsManager.RESULT_ERROR_NO_SERVICE:
                        Toast.makeText(getBaseContext(), "No service",
                                Toast.LENGTH_SHORT).show();
                        break;
                    case SmsManager.RESULT_ERROR_NULL_PDU:
                        Toast.makeText(getBaseContext(), "Null PDU",
                                Toast.LENGTH_SHORT).show();
                        break;
                    case SmsManager.RESULT_ERROR_RADIO_OFF:
                        Toast.makeText(getBaseContext(), "Radio off",
                                Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        }, new IntentFilter(SENT));

        // ---when the SMS has been delivered---
        registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context arg0, Intent arg1) {
                switch (getResultCode()) {
                    case Activity.RESULT_OK:
                        Toast.makeText(getBaseContext(), "SMS delivered",
                                Toast.LENGTH_SHORT).show();
                        break;
                    case Activity.RESULT_CANCELED:
                        Toast.makeText(getBaseContext(), "SMS not delivered",
                                Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        }, new IntentFilter(DELIVERED));

        SmsManager sms = SmsManager.getDefault();
        sms.sendTextMessage(phoneNumber, null, message, sentPI, deliveredPI);
    }
}