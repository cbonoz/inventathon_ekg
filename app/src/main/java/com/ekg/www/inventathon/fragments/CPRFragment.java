package com.ekg.www.inventathon.fragments;

import android.content.Context;
import android.graphics.Color;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.CountDownTimer;
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
import static android.content.Context.AUDIO_SERVICE;
import static com.google.android.gms.wearable.DataMap.TAG;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 */
public class CPRFragment extends Fragment {
    private Vibrator v;
    private TextToSpeech t1;

    private Button cardiacButton;
    private Button cprButton;
    private TextView heartTextView;
    private String userName;

    private LatLng userLocation = new LatLng(-105, 54);

    private final String CPR_VOICE_MESSAGE = "I may be in trouble, Check my phone to help.";
    private String cprTextMessage;
    private static final List<String> PHONE_NUMBERS = Arrays.asList("5109266842", "8189160713");
    private static final int VIBRATE_DURATION_MS = 1000;

    public CPRFragment() {
        // Required empty public constructor
    }

    private final SmsManager sms = SmsManager.getDefault();

    private void sendTextMessage(List<String> phoneNumbers, String message) {
        Log.d(TAG, "sendTextMessage");
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
    }

    private boolean heartTimerStarted = false;
    private boolean voiceStarted = false;
    private final int ONE_SEC_MS = 1000;
    private final int COUNT_DOWN_SECS = 15;
    private long lastHeartValue = 65;

    private Handler handler = new Handler();

    private Runnable heartRateRunnable = new Runnable() {
        @Override
        public void run() {
            double randVal = Math.random();
            if (randVal > .75) {
                lastHeartValue++;
            } else if (randVal < .25) {
                lastHeartValue--;
            }
            if (lastHeartValue > 70) {
                lastHeartValue = 70;
            } else if (lastHeartValue < 60) {
                lastHeartValue = 60;
            }
            heartTextView.setText(lastHeartValue + "");
            handler.postDelayed(this, ONE_SEC_MS);
        }
    };

    private CountDownTimer alertTimer = new CountDownTimer(COUNT_DOWN_SECS * ONE_SEC_MS, ONE_SEC_MS) {
        public void onTick(long millisUntilFinished) {
            long timeRemaining = Math.round(millisUntilFinished / ONE_SEC_MS);
//            mTextField.setText("seconds remaining: " + );
            Log.d(TAG, "CountDown: " + timeRemaining);
            lastHeartValue = Math.round(Math.random());
            heartTextView.setText(lastHeartValue + "");
        }

        // Trigger fired, send text message and show cpr button.
        public void onFinish() {
            cprButton.setVisibility(View.VISIBLE);
            sendTextMessage(PHONE_NUMBERS, cprTextMessage);
            Log.d(TAG, "countDown done");
        }
    };

    private Runnable voiceRunnable = new Runnable() {
        @Override
        public void run() {
            alertAndVibrate();
            handler.postDelayed(this, ONE_SEC_MS * 6);
        }
    };

    private void startHeartUpdate() {
        Log.d(TAG, "startHeartUpdate");
        if (!heartTimerStarted) {
            voiceStarted = false;
            handler.postDelayed(heartRateRunnable, ONE_SEC_MS);
            heartTimerStarted = true;
        }

        // Make sure alertTimer and voiceRunnable are off.
        try {
            alertTimer.cancel();
            Log.d(TAG, "stopped alertTimer");
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            handler.removeCallbacks(voiceRunnable);
            Log.d(TAG, "stopped voiceRunnable");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void stopHeartUpdate() {
        Log.d(TAG, "stopHeartUpdate");
        lastHeartValue = 0;
        if (heartTimerStarted) {
            Log.d(TAG, "stopped heartRateRunnable");
            handler.removeCallbacks(heartRateRunnable);
            heartTextView.setText(lastHeartValue + "");
            heartTimerStarted = false;
            // TODO: Uncomment when ready

        }
        if (!voiceStarted) {
            voiceStarted = true;
            // start countdown timer until alert siren.
            try {
                alertTimer.start();
                handler.postDelayed(voiceRunnable, COUNT_DOWN_SECS * ONE_SEC_MS);
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    private void toggleTimer() {
        if (heartTimerStarted) {
            stopHeartUpdate();
        } else {
            cprButton.setVisibility(View.INVISIBLE);
            startHeartUpdate();
        }
    }

    private void alertAndVibrate() {
        Log.d(TAG, "alertAndVibrate");
        Toast.makeText(getActivity(), CPR_VOICE_MESSAGE, Toast.LENGTH_SHORT).show();
        v.vibrate(VIBRATE_DURATION_MS);
        // TODO: Uncomment voice when ready
        AudioManager am = (AudioManager) getActivity().getSystemService(AUDIO_SERVICE);
        am.setSpeakerphoneOn(true);
        int amStreamMusicMaxVol = am.getStreamMaxVolume(am.STREAM_MUSIC);
        am.setStreamVolume(am.STREAM_MUSIC, amStreamMusicMaxVol, 0);
        t1.speak(CPR_VOICE_MESSAGE, TextToSpeech.QUEUE_FLUSH, null);
        am.setSpeakerphoneOn(false);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View cprView = inflater.inflate(R.layout.fragment_cpr, container, false);
        userName = "User";
        try {
            userName = this.getArguments().getString("userName");
        } catch (Exception e) {
            e.printStackTrace();
            userName = "User";
        }
        cprTextMessage = userName +
                " may be experiencing a cardiac arrest. He's currently at location: CNSI, UCLA";
        Log.d(TAG, "cprTextMessage: " + cprTextMessage);

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
            }
        });

        cprButton = (Button) cprView.findViewById(R.id.cpr_button);
        cprButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "cpr help button clicked");
                try {
                    // Return to normal rhythm.
                    startHeartUpdate();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                getActivity().getSupportFragmentManager().popBackStackImmediate();
//                ((MainActivity) getActivity()).updateFragment(Constants.MAP_FRAGMENT);
            }
        });
        cprButton.setVisibility(View.INVISIBLE);

        heartTextView = (TextView) cprView.findViewById(R.id.heart_text_view);
        heartTextView.setTextColor(Color.argb(alpha, 255, 0, 0));
        heartTextView.setText(lastHeartValue + "");

        timer = new Timer();
        heartTimerStarted = false;
        voiceStarted = false;
        startHeartUpdate();

        // Inflate the layout for this fragment
        return cprView;
    }

}
