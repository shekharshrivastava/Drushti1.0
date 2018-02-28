package com.example.drushti.views;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Vibrator;
import android.speech.tts.TextToSpeech;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.widget.Toast;

import com.example.drushti.utils.LocalTextToSpeech;
import com.example.drushti.utils.PermissionUtils;
import com.example.drushti.R;
import com.example.drushti.utils.SimpleGestureFilter;

public class ContactActivity extends AppCompatActivity implements SimpleGestureFilter.SimpleGestureListener {
    private SimpleGestureFilter detector;
    private Vibrator vb;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact);
        if (PermissionUtils.getInstance().checkPermission(getApplicationContext(), Manifest.permission.VIBRATE)) {
            vb = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        } else {
            PermissionUtils.getInstance().requestPermission(getApplicationContext(), ContactActivity.this, Manifest.permission.VIBRATE, PermissionUtils.PERMISSION_VIBRATE);
        }
        // Detect touched area
        detector = new SimpleGestureFilter(this, this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        LocalTextToSpeech._INSTANCE.speak("Contact", TextToSpeech.SUCCESS, null);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent me) {
        // Call onTouchEvent of SimpleGestureFilter class
        this.detector.onTouchEvent(me);
        return super.dispatchTouchEvent(me);
    }

    @Override
    public void onSwipe(int direction) {
        String str = "";
        if (PermissionUtils.getInstance().checkPermission(getApplicationContext(), Manifest.permission.VIBRATE)) {
            vb.vibrate(100);
        }

        switch (direction) {
            case SimpleGestureFilter.SWIPE_RIGHT:
                str = "Swipe Right";
                LocalTextToSpeech._INSTANCE.speak("Dialer", TextToSpeech.SUCCESS, null);
                Intent intent = new Intent(this, DialerActivity.class);
                startActivity(intent);
                break;
            case SimpleGestureFilter.SWIPE_LEFT:
                str = "Swipe Left";
                finish();
                break;
            case SimpleGestureFilter.SWIPE_DOWN:
                str = "Swipe Down";
                LocalTextToSpeech._INSTANCE.speak("Emergency Numbers", TextToSpeech.SUCCESS, null);
                Intent EmergencyIntent = new Intent(this, EmergencyActivity.class);
                startActivity(EmergencyIntent);
                break;
            case SimpleGestureFilter.SWIPE_UP:
                str = "Swipe Up";
                Intent contactMenu = new Intent(this, ContactMenuActivity.class);
                startActivity(contactMenu);
                break;

        }
    }

    @Override
    public void onDoubleTap() {
        Toast.makeText(this, "Double Tap", Toast.LENGTH_SHORT).show();
    }

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
                        Toast.makeText(ContactActivity.this, "Permission is not granted", Toast.LENGTH_LONG).show();
                    }
                }
                break;
        }
    }
}
