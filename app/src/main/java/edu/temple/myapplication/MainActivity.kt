package edu.temple.myapplication

import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.IBinder
import android.os.Looper
import android.widget.Button
import android.widget.TextView
import java.util.logging.Handler
import kotlin.concurrent.timer

class MainActivity : AppCompatActivity() {

    lateinit var timerTextView : TextView
    lateinit var timerBinder: TimerService.TimerBinder
    var isBound = false
    val timerHandler = android.os.Handler(Looper.getMainLooper()) {
        timerTextView.text = it.what.toString()
        true
    }

    val serviceConnection = object : ServiceConnection{
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            timerBinder = service as TimerService.TimerBinder
            timerBinder.setHandler(timerHandler)
            isBound = true
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            isBound = false
        }

    }



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        bindService(
            Intent(this, TimerService::class.java),
            serviceConnection,
            BIND_AUTO_CREATE

        )

        timerTextView = findViewById<TextView>(R.id.textView)

        findViewById<Button>(R.id.startButton).setOnClickListener {
            if (isBound) {
                if (timerBinder.isRunning) timerBinder.pause() else timerBinder.start(20)
            }
        }
        
        findViewById<Button>(R.id.stopButton).setOnClickListener {
            if (timerBinder.isRunning){
                timerBinder.stop()
                timerTextView.text = 0.toString()
            }
        }
    }

    override fun onDestroy() {
        unbindService(serviceConnection)
        super.onDestroy()
    }


}