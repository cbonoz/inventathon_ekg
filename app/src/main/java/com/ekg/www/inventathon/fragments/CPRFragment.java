package com.ekg.www.inventathon.fragments;

import android.content.Context;
import android.os.Bundle;
import android.os.Vibrator;
import android.speech.tts.TextToSpeech;
import android.support.v4.app.Fragment;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.ekg.www.inventathon.R;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import static com.google.android.gms.wearable.DataMap.TAG;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 */
public class CPRFragment extends Fragment {


    private Vibrator v;
    private TextToSpeech t1;

    private Button cardiacButton;

    private static final String CPR_HELP_TEXT = "Cardiac Arrest. Please Check Phone.";
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

    private void alertAndVibrateAndText() {
        Toast.makeText(getActivity(), CPR_HELP_TEXT,Toast.LENGTH_SHORT).show();
        t1.speak(CPR_HELP_TEXT, TextToSpeech.QUEUE_FLUSH, null);
        v.vibrate(VIBRATE_DURATION_MS);
        sendTextMessage(PHONE_NUMBERS, CPR_HELP_TEXT);
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View cprView = inflater.inflate(R.layout.fragment_cpr, container, false);
        t1 = new TextToSpeech(getActivity(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status != TextToSpeech.ERROR) {
                    t1.setLanguage(Locale.UK);
                }
            }
        });

        v = (Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE);
        cardiacButton = (Button) cprView.findViewById(R.id.cardiac_button);
        cardiacButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "cardiac button clicked");
                alertAndVibrateAndText();
            }
        });


        // Inflate the layout for this fragment
        return cprView;

    }

}
