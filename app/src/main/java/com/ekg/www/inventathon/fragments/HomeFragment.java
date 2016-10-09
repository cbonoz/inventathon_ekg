package com.ekg.www.inventathon.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.TextView;

import com.ekg.www.inventathon.Constants;
import com.ekg.www.inventathon.MainActivity;
import com.ekg.www.inventathon.R;
import com.ekg.www.inventathon.auth.AuthUser;
import com.google.firebase.auth.FirebaseUser;

/**
 * A placeholder fragment containing a simple view.
 */
public class HomeFragment extends Fragment {
    private static final String TAG = "HomeFragment";

    private Button profileButton;
    private Button mapButton;
    private Button cprButton;

    private Button googleSignOutButton;

    private TextView appNameTextView;
    private WebView profileView;

    private FirebaseUser user;

    public HomeFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View homeView = inflater.inflate(R.layout.fragment_main, container, false);

        user = AuthUser.getUser();

        profileButton = (Button) homeView.findViewById(R.id.profile_button);
        mapButton = (Button) homeView.findViewById(R.id.map_button);
        cprButton = (Button) homeView.findViewById(R.id.cpr_help_button);

        googleSignOutButton = (Button) homeView.findViewById(R.id.google_sign_out_button);

//        appNameTextView = (TextView) homeView.findViewById(R.id.app_name_text_view);
//        appNameTextView.getBackground().setAlpha(100);

        profileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "profile button clicked");
                ((MainActivity) getActivity()).updateFragment(Constants.PROFILE_FRAGMENT);
            }
        });

        mapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "map button clicked");
                ((MainActivity) getActivity()).updateFragment(Constants.MAP_FRAGMENT);
            }
        });

        cprButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "cpr button clicked");
                ((MainActivity) getActivity()).updateFragment(Constants.CPR_FRAGMENT);
            }
        });

        googleSignOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "google sign out button clicked");
                // Sign out the user using the main activity method.
                ((MainActivity) getActivity()).signOut();
            }
        });

//        profileView = (WebView) homeView.findViewById(R.id.profileView);
//        profileView.loadUrl(user.getPhotoUrl().toString());

        return homeView;
    }
}
