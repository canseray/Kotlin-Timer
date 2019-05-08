package com.example.kotlin_timer

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity;
import android.view.Menu
import android.view.MenuItem
import androidx.annotation.RequiresApi
import com.example.kotlin_timer.util.NotificationUtil
import com.example.kotlin_timer.util.PreUtil

import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*
import java.util.*

class MainActivity : AppCompatActivity() {

    companion object{

        @RequiresApi(Build.VERSION_CODES.KITKAT)

        fun setAlarm(context: Context, nowSeconds: Long, secondsRemaining: Long): Long{
            val wakeUpTime = (nowSeconds + secondsRemaining) * 1000
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            val intent = Intent(context,TimerExpiredReceiver::class.java)
            val pendingIntent = PendingIntent.getBroadcast(context, 0,intent, 0)
            alarmManager.setExact(AlarmManager.RTC_WAKEUP,wakeUpTime,pendingIntent)
            PreUtil.setAlarmSetTime(nowSeconds,context)
            return wakeUpTime
        }

        fun removeAlarm(context: Context){
            val intent = Intent(context,TimerExpiredReceiver::class.java)
            val pendingIntent = PendingIntent.getBroadcast(context, 0,intent, 0)
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            alarmManager.cancel(pendingIntent)
            PreUtil.setAlarmSetTime(0, context)

        }

        val nowSeconds: Long
        get() = Calendar.getInstance().timeInMillis / 1000
    }




    enum class TimerState{
        Stopped, Paused, Running
    }

    private lateinit var timer : CountDownTimer
    private var timerLengthSeconds: Long = 0L
    private var timerState = TimerState.Stopped
    private var secondsRemaining = 0L






    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        supportActionBar?.setIcon(R.drawable.ic_timer)
        supportActionBar?.title = "     Timer"

        fab_start.setOnClickListener { v ->
            startTimer()
            timerState = TimerState.Running
            updateButtons()
        }

        fab_pause.setOnClickListener { v ->
            timer.cancel()
            timerState = TimerState.Paused
            updateButtons()
        }

        fab_stop.setOnClickListener { v ->
            timer.cancel()
            onTimerFinished()
        }


    }



    override fun onResume() {
        super.onResume()
        initTimer()
        removeAlarm(this)
        NotificationUtil.hideTimerNotificaiton(this)
    }



    @RequiresApi(Build.VERSION_CODES.KITKAT)
    override fun onPause() {
        super.onPause()
        if (timerState == TimerState.Running){
            timer.cancel()
            val wakeUpTime = setAlarm(this, nowSeconds,secondsRemaining)
            NotificationUtil.showTimerRunning(this,wakeUpTime)
        }
        else if(timerState == TimerState.Paused) {
            NotificationUtil.showTimerPaused(this)
        }

        PreUtil.setPreviousTimerLengthSeconds(timerLengthSeconds,this)
        PreUtil.setTimerState(timerState,this)
        PreUtil.setSecondsRemaining(secondsRemaining,this)
    }





    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }





    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_settings -> {
                val intent = Intent(this,SettingsActivity::class.java)
                startActivity(intent)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }





    private fun startTimer(){
        timerState = TimerState.Running

        timer = object : CountDownTimer(secondsRemaining * 1000 , 1000){

            override fun onFinish() = onTimerFinished()

            override fun onTick(millisUntilFinished: Long) {
                secondsRemaining = millisUntilFinished / 1000
                updateCountdownUI()
            }

        }.start()
    }



    private fun updateButtons(){
        when (timerState){
            TimerState.Running ->{
                fab_start.isEnabled = false
                fab_pause.isEnabled = true
                fab_stop.isEnabled = true
            }
            TimerState.Stopped ->{
                fab_start.isEnabled = true
                fab_pause.isEnabled = false
                fab_stop.isEnabled = false
            }
            TimerState.Paused ->{
                fab_start.isEnabled = true
                fab_pause.isEnabled = false
                fab_stop.isEnabled = true
            }
        }
    }



    private fun onTimerFinished(){
        timerState = TimerState.Stopped
        setNewTimerLength()
        progress_countdown.progress = 0
        PreUtil.setSecondsRemaining(timerLengthSeconds,this)
        secondsRemaining = timerLengthSeconds
    }



    private fun initTimer(){
        timerState = PreUtil.getTimerState(this)
        if (timerState == TimerState.Stopped)
            setNewTimerLength()
            //we dont want to change the length of the timer which is allready running
            //if the lenth was changed in settings while it was background
        else
            setPreviousTimerLenght()

        secondsRemaining = if (timerState == TimerState.Running || timerState == TimerState.Paused)
            PreUtil.getSecondsRemaining(this)
        else
            timerLengthSeconds

        val alarmSetTime = PreUtil.getAlarmSetTime(this)
        if (alarmSetTime > 0)
            secondsRemaining -= nowSeconds - alarmSetTime

        if (secondsRemaining <= 0)
            onTimerFinished()

        else if (timerState == TimerState.Running)
            startTimer()
        updateButtons()
        updateCountdownUI()
    }



    private fun updateCountdownUI(){
        val minuteUntilFinished = secondsRemaining / 60
        val secondsInMinuteUntilFinished = secondsRemaining - minuteUntilFinished + 60
        val secondsStr = secondsInMinuteUntilFinished.toString()
        textView_countdown.text = "$minuteUntilFinished:${
        if (secondsStr.length == 2) secondsStr
        else "0" + secondsStr}"
        progress_countdown.progress = (timerLengthSeconds - secondsRemaining).toInt()
    }



    private fun setNewTimerLength(){
        val lengthInMinutes = PreUtil.getTimerLength(this)
        timerLengthSeconds = (lengthInMinutes * 60L)
        progress_countdown.max = timerLengthSeconds.toInt()
    }



    private fun setPreviousTimerLenght(){
        timerLengthSeconds = PreUtil.getPreviousTimerLengthSeconds(this)
        progress_countdown.max = timerLengthSeconds.toInt()
    }



}

