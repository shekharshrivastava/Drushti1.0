package com.example.drushti.views;

import android.content.Intent;
import android.gesture.Gesture;
import android.gesture.GestureLibraries;
import android.gesture.GestureLibrary;
import android.gesture.GestureOverlayView;
import android.gesture.Prediction;
import android.os.Bundle;
import android.os.Environment;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;

import com.example.drushti.utils.LocalTextToSpeech;
import com.example.drushti.R;

import java.io.File;
import java.util.ArrayList;


public class MainActivity extends AppCompatActivity implements GestureOverlayView.OnGesturePerformedListener {

    private GestureLibrary mGestureLibrary;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
    }

    private void init() {
        GestureOverlayView gestures = (GestureOverlayView) findViewById(R.id.gestures);
        //LocalTextToSpeech._INSTANCE.speak("Home. Draw character among c, m, and, e", TextToSpeech.SUCCESS, null);
        //LocalTextToSpeech._INSTANCE.speak("Draw character among c, m, and e", TextToSpeech.SUCCESS, null);
        File gesturesFile = new File(Environment.getExternalStorageDirectory() + File.separator + "gestures");
        if (Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED) && gesturesFile.exists()) {
            mGestureLibrary = GestureLibraries.fromFile(gesturesFile);
        } else {
            mGestureLibrary = GestureLibraries.fromRawResource(this, R.raw.gesture);
        }
        if (mGestureLibrary.load()) {
            gestures.addOnGesturePerformedListener(this);
            gestures.setGestureVisible(true);
        } else {
            gestures.setGestureVisible(false);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        LocalTextToSpeech._INSTANCE.speak("Home. Draw, a, character, among, c, m, and, e", TextToSpeech.SUCCESS, null);
    }

    @Override
    public void onGesturePerformed(GestureOverlayView overlay, Gesture gesture) {
        ArrayList<Prediction> predictions = mGestureLibrary.recognize(gesture);
        if (predictions.size() > 0) {
            Prediction prediction = predictions.get(0);
            if (prediction.score > 3.0) {
                String name = prediction.name;
                switch (name.toLowerCase()) {
                    case "c":
                        Intent intent = new Intent(this, ContactActivity.class);
                        startActivity(intent);
                        break;

                    case "m":
                        Intent messageIntent = new Intent(this, MessageActivity.class);
                        startActivity(messageIntent);
                        break;

                    case "e":
                        Intent emergencyMsgIntent = new Intent(this, EmergencyMessageActivity.class);
                        startActivity(emergencyMsgIntent);
                        break;
                }
            }
        }
    }
}
