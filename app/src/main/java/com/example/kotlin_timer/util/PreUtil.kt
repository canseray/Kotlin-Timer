package com.example.kotlin_timer.util

import android.content.Context
import android.preference.PreferenceManager
import com.example.kotlin_timer.MainActivity
import java.security.AccessControlContext


//yardımcı metotlar-uygulama ekranı

class PreUtil {


    companion object{

                                            //layout name
        private const val TIMER_LENGTH_ID = "com.example.kotlin_timer.timer_length"

        fun getTimerLength(context: Context): Int{
            val preferences = PreferenceManager.getDefaultSharedPreferences(context)
            return preferences.getInt(TIMER_LENGTH_ID,10)
        }

        //SharedPreferences: uygulamadan cıktıgında da veriyi saklamak


        //uygulamaya önceki sefer girdigimizde ayarladgımız süre uzunlugu
        private const val PREVIOUS_TIMER_LENGTH_SECONDS_ID = "com.example.kotlin_timer.previous_timer_length"

        fun getPreviousTimerLengthSeconds(context: Context): Long{
            val preferences = PreferenceManager.getDefaultSharedPreferences(context)
            return preferences.getLong(PREVIOUS_TIMER_LENGTH_SECONDS_ID,0)
        }

        fun setPreviousTimerLengthSeconds(seconds: Long, context: Context){
            val editor = PreferenceManager.getDefaultSharedPreferences(context).edit()
            editor.putLong(PREVIOUS_TIMER_LENGTH_SECONDS_ID,seconds)
            editor.apply()
        }





        //zamanlayıcı kaçıncı saniyede
        private const val TIMER_STATE_ID = "com.example.kotlin_timer.timer_state"

        fun getTimerState(context: Context): MainActivity.TimerState{
            val preferences = PreferenceManager.getDefaultSharedPreferences(context)
            val ordinal = preferences.getInt(TIMER_STATE_ID,0)
            return MainActivity.TimerState.values()[ordinal]
        }

        fun setTimerState(state: MainActivity.TimerState, context: Context){
            val editor = PreferenceManager.getDefaultSharedPreferences(context).edit()
            val ordinal = state.ordinal
            editor.putInt(TIMER_STATE_ID,ordinal)
            editor.apply()
        }





        //bitmesine kaç saniye var
        private const val SECONDS_REMAINING_ID = "com.example.kotlin_timer.seconds_remaining"

        fun getSecondsRemaining(context: Context): Long{
            val preferences = PreferenceManager.getDefaultSharedPreferences(context)
            return preferences.getLong(SECONDS_REMAINING_ID,0)
        }

        fun setSecondsRemaining(seconds: Long, context: Context){
            val editor = PreferenceManager.getDefaultSharedPreferences(context).edit()
            editor.putLong(SECONDS_REMAINING_ID,seconds)
            editor.apply()
        }





        //alarm için kalan zaman
        private const val ALARM_SET_TIME_ID = "com.example.kotlin_timer.background_time"

        fun getAlarmSetTime(context: Context): Long{
            val preferences = PreferenceManager.getDefaultSharedPreferences(context)
            return preferences.getLong(ALARM_SET_TIME_ID,0)
        }

        fun setAlarmSetTime(time: Long, context: Context){
            val editor = PreferenceManager.getDefaultSharedPreferences(context).edit()
            editor.putLong(ALARM_SET_TIME_ID,time)
            editor.apply()
        }


    }
}