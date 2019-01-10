package com.journeyOS.base.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import com.journeyOS.base.utils.AppUtils;
import com.journeyOS.base.utils.LogUtils;

public class BootBroadcastReceiver extends BroadcastReceiver {
    private static final String TAG = BootBroadcastReceiver.class.getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (Intent.ACTION_BOOT_COMPLETED.equals(action)) {
            LogUtils.d(TAG, "boot broadcast receiver, " + action);
            AppUtils.startEdge(context);
        }
    }
}
