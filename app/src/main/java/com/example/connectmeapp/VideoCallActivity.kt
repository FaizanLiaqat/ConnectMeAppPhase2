package com.example.connectmeapp

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class VideoCallActivity : AppCompatActivity() {

    private lateinit var callTime: TextView
    private lateinit var endCallButton: ImageView
    private lateinit var switchToAudioCall: ImageView
    private lateinit var speakerButton: ImageView
    private lateinit var microphoneButton: ImageView
    private var isSpeakerOn = false
    private var isMicMuted = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video_call)

        // 🔹 Initialize UI Elements
        callTime = findViewById(R.id.call_time)
        endCallButton = findViewById(R.id.end_call)
        switchToAudioCall = findViewById(R.id.switch_to_audio)
        speakerButton = findViewById(R.id.speaker)
        microphoneButton = findViewById(R.id.microphone)

        // 🔹 Handle End Call Button Click
        endCallButton.setOnClickListener {
            finish() // Ends the call & closes activity
        }

        // 🔹 Handle Switch to Audio Call
        switchToAudioCall.setOnClickListener {
            val intent = Intent(this, PhoneCallActivity::class.java)
            startActivity(intent)
            finish() // Closes video call activity
        }

        // 🔹 Toggle Speaker Mode
        speakerButton.setOnClickListener {
            isSpeakerOn = !isSpeakerOn
            speakerButton.setImageResource(
                if (isSpeakerOn) R.drawable.speaker_on else R.drawable.speaker_off
            )
        }

        // 🔹 Toggle Microphone Mute/Unmute
        microphoneButton.setOnClickListener {
            isMicMuted = !isMicMuted
            microphoneButton.setImageResource(
                if (isMicMuted) R.drawable.mic_off_logo else R.drawable.mic_logo
            )
        }
    }
}


