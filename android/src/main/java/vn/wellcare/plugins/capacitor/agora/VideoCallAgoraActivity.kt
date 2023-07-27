package vn.wellcare.plugins.capacitor.agora

import android.app.Activity
import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import android.util.Rational
import android.view.View
import android.widget.ImageView
import androidx.coordinatorlayout.widget.CoordinatorLayout
import com.getcapacitor.JSObject
import io.agora.rtc2.Constants
import io.agora.rtc2.IRtcEngineEventHandler
import io.agora.rtc2.RtcEngine
import vn.wellcare.plugins.capacitor.agora.`agora-ui-kit`.*
import vn.wellcare.plugins.capacitor.agora.util.Constant
import vn.wellcare.plugins.capacitor.agora.util.IParticipant

class VideoCallAgoraActivity : Activity(), AgoraVideoViewerDelegate {
    var TAG = "CapacitorPluginAgoraPlugin"
    private lateinit var mRtcEngine: RtcEngine

    companion object {
        var agoraVideoVideoViewer: AgoraVideoViewer? = null

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.videocall_agora);

        initAgoraView();
        val data = JSObject(intent.getStringExtra(Constant.JOINROOM).toString())
        val appId = data.getString(Constant.APPID)
//        mRtcEngine = RtcEngine.create(applicationContext, appId, this.networkQualityListener)
        CapacitorPluginAgoraPlugin.sendEvent("onActivity", JSObject().put("isReady", true))
//
//        val persistentbottomSheet = findViewById<LinearLayout>(R.id.bottom_sheet)
//        val behavior: BottomSheetBehavior<*> = BottomSheetBehavior.from<View>(persistentbottomSheet)
    }

    override fun onPictureInPictureModeChanged(isInPictureInPictureMode: Boolean,
                                               newConfig: Configuration) {
        if (isInPictureInPictureMode) {
            CapacitorPluginAgoraPlugin.sendEvent("onSelfAction", JSObject().put("event", "enterPipMode"))
        } else {
            CapacitorPluginAgoraPlugin.sendEvent("onSelfAction", JSObject().put("event", "exitPipMode"))
        }
    }

    override fun onDestroy() {
      super.onDestroy()
      agoraVideoVideoViewer?.clearCountDown()
    }

    private fun initAgoraView() {
        // Get the data passed from the previous activity
        val data = JSObject(intent.getStringExtra(Constant.JOINROOM).toString())
        Log.d(TAG, "initAgoraView - AppData: $data")
        val appId = data.getString(Constant.APPID)
        val token = data.getString(Constant.TOKEN)
        val uid = data.getString(Constant.UID)?.toInt()
        val channel = data.getString(Constant.CHANNELNAME)
        val user = data.getJSObject("user")?.let {
            IParticipant(it)
        }
        try {
            // Set the Agora settings for the video view
            val agoraSettings = AgoraSettings()
            // agoraSettings.colors.micFlag = Color.WHITE
            // Create an Agora video view and add it to the activity's view
            agoraVideoVideoViewer = AgoraVideoViewer(this, AgoraConnectionData(appId!!, token), agoraSettings = agoraSettings, user = user, delegate = this)
        } catch (e: Exception) {
            Log.e(TAG, "initAgoraView", e)
            return
        }
        addContentView(agoraVideoVideoViewer, CoordinatorLayout.LayoutParams(CoordinatorLayout.LayoutParams.MATCH_PARENT, CoordinatorLayout.LayoutParams.MATCH_PARENT))
        // Check if the necessary permissions have been granted
        if (AgoraVideoViewer.requestPermission(this)) {
            Log.d(TAG, "initAgoraView - permissions granted")
            agoraVideoVideoViewer?.user = user
//            agoraVideoVideoViewer?.joinChannelCallBack =
            agoraVideoVideoViewer!!.join(channel
                    ?: "", token, role = Constants.CLIENT_ROLE_BROADCASTER, uid);
        } else {
            Log.e(TAG, "initAgoraView - permissions not granted")
        }
    }

    override fun leftChannel(channel: String) {
        Log.e(TAG, "leftChannel")
        super.leftChannel(channel)
        // Remove the video view from the activity's view and set the delegate to null
        agoraVideoVideoViewer?.delegate = null
        runOnUiThread {
            onBackPressed()
        }
    }

    override fun onBackPressed() {
        // super.onBackPressed()
        return;
    }

    override fun onPictureInPictureModeChanged(
        isInPictureInPictureMode: Boolean,
        newConfig: Configuration?
    ) {
        super.onPictureInPictureModeChanged(isInPictureInPictureMode, newConfig)
        agoraVideoVideoViewer?.setPiPUIVisible(isInPictureInPictureMode)
    }

//    private val networkQualityListener = object : IRtcEngineEventHandler() {
//
//        override fun onNetworkQuality(uid: Int, txQuality: Int, rxQuality: Int) {
//            // Handle network quality updates
//            // Log.d(TAG, "onNetworkQuality: uid = $uid, txQuality = $txQuality, rxQuality = $rxQuality")
//            runOnUiThread {
//                val iconSignal = findViewById<ImageView>(R.id.icon_signal)
//                when {
//                    txQuality == 0 && rxQuality == 0 -> {
//                        iconSignal.setImageResource(R.drawable.no_signal)
//                        iconSignal.visibility = View.VISIBLE
//                    }
//                    txQuality == 1 || rxQuality == 1 -> {
//                        iconSignal.setImageResource(R.drawable.bad_signal)
//                        iconSignal.visibility = View.VISIBLE
//                    }
//                    txQuality == 2 || rxQuality == 2 -> {
//                        iconSignal.setImageResource(R.drawable.medium_signal)
//                        iconSignal.visibility = View.VISIBLE
//                    }
//                    txQuality == 3 || rxQuality == 3 -> {
//                        iconSignal.setImageResource(R.drawable.medium_signal)
//                        iconSignal.visibility = View.VISIBLE
//                    }
//                    txQuality == 4 && rxQuality == 4 -> {
//                        iconSignal.setImageResource(R.drawable.good_signal)
//                        iconSignal.visibility = View.VISIBLE
//                    }
//                    txQuality == 5 && rxQuality == 5 -> {
//                        iconSignal.setImageResource(R.drawable.good_signal)
//                        iconSignal.visibility = View.VISIBLE
//                    }
//                    else -> {
//                        iconSignal.visibility = View.VISIBLE
//                    }
//                }
//            }
//        }
//    }
}
