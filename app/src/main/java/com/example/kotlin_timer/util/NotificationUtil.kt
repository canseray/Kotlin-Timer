package com.example.kotlin_timer.util

import android.annotation.TargetApi
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.TaskStackBuilder
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import androidx.annotation.ColorInt
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.example.kotlin_timer.AppConstants
import com.example.kotlin_timer.MainActivity
import com.example.kotlin_timer.R
import com.example.kotlin_timer.TimerNotificationActionReceiver
import java.text.SimpleDateFormat
import java.util.*


//yardımcı metotlar-bildirim ekranı
class NotificationUtil {
    companion object{
        private const val CHANNEL_ID_TIMER = "menu_timer"
        private const val  CHANNEL_NAME_TIMER = "Timer App Timer"
        private const val TIMER_ID = 0


        //bildirim ekranında timer durumu
        //PendingIntent: asenkron,notify ekranına tıkladıgımızda gerceklesecekler


        //zaman bitince
        @RequiresApi(Build.VERSION_CODES.JELLY_BEAN)
        fun showTimerExpired(context: Context){

            val startIntent = Intent(context, TimerNotificationActionReceiver::class.java)
            startIntent.action = AppConstants.ACTION_START
            val startPendingIntent = PendingIntent.getBroadcast(context,
                            0,startIntent,PendingIntent.FLAG_CANCEL_CURRENT)

            val nBuilder = getBasicNotificationBuilder(context, CHANNEL_ID_TIMER,true)
            nBuilder.setContentTitle("Timer Expired")
                     .setContentText("start again?")
                    .setContentIntent(getPendingIntentWithStack(context,MainActivity::class.java))
                    .addAction(R.drawable.ic_play,"Start",startPendingIntent)

            val mManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            mManager.createNotificationChannel(CHANNEL_ID_TIMER, CHANNEL_NAME_TIMER,true)
            mManager.notify(TIMER_ID,nBuilder.build())

        }



        //timer calısırken
        @RequiresApi(Build.VERSION_CODES.JELLY_BEAN)
        fun showTimerRunning(context: Context, wakeUpTime: Long){


            val stopIntent = Intent(context, TimerNotificationActionReceiver::class.java)
            stopIntent.action = AppConstants.ACTION_START
            val stopPendingIntent = PendingIntent.getBroadcast(context,
                0,stopIntent,PendingIntent.FLAG_CANCEL_CURRENT)



            val pauseIntent = Intent(context, TimerNotificationActionReceiver::class.java)
            pauseIntent.action = AppConstants.ACTION_START
            val pausePendingIntent = PendingIntent.getBroadcast(context,
                0,pauseIntent,PendingIntent.FLAG_CANCEL_CURRENT)

            val df = SimpleDateFormat.getTimeInstance(SimpleDateFormat.SHORT)


            val nBuilder = getBasicNotificationBuilder(context, CHANNEL_ID_TIMER,true)
            nBuilder.setContentTitle("Timer is running")
                .setContentText("end : ${df.format(Date(wakeUpTime))}")    //kalan süre end: 6:20
                .setContentIntent(getPendingIntentWithStack(context,MainActivity::class.java))
                .setOngoing(true)
                .addAction(R.drawable.ic_play,"Stop",stopPendingIntent)
                .addAction(R.drawable.ic_play,"Pause",pausePendingIntent)

            val mManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            mManager.createNotificationChannel(CHANNEL_ID_TIMER, CHANNEL_NAME_TIMER,true)
            mManager.notify(TIMER_ID,nBuilder.build())
        }



        //timer durdurunca
        @RequiresApi(Build.VERSION_CODES.JELLY_BEAN)
        fun showTimerPaused(context: Context){

            val resumeIntent = Intent(context, TimerNotificationActionReceiver::class.java)
            resumeIntent.action = AppConstants.ACTION_START
            val resumePendingIntent = PendingIntent.getBroadcast(context,
                0,resumeIntent,PendingIntent.FLAG_CANCEL_CURRENT)

            val nBuilder = getBasicNotificationBuilder(context, CHANNEL_ID_TIMER,true)
            nBuilder.setContentTitle("Timer is paused")
                .setContentText("resume?")
                .setContentIntent(getPendingIntentWithStack(context,MainActivity::class.java))
                .setOngoing(true)
                .addAction(R.drawable.ic_play,"Resume",resumePendingIntent)

            val mManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            mManager.createNotificationChannel(CHANNEL_ID_TIMER, CHANNEL_NAME_TIMER,true)
            mManager.notify(TIMER_ID,nBuilder.build())

        }




        fun hideTimerNotificaiton(context: Context){
            val mManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            mManager.cancel(TIMER_ID)
        }





        private fun getBasicNotificationBuilder(context: Context, channelId: String, playSound: Boolean)
        : NotificationCompat.Builder {
            val notificationSound: Uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
            val nBuilder = NotificationCompat.Builder(context,channelId)
                .setSmallIcon(R.drawable.ic_timer)
                .setAutoCancel(true)
                .setDefaults(0)
            if (playSound) nBuilder.setSound(notificationSound)
            return nBuilder
        }

        @RequiresApi(Build.VERSION_CODES.JELLY_BEAN)
        private fun <T> getPendingIntentWithStack(context: Context, javaClass: Class<T>) : PendingIntent  {
            val resultIntent = Intent(context,javaClass)
            resultIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP

            val stackBuilder = TaskStackBuilder.create(context)
            stackBuilder.addParentStack(javaClass)
            stackBuilder.addNextIntent(resultIntent)

            return stackBuilder.getPendingIntent(0,PendingIntent.FLAG_UPDATE_CURRENT)
        }

        @TargetApi(26)
        private fun NotificationManager.createNotificationChannel(channelId: String,
                                                                    channelName: String,
                                                                    playSound: Boolean){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
                val channelImportance = if (playSound) NotificationManager.IMPORTANCE_DEFAULT
                                        else NotificationManager.IMPORTANCE_LOW
                val nChannel = NotificationChannel(channelId,channelName,channelImportance)
                nChannel.enableLights(true)
                nChannel.lightColor = Color.BLUE
                this.createNotificationChannel(nChannel)

            }
        }


    }
}