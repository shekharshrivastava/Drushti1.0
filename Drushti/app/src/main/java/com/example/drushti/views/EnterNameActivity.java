package com.example.drushti.views;

import android.Manifest;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.provider.Contacts;
import android.provider.ContactsContract;
import android.speech.tts.TextToSpeech;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.widget.EditText;
import android.widget.Toast;

import com.example.drushti.utils.LocalTextToSpeech;
import com.example.drushti.utils.PermissionUtils;
import com.example.drushti.R;
import com.example.drushti.utils.SimpleGestureFilter;

public class EnterNameActivity extends AppCompatActivity implements SimpleGestureFilter.SimpleGestureListener {

    private EditText nameEditText;
    private String number;
    private Vibrator vb;
    private SimpleGestureFilter detector;
    private Uri updateUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter_name);
        Intent intent = getIntent();
        number = intent.getStringExtra("number");
        nameEditText = (EditText) findViewById(R.id.nameET);
        if (PermissionUtils.getInstance().checkPermission(getApplicationContext(), Manifest.permission.VIBRATE)) {
            vb = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        } else {
            PermissionUtils.getInstance().requestPermission(getApplicationContext(), EnterNameActivity.this, Manifest.permission.VIBRATE, PermissionUtils.PERMISSION_VIBRATE);
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
                String str = "Swipe Right";
                String inputName = nameEditText.getText().toString();
                if (!inputName.isEmpty()) {
                    addContact(inputName, number);
                    LocalTextToSpeech._INSTANCE.speak("Your Number is saved", TextToSpeech.SUCCESS, null);
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            // Do something after 2s = 2000ms
                            finish();
                        }
                    },1000);
                } else {
                    LocalTextToSpeech._INSTANCE.speak("Please enter the name to continue", TextToSpeech.SUCCESS, null);
                }
        }
    }


    @Override
    public void onDoubleTap() {
        Toast.makeText(this, "Double Tap", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case PermissionUtils.PERMISSION_VIBRATE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    vb = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                } else {
                    if (!shouldShowRequestPermissionRationale(Manifest.permission.VIBRATE)) {
                        PermissionUtils.openAppInfoSetting(getApplicationContext());
                    } else {
                        Toast.makeText(EnterNameActivity.this, "Permission is not granted", Toast.LENGTH_LONG).show();
                    }
                }
                break;
        }
    }


    private void addContact(String name, String phone) {
        ContentValues values = new ContentValues();
        values.put(Contacts.People.NUMBER, phone);
        values.put(Contacts.People.TYPE, ContactsContract.CommonDataKinds.Phone.TYPE_CUSTOM);
        values.put(Contacts.People.LABEL, name);
        values.put(Contacts.People.NAME, name);
        Uri dataUri = getContentResolver().insert(Contacts.People.CONTENT_URI, values);
        updateUri = Uri.withAppendedPath(dataUri, Contacts.People.Phones.CONTENT_DIRECTORY);
        values.clear();
        values.put(Contacts.People.Phones.TYPE, Contacts.People.TYPE_MOBILE);
        values.put(Contacts.People.NUMBER, phone);
        updateUri = getContentResolver().insert(updateUri, values);
    }


}

