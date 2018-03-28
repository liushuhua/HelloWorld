package com.example.lenovo.helloworld.alive;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class GrayService extends Service {
    public GrayService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
