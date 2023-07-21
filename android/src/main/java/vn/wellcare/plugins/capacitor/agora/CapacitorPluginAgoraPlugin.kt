package vn.wellcare.plugins.capacitor.agora

import android.content.Intent
import android.os.Build
import android.util.Log
import android.widget.Toast
import com.getcapacitor.JSObject
import com.getcapacitor.Plugin
import com.getcapacitor.PluginCall
import com.getcapacitor.PluginMethod
import com.getcapacitor.annotation.CapacitorPlugin
import io.agora.iris.IrisApiEngine
import io.agora.iris.IrisEventHandler
import vn.wellcare.plugins.capacitor.agora.util.Constant
import vn.wellcare.plugins.capacitor.agora.util.IParticipant
import android.os.Handler
import androidx.annotation.RequiresApi

@CapacitorPlugin(name = "CapacitorPluginAgora")
class CapacitorPluginAgoraPlugin : Plugin() {
    var TAG = "CapacitorPluginAgoraPlugin"
    private var mParticipants: List<IParticipant> = mutableListOf()

    @OptIn(ExperimentalUnsignedTypes::class)
    @RequiresApi(Build.VERSION_CODES.O)
    @PluginMethod
    fun enterPictureInPictureMode(call: PluginCall) {
        VideoCallAgoraActivity.agoraVideoVideoViewer?.enterPictureInPicture()
        call.resolve()
    }
    
    @PluginMethod
    fun joinChannel(call: PluginCall) {
        try {
            Log.d(TAG, "joinChannel")
            // Get the data passed from the client app and start a new activity to join the channel
            val data = call.data.toString()
            val i = Intent(activity, VideoCallAgoraActivity::class.java)
            i.putExtra(Constant.JOINROOM, data)
            activity.startActivity(i)
            // Set the callback for when the user joins the channel
            VideoCallAgoraActivity.agoraVideoVideoViewer?.joinChannelCallBack =
                    { uid: UInt, message: String? ->
                        val ret = JSObject()
                        ret.put(Constant.UID, uid)
                        call.resolve(ret)
                    }
            // Update the participant list after a delay of 10 seconds
            Handler().postDelayed({
                VideoCallAgoraActivity.agoraVideoVideoViewer?.updateParticipantLists(this.mParticipants)
            }, 10000)
        } catch (e: Exception) {
            Toast.makeText(context, e.message, Toast.LENGTH_LONG).show()
        }
    }

    @PluginMethod
    fun leaveChannel(call: PluginCall) {
        Log.d(TAG, "leaveChannel")
        // Leave the channel and stop the video preview
        val value = call.getString("room")
        val ret = JSObject()
        call.resolve(ret)
        // VideoCallAgoraActivity.agoraVideoVideoViewer?.agkit?.stopPreview()
        // VideoCallAgoraActivity.agoraVideoVideoViewer?.agkit?.leaveChannel()
        VideoCallAgoraActivity.agoraVideoVideoViewer?.leaveChannel()
        VideoCallAgoraActivity.agoraVideoVideoViewer?.close()
    }

    @PluginMethod
    fun updateParticipantLists(call: PluginCall) {
        Log.d(TAG, "updateParticipantLists")
        // Update the participant list with the data passed from the client app
        val jsonArray = call.getArray("participants")
        val participants = mutableListOf<IParticipant>()

        for (i in 0 until jsonArray.length()) {
            val jsObject = jsonArray.getJSONObject(i)
            val participant = IParticipant(JSObject(jsObject.toString()))
            participants.add(participant)
        }
        this.mParticipants = participants
        VideoCallAgoraActivity.agoraVideoVideoViewer?.updateParticipantLists(this.mParticipants)
        val ret = JSObject()
        call.resolve(ret)
    }

    @PluginMethod
    fun showAlert(call: PluginCall) {
        var text = call.getString("alertText", "this is default text alert !")
        VideoCallAgoraActivity.agoraVideoVideoViewer?.showAlert(text ?: "ALERT!")
    }

    @PluginMethod
    fun setCountdown(call: PluginCall) {
        var duration = call.getInt("seconds", 60)
        VideoCallAgoraActivity.agoraVideoVideoViewer?.countDown(duration?:60)
    }

    @PluginMethod
    fun showRecordingStatus(call: PluginCall) {
        VideoCallAgoraActivity.agoraVideoVideoViewer?.toggleRecordingIcon(call.getBoolean("isShown", false) ?: false)
    }
    //    override fun OnEvent(event: String, data: String, buffee: List<ByteArray>) {
    //        Log.e("OnEvent", "event: $event data: $data")
    //        Log.e("OnEvent", "list: " + buffee.size)
    //    }

    protected fun runOnUIThread(runnable: Runnable?) {
        runOnUIThread(runnable)
    }

    companion object {
        const val NAME = "CapacitorPluginAgora"
        var instance: CapacitorPluginAgoraPlugin? = null
            private set

        // Send an event with the specified data to the client app
        fun sendEvent(event: String, data: JSObject?) {
            instance?.notifyListeners(event, data)
        }
    }

    init {
        instance = this
    }
}
