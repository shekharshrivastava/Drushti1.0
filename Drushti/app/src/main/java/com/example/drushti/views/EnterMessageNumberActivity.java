package com.example.drushti.views;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Vibrator;
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

public class EnterMessageNumberActivity extends AppCompatActivity implements SimpleGestureFilter.SimpleGestureListener {

    private EditText etNumber;

    private SimpleGestureFilter detector;
    private Vibrator vb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_message);
        if (PermissionUtils.getInstance().checkPermission(getApplicationContext(), Manifest.permission.VIBRATE)) {
            vb = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        } else {
            PermissionUtils.getInstance().requestPermission(getApplicationContext(), EnterMessageNumberActivity.this, Manifest.permission.VIBRATE, PermissionUtils.PERMISSION_VIBRATE);
        }
        // Detect touched area
        detector = new SimpleGestureFilter(this, this);

        etNumber = (EditText) findViewById(R.id.numberEditText);

        LocalTextToSpeech._INSTANCE.speak("Please Enter 10 digit number to send message", TextToSpeech.SUCCESS, null);

        init();
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent me) {
        // Call onTouchEvent of SimpleGestureFilter class
        this.detector.onTouchEvent(me);
        return super.dispatchTouchEvent(me);
    }

    private void init() {
        etNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length() > i) {
                    char name = charSequence.charAt(i);
                    LocalTextToSpeech._INSTANCE.speak(String.valueOf(name), TextToSpeech.SUCCESS, null);
                } else {
//                    LocalTextToSpeech._INSTANCE.speak("Back", TextToSpeech.SUCCESS, null);
                }
                if (charSequence.length() == 10) {
                    LocalTextToSpeech._INSTANCE.speak("Close the keyboard and swipe right to enter message", TextToSpeech.SUCCESS, null);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });


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
                if (!etNumber.getText().toString().isEmpty()) {
                    Intent intent = new Intent(this, EnterMessageActivity.class);
                    intent.putExtra("number", etNumber.getText().toString());
                    startActivity(intent);
                    finish();
                } else {
                    LocalTextToSpeech._INSTANCE.speak("Please enter the number to go further", TextToSpeech.SUCCESS, null);

                }
                break;
            case SimpleGestureFilter.SWIPE_LEFT:
                str = "Swipe Left";
                finish();
                break;


        }
    }

    @Override
    public void onDoubleTap() {

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
                        Toast.makeText(EnterMessageNumberActivity.this, "Permission is not granted", Toast.LENGTH_LONG).show();
                    }
                }
                break;

            case PermissionUtils.PERMISSION_SEND_SMS:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                } else {
                    if (!shouldShowRequestPermissionRationale(Manifest.permission.VIBRATE)) {
                        PermissionUtils.openAppInfoSetting(getApplicationContext());
                    } else {
                        Toast.makeText(EnterMessageNumberActivity.this, "Permission is not granted", Toast.LENGTH_LONG).show();
                    }
                }
                break;
        }
    }
}
