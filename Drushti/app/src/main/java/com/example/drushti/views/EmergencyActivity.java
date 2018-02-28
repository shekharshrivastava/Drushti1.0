package com.example.drushti.views;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.speech.tts.TextToSpeech;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.widget.EditText;
import android.widget.Toast;

import com.example.drushti.R;
import com.example.drushti.utils.LocalTextToSpeech;
import com.example.drushti.utils.PermissionUtils;
import com.example.drushti.utils.SimpleGestureFilter;

import java.util.ArrayList;

public class EmergencyActivity extends AppCompatActivity implements SimpleGestureFilter.SimpleGestureListener, TextWatcher {

    private EditText phoneNumberET;
    private SimpleGestureFilter detector;
    private Vibrator vb;
    private ArrayList<String> phoneArrayList;
    private SharedPreferences sPrefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emergency);
        sPrefs = PreferenceManager.getDefaultSharedPreferences(this);

        phoneArrayList = new ArrayList<>();
        int size = sPrefs.getInt("size", 0);

        for (int j = 0; j < size; j++) {
            phoneArrayList.add(sPrefs.getString("val" + j, null));
        }

        phoneNumberET = (EditText) findViewById(R.id.numberET);
        phoneNumberET.addTextChangedListener(this);
        LocalTextToSpeech._INSTANCE.speak("Enter Number", TextToSpeech.SUCCESS, null);


        if (PermissionUtils.getInstance().checkPermission(getApplicationContext(), Manifest.permission.VIBRATE)) {
            vb = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        } else {
            PermissionUtils.getInstance().requestPermission(getApplicationContext(), EmergencyActivity.this, Manifest.permission.VIBRATE, PermissionUtils.PERMISSION_VIBRATE);
        }
        // Detect touched area
        detector = new SimpleGestureFilter(this, this);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent me) {
        // Call onTouchEvent of SimpleGestureFilter class
        this.detector.onTouchEvent(me);
        return super.dispatchTouchEvent(me);
    }

    @Override
    public void onSwipe(int direction) {
        switch (direction) {
            case SimpleGestureFilter.SWIPE_RIGHT:
                String phoneNumber = phoneNumberET.getText().toString();
                if (!phoneNumber.isEmpty()) {
                    phoneArrayList.add(phoneNumber);
                    String str = "Swipe Right";
                    SharedPreferences.Editor sEdit = sPrefs.edit();
                    for (int i = 0; i < phoneArrayList.size(); i++) {
                        sEdit.putString("val" + i, phoneArrayList.get(i));
                    }
                    sEdit.putInt("size", phoneArrayList.size());
                    sEdit.commit();
                    finish();
                } else {
                    LocalTextToSpeech._INSTANCE.speak("Please enter the number to continue", TextToSpeech.SUCCESS, null);
                }
                break;
        }
    }


    @Override
    public void onDoubleTap() {
        Toast.makeText(this, "Double Tap", Toast.LENGTH_SHORT).show();
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case PermissionUtils.PERMISSION_VIBRATE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    vb = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                } else {
                    if (!shouldShowRequestPermissionRationale(Manifest.permission.VIBRATE)) {
                        PermissionUtils.openAppInfoSetting(getApplicationContext());
                    } else {
                        Toast.makeText(EmergencyActivity.this, "Permission is not granted", Toast.LENGTH_LONG).show();
                    }
                }
                break;
        }
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        if (charSequence.length() == 10) {
            LocalTextToSpeech._INSTANCE.speak("Close the keyboard and swipe right to save your emergency contact", TextToSpeech.SUCCESS, null);
        }
    }

    @Override
    public void afterTextChanged(Editable editable) {

    }
}
