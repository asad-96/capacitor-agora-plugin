package vn.wellcare.plugins.capacitor.agora

import vn.wellcare.plugins.capacitor.agora.`agora-ui-kit`.AgoraConnectionData
import vn.wellcare.plugins.capacitor.agora.`agora-ui-kit`.AgoraVideoViewer
import vn.wellcare.plugins.capacitor.agora.`agora-ui-kit`.requestPermission
import vn.wellcare.plugins.capacitor.agora.util.Constant
import android.app.Activity
import android.os.Bundle
import android.widget.FrameLayout
import io.agora.rtc2.Constants

class VideoCallAgoraActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.videocall_agora);

        initAgoraView();
    }

    private fun initAgoraView() {
        val appId = intent.getStringExtra(Constant.APPID).toString();
        val token = intent.getStringExtra(Constant.TOKEN).toString();
        val uid = intent.getStringExtra(Constant.UID)?.toInt();
        val channel = intent.getStringExtra(Constant.CHANNELNAME).toString();
//        val appId = "ba4643f2b3a145f29575b8783d3a5ec1";
//        val token = "007eJxTYGhnX9YeWXCkQrqRX+r8psMuG5tX/4wLt06163otsY5bMl2BISnRxMzEOM0oyTjR0MQ0zcjS1Nw0ycLcwjjFONE0NdnwwJ23yQ2BjAyKT+ayMjJAIIjPylCSWlxiyMAAABhhH0A=";
//        var channel = "test1";
//        var uid = 0;
        var agoraVideoVideoViewer: AgoraVideoViewer? = null;
        try {
            agoraVideoVideoViewer = AgoraVideoViewer(this, AgoraConnectionData(appId));
        } catch (e: Exception) {
            println("Error when create AgoraVideoViewer")
            return
        }
        addContentView(agoraVideoVideoViewer, FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT))
        if (AgoraVideoViewer.requestPermission(this)) {
            println("join channel")
            agoraVideoVideoViewer!!.join(channel, token, role = Constants.CLIENT_ROLE_BROADCASTER, uid);
        } else {
            println("Error")
        }
    }
}