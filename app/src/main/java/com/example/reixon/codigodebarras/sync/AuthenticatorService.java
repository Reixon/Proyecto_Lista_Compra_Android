package com.example.reixon.codigodebarras.sync;


import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

/**
 * Created by reixon on 12/02/2018.
 */

public class AuthenticatorService extends Service {
    private AccountAuthenticator authenticator;

    @Override
    public void onCreate() {
        authenticator = new AccountAuthenticator(this);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return authenticator.getIBinder();
    }
}
