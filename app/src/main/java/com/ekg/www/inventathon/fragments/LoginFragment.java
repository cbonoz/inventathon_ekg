package com.ekg.www.inventathon.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ekg.www.inventathon.MainActivity;
import com.ekg.www.inventathon.R;
import com.google.android.gms.common.SignInButton;

/**
 * A simple {@link Fragment} subclass.
 * to handle interaction events.
 */
public class LoginFragment extends Fragment {
    private static final String TAG = "LoginFragment";

    public LoginFragment() {
        // Required empty public constructor
    }

    private SignInButton googleSignInButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Log.d(TAG, "onCreateView");
        View loginView = inflater.inflate(R.layout.fragment_login, container, false);

        googleSignInButton = (SignInButton) loginView.findViewById(R.id.sign_in_button);
        googleSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "google sign in button clicked");
                // Sign out the user using the main activity method.
                ((MainActivity) getActivity()).signIn();
            }
        });


        return loginView;
    }
}

