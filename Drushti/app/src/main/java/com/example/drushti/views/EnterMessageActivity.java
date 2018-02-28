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
import android.telephony.SmsManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.widget.EditText;
import android.widget.Toast;

import com.example.drushti.utils.LocalTextToSpeech;
import com.example.drushti.utils.PermissionUtils;
import com.example.drushti.R;
import com.example.drushti.utils.SimpleGestureFilter;

public class EnterMessageActivity extends AppCompatActivity implements SimpleGestureFilter.SimpleGestureListener {

    private EditText etMessage;
    private SimpleGestureFilter detector;
    private Vibrator vb;
    private String recipentNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter_message);
        Intent intent = getIntent();
        recipentNumber = intent.getStringExtra("number");
        if (PermissionUtils.getInstance().checkPermission(getApplicationContext(), Manifest.permission.VIBRATE)) {
            vb = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        } else {
            PermissionUtils.getInstance().requestPermission(getApplicationContext(), EnterMessageActivity.this, Manifest.permission.VIBRATE, PermissionUtils.PERMISSION_VIBRATE);
        }
        // Detect touched area
        detector = new SimpleGestureFilter(this, this);

        etMessage = (EditText) findViewById(R.id.messageEditText);

        LocalTextToSpeech._INSTANCE.speak("Close the keyboard and swipe right to send your message once you are done", TextToSpeech.SUCCESS, null);

        init();
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent me) {
        // Call onTouchEvent of SimpleGestureFilter class
        this.detector.onTouchEvent(me);
        return super.dispatchTouchEvent(me);
    }

    private void init() {
        etMessage.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (etMessage.getText().length() == 480) {
                    LocalTextToSpeech._INSTANCE.speak("Max length of your message is reached please close the keyboard and swipe right to send", TextToSpeech.SUCCESS, null);

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
                sendMessage();
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

    private void sendMessage() {
        String message = etMessage.getText().toString();
        if (PermissionUtils.getInstance().checkPermission(this, Manifest.permission.SEND_SMS)) {
            try {
                SmsManager smsMgrVar = SmsManager.getDefault();
                smsMgrVar.sendTextMessage(recipentNumber, null, message, null, null);
                LocalTextToSpeech._INSTANCE.speak("Message Sent Successfully", TextToSpeech.SUCCESS, null);
                Toast.makeText(getApplicationContext(), "Message Sent Successfully",
                        Toast.LENGTH_LONG).show();
                finish();
            } catch (Exception ErrVar) {
                LocalTextToSpeech._INSTANCE.speak("Please try again", TextToSpeech.SUCCESS, null);
                Toast.makeText(getApplicationContext(), ErrVar.getMessage().toString(),
                        Toast.LENGTH_LONG).show();
                ErrVar.printStackTrace();
            }
        } else {
            PermissionUtils.getInstance().requestPermission(getApplicationContext(), EnterMessageActivity.this, Manifest.permission.SEND_SMS, PermissionUtils.PERMISSION_SEND_SMS);
        }
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
                        Toast.makeText(EnterMessageActivity.this, "Permission is not granted", Toast.LENGTH_LONG).show();
                    }
                }
                break;

            case PermissionUtils.PERMISSION_SEND_SMS:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    sendMessage();
                } else {
                    if (!shouldShowRequestPermissionRationale(Manifest.permission.VIBRATE)) {
                        PermissionUtils.openAppInfoSetting(getApplicationContext());
                    } else {
                        Toast.makeText(EnterMessageActivity.this, "Permission is not granted", Toast.LENGTH_LONG).show();
                    }
                }
                break;
        }
    }
}
