package com.ekg.www.inventathon.auth;

import com.google.firebase.auth.FirebaseUser;

/**
 * Singleton class for encapsulating the google api client and current Firebase auth user.
 * Created by cbono on 9/18/16.
 */
public class AuthUser {
    private AuthUser() {

    }

    private static FirebaseUser user;
//    private static GoogleApiClient googleApiClient;

    public static FirebaseUser getUser() {
        return user;
    }
    public static void setUser(FirebaseUser firebaseUser) {
        user = firebaseUser;
    }

//    public static GoogleApiClient getClient() {
//        return googleApiClient;
//    }
//    public static void setClient(GoogleApiClient client) {
//        googleApiClient = client;
//    }
}
