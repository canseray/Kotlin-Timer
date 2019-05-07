package com.example.kotlin_timer

import android.annotation.TargetApi
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresApi
import com.example.kotlin_timer.util.NotificationUtil
import com.example.kotlin_timer.util.PreUtil

class TimerNotificationActionReceiver : BroadcastReceiver() {

    //bildirim ekranında olan biten view

    @TargetApi(Build.VERSION_CODES.KITKAT)
    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN)
    override fun onReceive(context: Context, intent: Intent) {
        when (intent.action){

            AppConstants.ACTION_STOP -> {
                MainActivity.removeAlarm(context)
                PreUtil.setTimerState(MainActivity.TimerState.Stopped,context)
                NotificationUtil.hideTimerNotificaiton(context)
            }

            AppConstants.ACTION_PAUSE -> {
                var secondsRemaining = PreUtil.getSecondsRemaining(context)
                val alarmSetTime = PreUtil.getAlarmSetTime(context)
                val nowSeconds = MainActivity.nowSeconds

                secondsRemaining -= nowSeconds - alarmSetTime
                PreUtil.setSecondsRemaining(secondsRemaining,context)

                MainActivity.removeAlarm(context)
                PreUtil.setTimerState(MainActivity.TimerState.Paused,context)
                NotificationUtil.showTimerPaused(context)
            }

            AppConstants.ACTION_RESUME -> {
                var secondsRemaining = PreUtil.getSecondsRemaining(context)
                val wakeUpTime = MainActivity.setAlarm(context,MainActivity.nowSeconds,secondsRemaining)
                PreUtil.setTimerState(MainActivity.TimerState.Running,context)
                NotificationUtil.showTimerRunning(context,wakeUpTime)
            }

            AppConstants.ACTION_START -> {
                val minutesRemaining = PreUtil.getTimerLength(context)
                val secondsRemaining = minutesRemaining * 60L
                val wakeUpTime = MainActivity.setAlarm(context,MainActivity.nowSeconds,secondsRemaining)
                PreUtil.setTimerState(MainActivity.TimerState.Running,context)
                PreUtil.setSecondsRemaining(secondsRemaining,context)
                NotificationUtil.showTimerRunning(context,wakeUpTime)
            }
        }
    }
}
