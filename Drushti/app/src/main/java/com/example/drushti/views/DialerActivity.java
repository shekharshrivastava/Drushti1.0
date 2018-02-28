package com.example.drushti.views;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.drushti.utils.LocalTextToSpeech;
import com.example.drushti.utils.PermissionUtils;
import com.example.drushti.R;


public class DialerActivity extends AppCompatActivity implements TextWatcher, View.OnClickListener {

    private EditText numberText;
    private ImageView iv_call;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dialer);
        numberText = (EditText)findViewById(R.id.numberEditText);
        iv_call = (ImageView) findViewById(R.id.iv_call);
        iv_call.setOnClickListener(this);
        numberText.addTextChangedListener(this);

        if (!PermissionUtils.getInstance().checkPermission(getApplicationContext(), Manifest.permission.CALL_PHONE)) {
            PermissionUtils.getInstance().requestPermission(getApplicationContext(), DialerActivity.this, Manifest.permission.CALL_PHONE, PermissionUtils.PERMISSION_CALL_PHONE);
        }
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        if (charSequence.length() > i) {
            char name = charSequence.charAt(i);
            LocalTextToSpeech._INSTANCE.speak(String.valueOf(name), TextToSpeech.SUCCESS, null);
        } else {
            LocalTextToSpeech._INSTANCE.speak("Back", TextToSpeech.SUCCESS, null);
        }
    }

    @Override
    public void afterTextChanged(Editable editable) {
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_call:
                if (PermissionUtils.getInstance().checkPermission(getApplicationContext(), Manifest.permission.CALL_PHONE)) {
                    String numberString = numberText.getText().toString();
                    if (numberString.length() == 10) {
                        LocalTextToSpeech._INSTANCE.speak("Call, to, the person", TextToSpeech.SUCCESS, null);
                        Uri number = Uri.parse("tel:" + numberString);
                        Intent dial = new Intent(Intent.ACTION_CALL, number);
                        startActivity(dial);
                    } else {
                        LocalTextToSpeech._INSTANCE.speak("Invalid, phone, number", TextToSpeech.SUCCESS, null);
                    }
                } else {
                    PermissionUtils.getInstance().requestPermission(getApplicationContext(), DialerActivity.this, Manifest.permission.CALL_PHONE, PermissionUtils.PERMISSION_CALL_PHONE);
                }
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case PermissionUtils.PERMISSION_CALL_PHONE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                } else {
                    if (!shouldShowRequestPermissionRationale(Manifest.permission.CALL_PHONE)) {
                        PermissionUtils.openAppInfoSetting(getApplicationContext());
                    } else {
                        Toast.makeText(DialerActivity.this, "Permission is not granted", Toast.LENGTH_LONG).show();
                    }
                }
                break;
        }
    }
}
