package com.ekg.www.inventathon.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.Button;

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
    private Button emtButton;
    private Button cprButton;

    private Button googleSignOutButton;

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
        emtButton = (Button) homeView.findViewById(R.id.emt_button);
        googleSignOutButton = (Button) homeView.findViewById(R.id.google_sign_out_button);

        // TODO(cbono): add behavior to buttons.
        profileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "profile button clicked");
                ((MainActivity) getActivity()).updateFragment(Constants.PROFILE_FRAGMENT);
            }
        });
        emtButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "schedule button clicked");
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
