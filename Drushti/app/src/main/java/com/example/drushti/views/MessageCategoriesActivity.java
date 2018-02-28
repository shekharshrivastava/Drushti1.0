package com.example.drushti.views;

import android.app.ProgressDialog;
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
import android.widget.ListView;

import com.example.drushti.R;
import com.example.drushti.utils.LocalTextToSpeech;

import java.io.File;
import java.util.ArrayList;

public class MessageCategoriesActivity extends AppCompatActivity implements GestureOverlayView.OnGesturePerformedListener{
        private static final int TYPE_INCOMING_MESSAGE = 2;
    private ListView messageList;
    private MessageListAdapter messageListAdapter;
    private ArrayList<Message> recordsStored;
    private ArrayList<Message> listInboxMessages;
    private ProgressDialog progressDialogInbox;
    private GestureLibrary mGestureLibrary;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_list);

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

//        break;
    }

    @Override
    protected void onResume() {
        super.onResume();
        LocalTextToSpeech._INSTANCE.speak("Message category. Draw, a, character, among, i, o, and, d", TextToSpeech.SUCCESS, null);
    }

    @Override
    public void onGesturePerformed(GestureOverlayView overlay, Gesture gesture) {
        ArrayList<Prediction> predictions = mGestureLibrary.recognize(gesture);
        if (predictions.size() > 0) {
            Prediction prediction = predictions.get(0);
            if (prediction.score > 3.0) {
                String name = prediction.name;
                switch (name.toLowerCase()) {
                    case "i":
                        Intent inboxIntent = new Intent(this, MessageInboxActivity.class);
                        inboxIntent.putExtra("messages","content://sms/inbox");
                        startActivity(inboxIntent);
                        break;

                    case "o":
                        Intent sentIntent = new Intent(this, MessageInboxActivity.class);
                        sentIntent.putExtra("messages","content://sms/sent");
                        startActivity(sentIntent);
                        break;

                    case "d":
                        Intent draftIntent = new Intent(this, MessageInboxActivity.class);
                        draftIntent.putExtra("messages","content://sms/draft");
                        startActivity(draftIntent);
                        break;
                }
            }
        }
    }
}