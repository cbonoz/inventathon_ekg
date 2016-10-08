package com.ekg.www.inventathon.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.Vibrator;
import android.speech.tts.TextToSpeech;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.ekg.www.inventathon.R;

import java.util.Locale;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link CPRFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class CPRFragment extends Fragment {
    private OnFragmentInteractionListener mListener;

    private Vibrator v;
    private final TextToSpeech t1;

    private static final String CPR_HELP_TEXT = "Cardiac Arrest. Please Check Phone.";
    private static final int VIBRATE_DURATION_MS = 500;

    public CPRFragment() {
        // Required empty public constructor
        t1=new TextToSpeech(getActivity(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status != TextToSpeech.ERROR) {
                    t1.setLanguage(Locale.UK);
                }
            }
        });

        v = (Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE);
    }

    private void alertAndVibrate() {
        Toast.makeText(getActivity(), CPR_HELP_TEXT,Toast.LENGTH_SHORT).show();
        t1.speak(CPR_HELP_TEXT, TextToSpeech.QUEUE_FLUSH, null);
        v.vibrate(VIBRATE_DURATION_MS);

    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_cpr, container, false);

    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
