package com.ekg.www.inventathon.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class BTService extends Service {
    public BTService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
