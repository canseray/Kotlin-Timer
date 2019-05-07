package com.example.kotlin_timer

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresApi
import com.example.kotlin_timer.util.NotificationUtil
import com.example.kotlin_timer.util.PreUtil

class TimerExpiredReceiver : BroadcastReceiver() {

    //zamanlayıcı süresi doldugunda

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN)
    override fun onReceive(context: Context, intent: Intent) {

        NotificationUtil.showTimerExpired(context)

        PreUtil.setTimerState(MainActivity.TimerState.Stopped,context)
        PreUtil.setAlarmSetTime(0,context)
    }
}
