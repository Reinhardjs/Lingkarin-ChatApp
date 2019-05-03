package com.lingkarin.dev.chatapp.services.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.lingkarin.dev.chatapp.services.service.MyService;

public class BootReceiver extends BroadcastReceiver {
    public void onReceive(Context paramContext, Intent paramIntent) {
        if (!MyService.isMyServiceRunning(paramContext)) {
            paramContext.startService(new Intent(paramContext, MyService.class));
        }
    }
}
