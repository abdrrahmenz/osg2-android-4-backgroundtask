package com.educa62.backgroundtask;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class MyAlarmManager extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        // munculin toast dari alarmManager yg dipanggil di Activity
        Toast.makeText(context, "hello this action from alarmManager", Toast.LENGTH_SHORT).show();
    }
}
