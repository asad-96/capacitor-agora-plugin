package vn.wellcare.plugins.capacitor.agora

import android.app.Activity
import android.graphics.Color
import android.os.Bundle
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
    companion object {
        var agoraVideoVideoViewer: AgoraVideoViewer? = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.videocall_agora);

        initAgoraView();
//
//        val persistentbottomSheet = findViewById<LinearLayout>(R.id.bottom_sheet)
//        val behavior: BottomSheetBehavior<*> = BottomSheetBehavior.from<View>(persistentbottomSheet)
    }

    private fun initAgoraView() {
        val data = JSObject(intent.getStringExtra(Constant.JOINROOM).toString())
        println("AppData: $data")
        val appId = data.getString(Constant.APPID)
        val token = data.getString(Constant.TOKEN)
        val uid = data.getString(Constant.UID)?.toInt()
        val channel = data.getString(Constant.CHANNELNAME)
        val user = data.getJSObject("user")?.let {
            IParticipant(it)
        }
        try {
            val agoraSettings = AgoraSettings()
            agoraSettings.colors.micFlag = Color.WHITE
            agoraVideoVideoViewer = AgoraVideoViewer(this, AgoraConnectionData(appId!!, token), agoraSettings = agoraSettings, user = user, delegate = this)
        } catch (e: Exception) {
            println("Error when create AgoraVideoViewer")
            return
        }
        addContentView(agoraVideoVideoViewer, CoordinatorLayout.LayoutParams(CoordinatorLayout.LayoutParams.MATCH_PARENT, CoordinatorLayout.LayoutParams.MATCH_PARENT))
        if (AgoraVideoViewer.requestPermission(this)) {
            println("join channel")
            agoraVideoVideoViewer?.user = user
//            agoraVideoVideoViewer?.joinChannelCallBack =
            agoraVideoVideoViewer!!.join(channel ?: "", token, role = Constants.CLIENT_ROLE_BROADCASTER, uid);
        } else {
            println("Error")
        }
    }

    override fun leftChannel(channel: String) {
        super.leftChannel(channel)
        onBackPressed()
    }
}