package com.ekg.www.inventathon;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.widget.Toast;

import com.ekg.www.inventathon.auth.AuthUser;
import com.ekg.www.inventathon.fragments.CPRFragment;
import com.ekg.www.inventathon.fragments.DefibrillatorMapsFragment;
import com.ekg.www.inventathon.fragments.HomeFragment;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

public class MainActivity extends FragmentActivity implements
        GoogleApiClient.OnConnectionFailedListener {
    private static final String TAG = "MainActivity";

    private static final int RC_SIGN_IN = 9001;

    private GoogleApiClient mGoogleApiClient;

    private final FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseUser user = null;

    private static final String userName = "John";

    private Fragment homeFragment = new HomeFragment();
    private Fragment mapFragment = new DefibrillatorMapsFragment();
    private Fragment cprFragment = new CPRFragment();

    protected Bundle mSavedInstanceState;

    private Intent heartIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mSavedInstanceState = savedInstanceState;
        Log.d(TAG, "mainactivity onCreate");


        // TODO: Ensure user is logged in before accessing this activity.
        setContentView(R.layout.activity_main);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        // [END config_signin]

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        // [START auth_state_listener]
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                AuthUser.setUser(firebaseAuth.getCurrentUser());
                user = AuthUser.getUser();
                if (user != null) {
                    // User is signed in
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());

                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                    // Launch Login Activity.
//                    startLoginActivity();

                }

            }
        };

        if (savedInstanceState == null) {
            Log.d(TAG, "Starting home fragment");
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragment_container, homeFragment).commit();
        }

//        heartIntent = new Intent(this, BTService.class);
//        startService(heartIntent);


    }

    // [START on_start_add_listener]
    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }
    // [END on_start_add_listener]

    // [START on_stop_remove_listener]
    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }
    // [END on_stop_remove_listener]


    @Override
    public void onDestroy() {
        super.onDestroy();
//        stopService(heartIntent);
    }

    // [START onactivityresult]
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = result.getSignInAccount();
                firebaseAuthWithGoogle(account);
            } else {
                // Google Sign In failed, update UI appropriately
                // [START_EXCLUDE]
                Toast.makeText(MainActivity.this, "Login failed.",
                        Toast.LENGTH_SHORT).show();
                // [END_EXCLUDE]
            }
        }
    }
    // [END onactivityresult]

    // [START auth_with_google]
    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());
        // [START_EXCLUDE silent]
//        showProgressDialog();
        // [END_EXCLUDE]

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "signInWithCredential:onComplete:" + task.isSuccessful());

                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "signInWithCredential", task.getException());
                            Toast.makeText(MainActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                        // [START_EXCLUDE]
//                        hideProgressDialog();
                        // [END_EXCLUDE]
                    }
                });
    }
    // [END auth_with_google]


    public void updateFragment(int fragmentNumber) {
        Log.d(TAG, "updateFragment");
        final Fragment targetFragment;
        switch(fragmentNumber) {
            case Constants.HOME_FRAGMENT:
                targetFragment = homeFragment;
                break;
            case Constants.PROFILE_FRAGMENT:
                targetFragment = homeFragment;
                // TODO: Load another fragment
                return;
            case Constants.CPR_FRAGMENT:
                Bundle bundle = new Bundle();
                bundle.putString("userName", userName);
                targetFragment = cprFragment;
                targetFragment.setArguments(bundle);
                break;
            case Constants.MAP_FRAGMENT:
                targetFragment = mapFragment;
                break;
            default:
                Log.e(TAG, "Invalid fragmentNumber " + fragmentNumber + " for updateFragment");
                return;
        }

        if (targetFragment.isAdded()) {
            Log.e(TAG, targetFragment.getClass() + " fragment already added.");
            return;
        }

        if (findViewById(R.id.fragment_container) != null) {
            Log.d(TAG, "updating fragment to " + targetFragment.getClass());

            // Create new fragment and transaction
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

            // Replace whatever is in the fragment_container view with this fragment,
            // and add the transaction to the back stack if needed
            transaction.replace(R.id.fragment_container, targetFragment);
            transaction.addToBackStack(null);

            // Commit the transaction
            transaction.commit();
        }

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        // An unresolvable error has occurred and Google APIs (including Sign-In) will not
        // be available.
        Log.d(TAG, "onConnectionFailed:" + connectionResult);
        Toast.makeText(this, "Google Play Services error.", Toast.LENGTH_SHORT).show();
    }


    @Override
    public void onBackPressed() {
        Log.d(TAG, "onBackPressed - should send to homeFragment");
        updateFragment(Constants.HOME_FRAGMENT);
    }


    //  Auth Actions.
    public void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    public void signOut() {
        // Firebase sign out
        mAuth.signOut();
        AuthUser.setUser(null);

        // Google sign out
        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(@NonNull Status status) {
                        startLoginActivity();
                    }
                });
    }

    public void revokeAccess() {
        // Firebase sign out
        mAuth.signOut();
        AuthUser.setUser(null);

        // Google revoke access
        Auth.GoogleSignInApi.revokeAccess(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(@NonNull Status status) {
                        startLoginActivity();
                    }
                });
    }

    private void startLoginActivity() {
        startActivity(new Intent(MainActivity.this, LoginActivity.class));
    }


}
