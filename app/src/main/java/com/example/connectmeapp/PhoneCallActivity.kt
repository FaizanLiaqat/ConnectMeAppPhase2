package com.example.connectmeapp

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

import io.agora.rtc2.IRtcEngineEventHandler
import io.agora.rtc2.RtcEngine

class PhoneCallActivity : AppCompatActivity() {

    private lateinit var username: TextView
    private lateinit var profileImage: ImageView
    private lateinit var callTime: TextView
    private lateinit var endCallButton: ImageView
    private lateinit var switchToVideoCall: ImageView
    private lateinit var speakerButton: ImageView
    private lateinit var microphoneButton: ImageView

    // Agora integration
    private var rtcEngine: RtcEngine? = null
    private val appId: String by lazy { getString(R.string.appid) }
    private val token: String? by lazy { getString(R.string.token) }
    private val channelName: String by lazy { getString(R.string.channel) }  // Or you may use a different channel name for audio

    private val rtcEventHandler = object : IRtcEngineEventHandler() {
        override fun onJoinChannelSuccess(channel: String?, uid: Int, elapsed: Int) {
            runOnUiThread {
                Toast.makeText(this@PhoneCallActivity, "Joined audio channel: $channel", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private var isSpeakerOn = false
    private var isMicMuted = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_phone_call)

        // Get User Data from Intent
        val userName = intent.getStringExtra("username") ?: "Unknown"
        val userImage = intent.getIntExtra("profileImage", R.drawable.profile_placeholder)

        // Initialize UI Elements
        username = findViewById(R.id.call_username)
        profileImage = findViewById(R.id.call_profile_image)
        callTime = findViewById(R.id.call_time)
        endCallButton = findViewById(R.id.end_call)
        switchToVideoCall = findViewById(R.id.switch_to_video)
        speakerButton = findViewById(R.id.speaker)
        microphoneButton = findViewById(R.id.microphone)

        username.text = userName
        profileImage.setImageResource(userImage)

        initializeAgoraEngine()
        joinChannel()

        endCallButton.setOnClickListener {
            leaveChannelAndFinish()
        }

        switchToVideoCall.setOnClickListener {
            val intent = Intent(this, VideoCallActivity::class.java).apply {
                putExtra("username", userName)
                putExtra("profileImage", userImage)
            }
            startActivity(intent)
            leaveChannelAndFinish()
        }

        speakerButton.setOnClickListener {
            isSpeakerOn = !isSpeakerOn
            speakerButton.setImageResource(if (isSpeakerOn) R.drawable.speaker_on else R.drawable.speaker_off)
            rtcEngine?.setEnableSpeakerphone(isSpeakerOn)
        }

        microphoneButton.setOnClickListener {
            isMicMuted = !isMicMuted
            microphoneButton.setImageResource(if (isMicMuted) R.drawable.mic_off_logo else R.drawable.mic_logo)
            rtcEngine?.muteLocalAudioStream(isMicMuted)
        }
    }

    private fun initializeAgoraEngine() {
        try {
            rtcEngine = RtcEngine.create(baseContext, appId, rtcEventHandler)
        } catch (e: Exception) {
            throw RuntimeException("Error initializing Agora SDK: ${e.message}")
        }
    }

    private fun joinChannel() {
        rtcEngine?.disableVideo() // Audio-only call
        rtcEngine?.joinChannel(token, channelName, null, 0)
    }

    private fun leaveChannelAndFinish() {
        rtcEngine?.leaveChannel()
        RtcEngine.destroy()
        rtcEngine = null
        finish()
    }

    override fun onDestroy() {
        super.onDestroy()
        rtcEngine?.leaveChannel()
        RtcEngine.destroy()
        rtcEngine = null
    }
}
