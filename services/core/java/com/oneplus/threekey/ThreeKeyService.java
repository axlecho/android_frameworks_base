package com.oneplus.threekey;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.util.Slog;

import com.oem.os.IThreeKeyService;
import com.oneplus.threekey.ThreeKeyHw;
import com.oneplus.threekey.ThreeKeyHw.ThreeKeyUnsupportException;

public class ThreeKeyService extends IThreeKeyService.Stub {

    private static final String TAG = "ThreeKeyService";
    private static final int MSG_SYSTEM_READY = 1;
    private final Object mLock = new Object();

    private ThreeKeyHw threekeyhw;
    private Context mContext;

    // held while there is a pending state change.
    private final WakeLock mWakeLock;

    public ThreeKeyService(Context context) {
        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        mWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "OemExService");
        mContext = context;
        Slog.d(TAG,"[ThreeKeyService]");
    }


    public int getThreeKeyStatus() {
        Slog.d(TAG,"[getThreeKeyStatus]");
        try {
            return threekeyhw.getState();
        } catch (ThreeKeyUnsupportException e) {
            Slog.e(TAG,"system unsupport for threekey");
        }
        return 0;
    }

    private final Handler mHandler = new Handler(Looper.myLooper(), null, true) {
        @Override
        public void handleMessage(Message msg) {
            int newState = msg.arg1;
            int oldState = msg.arg2;

            switch (msg.what) {
                case MSG_SYSTEM_READY:
                    onSystemReady();
                    if (mWakeLock.isHeld()) {
                        mWakeLock.release();
                    }
                    break;
            }
        }
    };

    public void systemRunning() {
        synchronized (mLock) {
            // This wakelock will be released by handler
            if (!mWakeLock.isHeld()) {
                mWakeLock.acquire();
            }

            // Use message to aovid blocking system server
            Message msg = mHandler.obtainMessage(MSG_SYSTEM_READY, 0, 0, null);
            mHandler.sendMessage(msg);
        }
    }


    private void onSystemReady() {
        Slog.d(TAG, "systemReady");
        threekeyhw = new ThreeKeyHw(mContext);
        threekeyhw.init();
    }

}
