package com.android.xctech.sidekey;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

public class BootCompletedReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();

        // make sure the app icon is removed every time the device boots.
        if (action.equals(Intent.ACTION_BOOT_COMPLETED)) {
            context.startService(new Intent(context, SideKeyService.class));
        }
    }
}
