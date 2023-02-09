package vn.wellcare.plugins.capacitor.starter

import android.Manifest
import android.app.PictureInPictureParams
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.graphics.Point
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.util.Rational
import android.view.LayoutInflater
import android.view.SurfaceView
import android.view.View
import android.view.WindowManager
import android.widget.FrameLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.getcapacitor.JSObject
import io.agora.rtc2.*
import io.agora.rtc2.video.VideoCanvas
import vn.wellcare.plugins.capacitor.agora.R
import vn.wellcare.plugins.capacitor.starter.util.Constant
import java.util.*

class AgoraActivity : AppCompatActivity() {
    //A flag that will update the status of channel joining
    private var isJoined = false
    private var agoraEngine: RtcEngine? = null

    //SurfaceView to render local video in a Container.
    private var localSurfaceView: SurfaceView? = null

    //SurfaceView to render Remote video in a Container.
    private var remoteSurfaceView: SurfaceView? = null

    //COMMONS
    private val EVENT = "event"
    private val UID = "uid"
    private val ELAPSED = "elapsed"
    private val CHANNEL = "channel"
    private val REASON = "reason"

    /*
        This event listener will update the events stated in @IRtcEngineEventHandler and send it back to
        JS code
     */
    interface OnAgoraEvent {
        fun onEvent(jsonObject: JSObject?)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_agora)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        // If all the permissions are granted, initialize the RtcEngine object and join a channel.
        if (!checkSelfPermission()) {
            ActivityCompat.requestPermissions(this, REQUESTED_PERMISSIONS, PERMISSION_REQ_ID)
            return
        }
        init()
    }

    /*
        This will set the video sdk engine in order to join channel
     */
    private fun init() {
        val channelName = intent.getStringExtra(Constant.CHANNELNAME)
        val uid = intent.getStringExtra(Constant.UID)
        val uidN: Int
        val rand = Random()
        uidN = if (!TextUtils.isEmpty(uid)) {
            try {
                uid!!.toInt()
            } catch (e: NumberFormatException) {
                e.printStackTrace()
                rand.nextInt(20)
            }
        } else {
            rand.nextInt(20)
        }
        val token = intent.getStringExtra(Constant.TOKEN)
        val appId = intent.getStringExtra(Constant.APPID)
        setupVideoSDKEngine(appId)
        joinChannel(token, channelName, uidN)
    }

    //set up video RTC engine
    private fun setupVideoSDKEngine(appId: String?) {
        try {
            val config = RtcEngineConfig()
            config.mContext = baseContext
            config.mAppId = appId
            config.mEventHandler = mRtcEventHandler
            agoraEngine = RtcEngine.create(config)
            agoraEngine.setLogFilter(Constants.LOG_FILTER_OFF)
            agoraEngine.setLogLevel(Constants.LOG_FILTER_OFF)
            // By default, the video module is disabled, call enableVideo to enable it.
            agoraEngine.enableVideo()
        } catch (e: Exception) {
            e.printStackTrace()
            Log.e("setupVideoSDKEngine err", e.message!!)
        }
    }

    /**
     * Occurs when a remote user (Communication)/host (Live Broadcast) joins the channel.
     *
     *
     * Communication profile: This callback notifies the app when another user joins the channel. If other users are already in the channel, the SDK also reports to the app on the existing users.
     * Live Broadcast profile: This callback notifies the app when the host joins the channel. If other hosts are already in the channel, the SDK also reports to the app on the existing hosts. We recommend having at most 17 hosts in a channel
     *
     *
     * The SDK triggers this callback under one of the following circumstances:
     *
     *
     * A remote user/host joins the channel by calling the joinChannel method.
     * A remote user switches the user role to the host by calling the setClientRole method after joining the channel.
     * A remote user/host rejoins the channel after a network interruption.
     * The host injects an online media stream into the channel by calling the addInjectStreamUrl method.
     *
     * @param uid ID of the user or host who joins the channel.
     * @param elapsed Time delay (ms) from the local user calling joinChannel/setClientRole until this callback is triggered.
     */
    private val mRtcEventHandler: IRtcEngineEventHandler = object : IRtcEngineEventHandler() {
        // Listen for the remote host joining the channel to get the uid of the host.
        override fun onUserJoined(uid: Int, elapsed: Int) {
            if (onAgoraEvent != null) {
                val jsObject = JSObject()
                jsObject.put(EVENT, "onUserJoined")
                jsObject.put(UID, uid)
                jsObject.put(ELAPSED, elapsed)
                onAgoraEvent!!.onEvent(jsObject)
            }
            // Set the remote video view
            runOnUiThread { setupRemoteVideo(uid) }
        }

        override fun onJoinChannelSuccess(channel: String, uid: Int, elapsed: Int) {
            isJoined = true
            if (onAgoraEvent != null) {
                val jsObject = JSObject()
                jsObject.put(EVENT, "onJoinChannelSuccess")
                jsObject.put(CHANNEL, channel)
                jsObject.put(UID, uid)
                jsObject.put(ELAPSED, elapsed)
                onAgoraEvent!!.onEvent(jsObject)
            }
        }

        /**
         * Occurs when a remote user (Communication)/host (Live Broadcast) leaves the channel.
         *
         * There are two reasons for users to become offline:
         *
         * Leave the channel: When the user/host leaves the channel, the user/host sends a goodbye message. When this message is received, the SDK determines that the user/host leaves the channel.
         * Drop offline: When no data packet of the user or host is received for a certain period of time (20 seconds for the communication profile, and more for the live broadcast profile), the SDK assumes that the user/host drops offline. A poor network connection may lead to false detections, so we recommend using the Agora RTM SDK for reliable offline detection.
         *
         * @param uid ID of the user or host who leaves the channel or goes offline.
         * @param reason Reason why the user goes offline:
         *
         * USER_OFFLINE_QUIT(0): The user left the current channel.
         * USER_OFFLINE_DROPPED(1): The SDK timed out and the user dropped offline because no data packet was received within a certain period of time. If a user quits the call and the message is not passed to the SDK (due to an unreliable channel), the SDK assumes the user dropped offline.
         * USER_OFFLINE_BECOME_AUDIENCE(2): (Live broadcast only.) The client role switched from the host to the audience.
         */
        override fun onUserOffline(uid: Int, reason: Int) {
            if (onAgoraEvent != null) {
                val jsObject = JSObject()
                jsObject.put(EVENT, "onUserOffline")
                jsObject.put(UID, uid)
                jsObject.put(REASON, reason)
                onAgoraEvent!!.onEvent(jsObject)
            }
            runOnUiThread { remoteSurfaceView!!.visibility = View.GONE }
        }

        /**
         * Reports an error during SDK runtime.
         *
         * In most cases, the SDK cannot fix the issue and resume running. The SDK requires the app to take action or informs the user about the issue.
         *
         * For example, the SDK reports an ERR_START_CALL error when failing to initialize a call. The app informs the user that the call initialization failed and invokes the leaveChannel method to leave the channel. For detailed error codes, see Error Codes.
         *
         * @param error Error Code
         */
        override fun onError(err: Int) {
            super.onError(err)
        }

        override fun onEncryptionError(errorType: Int) {
            super.onEncryptionError(errorType)
        }

        override fun onPermissionError(permission: Int) {
            super.onPermissionError(permission)
        }

        /**
         * Occurs when the local user fails to receive a remote data stream.
         *
         * The SDK triggers this callback when the local user fails to receive the stream message that the remote user sends by calling the sendStreamMessage method.
         *
         * @param uid User ID of the remote user sending the data stream.
         * @param streamId Stream ID.
         * @param error Error Code.
         * @param missed The number of lost messages.
         * @param cached The number of incoming cached messages when the data stream is interrupted.
         */
        override fun onStreamMessageError(
            uid: Int,
            streamId: Int,
            error: Int,
            missed: Int,
            cached: Int
        ) {
            super.onStreamMessageError(uid, streamId, error, missed, cached)
        }

        /**
         * Occurs when a user leaves the channel.
         *
         * When the app calls the leaveChannel method, the SDK uses this callback to notify the app when the user leaves the channel.
         *
         * With this callback, the application retrieves the channel information, such as the call duration and statistics.
         *
         * @param stats Statistics of the call: RtcStats
         */
        override fun onLeaveChannel(stats: RtcStats) {
            super.onLeaveChannel(stats)
            if (onAgoraEvent != null) {
                val jsObject = JSObject()
                jsObject.put(EVENT, "onLeaveChannel")
                jsObject.put(UID, stats.users)
                onAgoraEvent!!.onEvent(jsObject)
            }
        }
    }

    fun showMessage(message: String?) {
        runOnUiThread { Toast.makeText(applicationContext, message, Toast.LENGTH_SHORT).show() }
    }

    //This will set the other user video at screen, after other user joined to the channel
    private fun setupRemoteVideo(uid: Int) {
        val container = findViewById<FrameLayout>(R.id.remote_video_view_container)
        remoteSurfaceView = SurfaceView(baseContext)
        remoteSurfaceView!!.setZOrderMediaOverlay(true)
        container.addView(remoteSurfaceView)
        agoraEngine!!.setupRemoteVideo(
            VideoCanvas(
                remoteSurfaceView,
                VideoCanvas.RENDER_MODE_FIT,
                uid
            )
        )
        // Display RemoteSurfaceView.
        remoteSurfaceView!!.visibility = View.VISIBLE
    }

    //This will set the current local user camera preview to the surface
    private fun setupLocalVideo() {
        // Create a SurfaceView object and add it as a child to the FrameLayout.
        localSurfaceView = SurfaceView(this)
        val container = findViewById<FrameLayout>(R.id.local_video_view_container)
        if (container != null) {
            container.addView(localSurfaceView)
            // Pass the SurfaceView object to Agora so that it renders the local video.
            agoraEngine!!.setupLocalVideo(
                VideoCanvas(
                    localSurfaceView,
                    VideoCanvas.RENDER_MODE_HIDDEN,
                    0
                )
            )
            container.visibility = View.VISIBLE
            localSurfaceView!!.visibility = View.VISIBLE
        }
    }

    fun joinChannel(token: String?, channelName: String?, uid: Int?) {
        if (checkSelfPermission()) {
            val options = ChannelMediaOptions()

            // For a Video call, set the channel profile as COMMUNICATION.
            options.channelProfile = Constants.CHANNEL_PROFILE_COMMUNICATION
            // Set the client role as BROADCASTER or AUDIENCE according to the scenario.
            options.clientRoleType = Constants.CLIENT_ROLE_BROADCASTER
            // Display LocalSurfaceView.
            setupLocalVideo()
            localSurfaceView!!.visibility = View.VISIBLE
            // Start local preview.
            agoraEngine!!.startPreview()
            // Join the channel with a temp token.
            // You need to specify the user ID yourself, and ensure that it is unique in the channel.
            agoraEngine!!.joinChannel(token, channelName, uid!!, options)
        } else {
            Toast.makeText(applicationContext, "Permissions was not granted", Toast.LENGTH_SHORT)
                .show()
        }
    }

    override fun onBackPressed() {
        onUserLeaveHint()
    }

    fun leaveChannel(view: View?) {
        if (!isJoined) {
            showMessage("Join a channel first")
        } else {
            agoraEngine!!.leaveChannel()
            showMessage("You left the channel")
            // Stop remote video rendering.
            if (remoteSurfaceView != null) remoteSurfaceView!!.visibility = View.GONE
            // Stop local video rendering.
            if (localSurfaceView != null) localSurfaceView!!.visibility = View.GONE
            isJoined = false
            finish()
        }
    }

    override fun onUserLeaveHint() {
        super.onUserLeaveHint()
    }

    override fun onPictureInPictureModeChanged(
        isInPictureInPictureMode: Boolean,
        newConfig: Configuration
    ) {
        super.onPictureInPictureModeChanged(isInPictureInPictureMode, newConfig)
        if (isInPictureInPictureMode) {
            findViewById<View>(R.id.LeaveButton).visibility = View.GONE
        } else {
            findViewById<View>(R.id.LeaveButton).visibility = View.VISIBLE
        }
    }

    public override fun onDestroy() {
        super.onDestroy()
    }

    private fun checkSelfPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            REQUESTED_PERMISSIONS[0]
        ) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(
                    this,
                    REQUESTED_PERMISSIONS[1]
                ) == PackageManager.PERMISSION_GRANTED
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_REQ_ID) {
            // Checking whether user granted the permission or not.
            if (grantResults.size > 0 && grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                showMessage("Audio permission not granted")
            }
            if (grantResults.size > 1 && grantResults[1] != PackageManager.PERMISSION_GRANTED) {
                showMessage("Camera permission not granted")
            }
            if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults.size > 1 && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                init()
            }
        }
    }
    
    companion object {
        var onAgoraEvent: OnAgoraEvent? = null
        @JvmStatic
        fun setOnAgoraEvent(onAgoraEvent1: OnAgoraEvent?) {
            onAgoraEvent = onAgoraEvent1
        }

        private const val PERMISSION_REQ_ID = 22
        private val REQUESTED_PERMISSIONS = arrayOf(
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.CAMERA
        )
    }
}