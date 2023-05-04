package vn.wellcare.plugins.capacitor.agora

import android.content.Intent
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

@CapacitorPlugin(name = "CapacitorPluginAgora")
class CapacitorPluginAgoraPlugin : Plugin() {
    var TAG = "CapacitorPluginAgoraPlugin"
    private var mParticipants: List<IParticipant> = mutableListOf()
    fun sendEvent(data: JSObject?) {
        notifyListeners("onEventReceived", data)
    }

    @PluginMethod
    fun joinChannel(call: PluginCall) {
        try {
            println("call plugin funtion")
            val data = call.data.toString()
            val i = Intent(activity, VideoCallAgoraActivity::class.java)
            i.putExtra(Constant.JOINROOM, data)
            activity.startActivity(i)
            VideoCallAgoraActivity.agoraVideoVideoViewer?.joinChannelCallBack =
                { uid: UInt, message: String? ->
                    val ret = JSObject()
                    ret.put(Constant.UID, uid)
                    call.resolve(ret)
                }

            Handler().postDelayed({
                VideoCallAgoraActivity.agoraVideoVideoViewer?.updateParticipantLists(this.mParticipants)
            }, 10000)
        } catch (e: Exception) {
            Toast.makeText(context, e.message, Toast.LENGTH_LONG).show()
        }
    }

    @PluginMethod
    fun leaveChannel(call: PluginCall) {
        val value = call.getString("room")
        val ret = JSObject()
        call.resolve(ret)
    }

    @PluginMethod
    fun updateParticipantLists(call: PluginCall) {
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

//    override fun OnEvent(event: String, data: String, buffee: List<ByteArray>) {
//        Log.e("OnEvent", "event: $event data: $data")
//        Log.e("OnEvent", "list: " + buffee.size)
//    }

    protected fun showLongToast(msg: String?) {
        runOnUIThread {
            val context = context ?: return@runOnUIThread
            Toast.makeText(context, msg, Toast.LENGTH_LONG).show()
        }
    }

    protected fun showShortToast(msg: String?) {
        runOnUIThread {
            val context = context ?: return@runOnUIThread
            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
        }
    }

    protected fun runOnUIThread(runnable: Runnable?) {
        runOnUIThread(runnable)
    }

    companion object {
        const val NAME = "CapacitorPluginAgora"
    }
}