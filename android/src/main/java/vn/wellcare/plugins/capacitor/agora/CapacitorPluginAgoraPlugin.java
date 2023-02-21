package vn.wellcare.plugins.capacitor.agora;

import static vn.wellcare.plugins.capacitor.agora.AgoraActivity.setOnAgoraEvent;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.getcapacitor.JSObject;
import com.getcapacitor.Plugin;
import com.getcapacitor.PluginCall;
import com.getcapacitor.PluginMethod;
import com.getcapacitor.annotation.CapacitorPlugin;

import java.util.List;

import io.agora.iris.IrisApiEngine;
import io.agora.iris.IrisEventHandler;
import vn.wellcare.plugins.capacitor.agora.util.Constant;

@CapacitorPlugin(name = "CapacitorPluginAgora")
public class CapacitorPluginAgoraPlugin
        extends Plugin
        implements IrisEventHandler, AgoraActivity.OnAgoraEvent {

    public static final String NAME = "CapacitorPluginAgora";
    public IrisApiEngine irisApiEngine;
    private CapacitorPluginAgora implementation = new CapacitorPluginAgora();

    String TAG = "CapacitorPluginAgoraPlugin";


    public void sendEvent(JSObject data) {
        notifyListeners("onEventReceived", data);
    }

    @PluginMethod
    public void joinChannel(PluginCall call) {
        try {
            System.out.println("call plugin funtion");
            Intent i = new Intent(getActivity(), VideoCallAgoraActivity.class);
            i.putExtra(Constant.CHANNELNAME, call.getString(Constant.CHANNELNAME));
            i.putExtra(Constant.UID, call.getString(Constant.UID));
            i.putExtra(Constant.TOKEN, call.getString(Constant.TOKEN));
            i.putExtra(Constant.APPID, call.getString(Constant.APPID));
            getActivity().startActivity(i);
            JSObject ret = new JSObject();
            call.resolve(ret);
            setOnAgoraEvent(this);
        } catch (Exception e) {
            Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    @PluginMethod
    public void leaveChannel(PluginCall call) {
        String value = call.getString("room");
        JSObject ret = new JSObject();
        ret.put("value", implementation.echo(value));
        call.resolve(ret);
    }

    @Override
    public void OnEvent(String event, String data, List<byte[]> buffee) {
        Log.e("OnEvent", "event: " + event + " data: " + data);
        Log.e("OnEvent", "list: " + buffee.size());
    }

    protected final void showLongToast(final String msg) {
        runOnUIThread(() -> {
            Context context = getContext();
            if (context == null) {
                return;
            }
            Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
        });
    }

    protected final void showShortToast(final String msg) {
        runOnUIThread(() -> {
            Context context = getContext();
            if (context == null) {
                return;
            }
            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
        });
    }

    protected final void runOnUIThread(Runnable runnable) {
        this.runOnUIThread(runnable);
    }


    @Override
    public void onEvent(JSObject jsonObject) {
        sendEvent(jsonObject);
    }
}
