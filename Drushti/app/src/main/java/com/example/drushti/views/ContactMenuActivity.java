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

public class ContactMenuActivity extends AppCompatActivity implements SimpleGestureFilter.SimpleGestureListener {
    private SimpleGestureFilter detector;
    private Vibrator vb;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_menu);
        if (PermissionUtils.getInstance().checkPermission(getApplicationContext(), Manifest.permission.VIBRATE)) {
            vb = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        } else {
            PermissionUtils.getInstance().requestPermission(getApplicationContext(), ContactMenuActivity.this, Manifest.permission.VIBRATE, PermissionUtils.PERMISSION_VIBRATE);
        }
        // Detect touched area
        detector = new SimpleGestureFilter(this, this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        LocalTextToSpeech._INSTANCE.speak("Contact Menu", TextToSpeech.SUCCESS, null);
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
                Intent addContactActivity = new Intent(this, EnterNumberActivity.class);
                startActivity(addContactActivity);
                break;
            case SimpleGestureFilter.SWIPE_LEFT:
                str = "Swipe Left";
                finish();
                break;
            case SimpleGestureFilter.SWIPE_DOWN:
                str = "Swipe Down";
                Intent deleteContactIntent = new Intent(this, DeleteContactActivity.class);
                startActivity(deleteContactIntent);
                break;
            case SimpleGestureFilter.SWIPE_UP:
                str = "Swipe Up";
                Intent contactListIntent = new Intent(this, ContactListActivity.class);
                startActivity(contactListIntent);
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
                        Toast.makeText(ContactMenuActivity.this, "Permission is not granted", Toast.LENGTH_LONG).show();
                    }
                }
                break;
        }
    }
}
