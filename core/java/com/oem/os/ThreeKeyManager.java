package com.oem.os;

import android.content.Context;
import android.os.IBinder;
import android.os.RemoteException;
import android.os.ServiceManager;

import com.oem.os.IThreeKeyService;

/** @hide */
public class ThreeKeyManager {
    private static final String TAG = "ThreeKeyManager";
    private static IThreeKeyService sService;

    public ThreeKeyManager(Context context) {
        
    }

    static public IThreeKeyService getService()
    {
        if (sService != null) {
            return sService;
        }
        IBinder b = ServiceManager.getService("ThreeKeyService");
        sService = IThreeKeyService.Stub.asInterface(b);
        return sService;
    }
}
