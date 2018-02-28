package com.example.drushti.utils;

import android.content.Context;
import java.util.Locale;

/**
 * Created by vishal.patil on 10/12/2017.
 */

public class LocalTextToSpeech {
    public static android.speech.tts.TextToSpeech _INSTANCE;

    public static android.speech.tts.TextToSpeech getInstance(Context context) {
        if (_INSTANCE == null) {
            _INSTANCE = new android.speech.tts.TextToSpeech(context, new android.speech.tts.TextToSpeech.OnInitListener() {
                @Override
                public void onInit(int status) {
                    if (status != android.speech.tts.TextToSpeech.ERROR) {
                        _INSTANCE.setLanguage(Locale.ENGLISH);
                    }
                }
            });
        }

        return _INSTANCE;
    }
}
