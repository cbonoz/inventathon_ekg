package com.ekg.www.inventathon.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.ekg.www.inventathon.bluetooth.BTSocket;

public class BTService extends Service {

    private static final String TAG = "BTService";

    public BTService() {
    }

    private BTSocket btSocket;

    @Override
    public IBinder onBind(Intent intent) {

        btSocket = new BTSocket(this);
        if (btSocket.openConn()) {
            btSocket.beginListen();
        }

        return null;
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i("LocalService", "Received start id " + startId + ": " + intent);
        btSocket = new BTSocket(this);
        if (btSocket.openConn()) {
            btSocket.beginListen();
        }
        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {

        try {
            btSocket.closeConn();
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "btSocket closeConn failure");
        }
    }
}
