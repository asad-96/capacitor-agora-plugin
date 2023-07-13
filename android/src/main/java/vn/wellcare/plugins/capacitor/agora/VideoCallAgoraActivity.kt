package vn.wellcare.plugins.capacitor.agora

import android.app.Activity
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.FrameLayout
import android.widget.LinearLayout
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.content.ContextCompat
import com.getcapacitor.JSObject
import com.google.android.material.bottomsheet.BottomSheetBehavior
import io.agora.rtc2.Constants
import vn.wellcare.plugins.capacitor.agora.`agora-ui-kit`.*
import vn.wellcare.plugins.capacitor.agora.util.Constant
import vn.wellcare.plugins.capacitor.agora.util.IParticipant

class VideoCallAgoraActivity : Activity(), AgoraVideoViewerDelegate {
    var TAG = "CapacitorPluginAgoraPlugin"
    companion object {
        var agoraVideoVideoViewer: AgoraVideoViewer? = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.videocall_agora);

        initAgoraView();
        CapacitorPluginAgoraPlugin.sendEvent("onActivity", JSObject().put("isReady", true))
//
//        val persistentbottomSheet = findViewById<LinearLayout>(R.id.bottom_sheet)
//        val behavior: BottomSheetBehavior<*> = BottomSheetBehavior.from<View>(persistentbottomSheet)
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
            agoraVideoVideoViewer!!.join(channel ?: "", token, role = Constants.CLIENT_ROLE_BROADCASTER, uid);
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
}