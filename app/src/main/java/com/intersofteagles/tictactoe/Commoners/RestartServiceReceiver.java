package com.intersofteagles.tictactoe.Commoners;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
import android.util.Log;

public class RestartServiceReceiver extends BroadcastReceiver
{


    @Override
    public void onReceive(Context context, Intent intent) {
        try {
//            context.startService(new Intent(context.getApplicationContext(), CommentService.class));
//
//            Intent alarm = new Intent(context, ShopReceiver.class);
//            alarm.putExtra("type", "shop");
//            boolean alarmRunning = (PendingIntent.getBroadcast(context, 0, alarm, PendingIntent.FLAG_NO_CREATE) != null);
//            if(!alarmRunning) {
//                PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, alarm, 0);
//                AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);     //Icrease to every 3 hrs
//                alarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime(), (60000)*60/*15 mins*/, pendingIntent);
//            }

        }catch (Exception e){e.printStackTrace();}

    }

}