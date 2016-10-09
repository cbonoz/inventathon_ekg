package com.ekg.www.inventathon.fragments;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.speech.tts.TextToSpeech;
import android.support.v4.app.Fragment;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.ekg.www.inventathon.R;
import com.google.android.gms.maps.model.LatLng;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import static android.R.attr.alpha;
import static com.google.android.gms.wearable.DataMap.TAG;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 */
public class CPRFragment extends Fragment {
    private Vibrator v;
    private TextToSpeech t1;

    private Button cardiacButton;
    private TextView heartTextView;
    private static final String name = "Chris";
    private LatLng userLocation = new LatLng(-105, 54);

    private final String CPR_VOICE_MESSAGE = "I may be in trouble, Check my phone to help.";
    private final String CPR_TEXT_MESSAGE =
            name + " may need help. Current has a critically low heart rate, at location: "
                    + userLocation.toString();
    private static final List<String> PHONE_NUMBERS = Arrays.asList("5109266842", "8189160713");
    private static final int VIBRATE_DURATION_MS = 500;

    public CPRFragment() {
        // Required empty public constructor

    }

    private final SmsManager sms = SmsManager.getDefault();

    private void sendTextMessage(List<String> phoneNumbers, String message) {

        for(int i=0;i<phoneNumbers.size();i++) {
            try {
                sms.sendTextMessage(phoneNumbers.get(i), null, message, null, null);
            }
            catch(IllegalArgumentException e)
            {

            }
        }
    }

    private Timer timer;
    private TimerTask timerTask;

    public void onPause(){
        super.onPause();
        stopTimer();
    }

    private boolean timerStarted = false;
    private final int ONE_SEC = 1000;
    private int lastHeartValue = 65;

    private Handler handler = new Handler();

    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            if (Math.random() > .5) {
                lastHeartValue++;
            } else {
                lastHeartValue--;
            }
            if (lastHeartValue > 70) {
                lastHeartValue = 70;
            } else if (lastHeartValue < 60) {
                lastHeartValue = 60;
            }
            heartTextView.setText(lastHeartValue + "");
            handler.postDelayed(this, ONE_SEC);
        }
    };




    private void startTimer() {
        if (!timerStarted) {
            handler.postDelayed(runnable, ONE_SEC);
        }
    }

    private void stopTimer() {
        if (timerStarted) {
            handler.removeCallbacks(runnable);
        }
    }

    private void toggleTimer() {
        if (timerStarted) {
            stopTimer();
        } else {
            startTimer();
        }
    }


    private void alertAndVibrateAndText() {
        Toast.makeText(getActivity(), CPR_VOICE_MESSAGE,Toast.LENGTH_SHORT).show();
        t1.speak(CPR_VOICE_MESSAGE, TextToSpeech.QUEUE_FLUSH, null);
        v.vibrate(VIBRATE_DURATION_MS);
        sendTextMessage(PHONE_NUMBERS, CPR_VOICE_MESSAGE);
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View cprView = inflater.inflate(R.layout.fragment_cpr, container, false);

        t1 = new TextToSpeech(getActivity(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status != TextToSpeech.ERROR) {
                    t1.setLanguage(Locale.US);
                }
            }
        });

        v = (Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE);
        cardiacButton = (Button) cprView.findViewById(R.id.cardiac_button);
        cardiacButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "cardiac button clicked");
                toggleTimer();
                alertAndVibrateAndText();
            }
        });

        heartTextView = (TextView) cprView.findViewById(R.id.heart_text_view);
        heartTextView.setTextColor(Color.argb(alpha, 255, 0, 0));
        heartTextView.setText("0");

        timer = new Timer();
        timerStarted = false;
        startTimer();

        // Inflate the layout for this fragment
        return cprView;
    }

}
