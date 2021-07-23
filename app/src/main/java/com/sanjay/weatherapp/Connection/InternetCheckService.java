package com.sanjay.weatherapp.Connection;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.IBinder;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

public class InternetCheckService extends Service {

    private BroadcastReceiver receiver = null;

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(intent != null){
            // setup receiver
            receiver = new InternetBroadcastReceiver();
            broadcastIntent();
        }
        return START_STICKY;
    }

    public void broadcastIntent() {
        registerReceiver(receiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // unregister service
        LocalBroadcastManager.getInstance(InternetCheckService.this).unregisterReceiver(receiver);
    }
}
