package com.example.connectmeapp

import android.content.Intent
import android.os.Bundle
import android.view.SurfaceView
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import io.agora.rtc2.IRtcEngineEventHandler
import io.agora.rtc2.RtcEngine
import io.agora.rtc2.video.VideoCanvas

class VideoCallActivity : AppCompatActivity() {

    private lateinit var callTime: TextView
    private lateinit var endCallButton: ImageView
    private lateinit var switchToAudioCall: ImageView
    private lateinit var speakerButton: ImageView
    private lateinit var microphoneButton: ImageView

    // Agora integration values loaded from strings.xml
    private val appId: String by lazy { getString(R.string.appid) }
    private val token: String? by lazy { getString(R.string.token) }  // Use token if enabled, otherwise it can be null
    private val channelName: String by lazy { getString(R.string.channel) }

    private var rtcEngine: RtcEngine? = null

    // Views for rendering video (should be SurfaceView in XML)
    private lateinit var remoteVideoView: SurfaceView
    private lateinit var localVideoView: SurfaceView

    // Track remote user uid for remote video setup
    private var remoteUserUid: Int = 0

    private val rtcEventHandler = object : IRtcEngineEventHandler() {
        override fun onJoinChannelSuccess(channel: String?, uid: Int, elapsed: Int) {
            runOnUiThread {
                Toast.makeText(this@VideoCallActivity, "Joined channel: $channel", Toast.LENGTH_SHORT).show()
            }
        }
        override fun onUserJoined(uid: Int, elapsed: Int) {
            runOnUiThread {
                remoteUserUid = uid
                setupRemoteVideo(uid)
            }
        }
        override fun onUserOffline(uid: Int, reason: Int) {
            runOnUiThread {
                removeRemoteVideo()
            }
        }
    }

    private var isSpeakerOn = false
    private var isMicMuted = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video_call)

        // Initialize UI Elements
        callTime = findViewById(R.id.call_time)
        endCallButton = findViewById(R.id.end_call)
        switchToAudioCall = findViewById(R.id.switch_to_audio)
        speakerButton = findViewById(R.id.speaker)
        microphoneButton = findViewById(R.id.microphone)

        // Obtain references for video rendering views.
        remoteVideoView = findViewById(R.id.video_placeholder) as SurfaceView
        localVideoView = findViewById(R.id.self_video_preview) as SurfaceView

        initializeAgoraEngine()
        setupLocalVideo()
        joinChannel()

        endCallButton.setOnClickListener {
            leaveChannelAndFinish()
        }

        switchToAudioCall.setOnClickListener {
            val intent = Intent(this, PhoneCallActivity::class.java).apply {
                putExtra("username", intent.getStringExtra("username") ?: "Unknown")
                putExtra("profileImage", intent.getIntExtra("profileImage", R.drawable.profile_placeholder))
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

    private fun setupLocalVideo() {
        rtcEngine?.enableVideo()
        rtcEngine?.setupLocalVideo(VideoCanvas(localVideoView, VideoCanvas.RENDER_MODE_FIT, 0))
    }

    private fun setupRemoteVideo(uid: Int) {
        rtcEngine?.setupRemoteVideo(VideoCanvas(remoteVideoView, VideoCanvas.RENDER_MODE_FIT, uid))
        remoteVideoView.visibility = SurfaceView.VISIBLE
    }

    private fun removeRemoteVideo() {
        remoteVideoView.visibility = SurfaceView.GONE
    }

    private fun joinChannel() {
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
