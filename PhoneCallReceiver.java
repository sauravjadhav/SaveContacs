package com.example.savecontacts;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.TelephonyManager;

public class PhoneCallReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(TelephonyManager.ACTION_PHONE_STATE_CHANGED)) {
            String state = intent.getStringExtra(TelephonyManager.EXTRA_STATE);
            if (state.equals(TelephonyManager.EXTRA_STATE_IDLE)) {
                // Start your MainActivity here
                Intent mainIntent = new Intent(context, MainActivity.class);
                mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(mainIntent);
            }
        }
    }
}
