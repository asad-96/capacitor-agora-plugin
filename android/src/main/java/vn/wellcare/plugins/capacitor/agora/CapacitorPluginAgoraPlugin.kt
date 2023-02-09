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
import vn.wellcare.plugins.capacitor.agora.AgoraActivity
import vn.wellcare.plugins.capacitor.agora.AgoraActivity.Companion.setOnAgoraEvent
import vn.wellcare.plugins.capacitor.agora.AgoraActivity.OnAgoraEvent
import vn.wellcare.plugins.capacitor.agora.util.Constant

@CapacitorPlugin(name = "CapacitorPluginAgora")
class CapacitorPluginAgoraPlugin : Plugin(), IrisEventHandler, OnAgoraEvent {
    var irisApiEngine: IrisApiEngine? = null
    var TAG = "CapacitorPluginAgoraPlugin"
    fun sendEvent(data: JSObject?) {
        notifyListeners("onEventReceived", data)
    }

    @PluginMethod
    fun joinChannel(call: PluginCall) {
        val i = Intent(activity, AgoraActivity::class.java)
        i.putExtra(Constant.CHANNELNAME, call.getString(Constant.CHANNELNAME))
        i.putExtra(Constant.UID, call.getString(Constant.UID))
        i.putExtra(Constant.TOKEN, call.getString(Constant.TOKEN))
        i.putExtra(Constant.APPID, call.getString(Constant.APPID))
        activity.startActivity(i)
        val ret = JSObject()
        call.resolve(ret)
        setOnAgoraEvent(this)
    }

    @PluginMethod
    fun leaveChannel(call: PluginCall) {
        val value = call.getString("room")
        val ret = JSObject()
        call.resolve(ret)
    }

    override fun OnEvent(event: String, data: String, buffee: List<ByteArray>) {
        Log.e("OnEvent", "event: $event data: $data")
        Log.e("OnEvent", "list: " + buffee.size)
    }

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

    override fun onEvent(jsonObject: JSObject?) {
        sendEvent(jsonObject)
    }

    companion object {
        const val NAME = "CapacitorPluginAgora"
    }
}