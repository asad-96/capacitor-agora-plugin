package vn.wellcare.plugins.capacitor.starter;

import static vn.wellcare.plugins.capacitor.starter.AgoraActivity.setOnAgoraEvent;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.getcapacitor.Bridge;
import com.getcapacitor.JSObject;
import com.getcapacitor.Plugin;
import com.getcapacitor.PluginCall;
import com.getcapacitor.PluginMethod;
import com.getcapacitor.annotation.CapacitorPlugin;

import org.json.JSONObject;

import io.agora.iris.IrisApiEngine;
import io.agora.iris.IrisEventHandler;
import io.agora.rtc2.IRtcEngineEventHandler;
import io.agora.rtc2.RtcEngine;
import vn.wellcare.plugins.capacitor.agora.R;

import java.util.List;

@CapacitorPlugin(name = "CapacitorPluginAgora")
public class CapacitorPluginAgoraPlugin
  extends Plugin
  implements IrisEventHandler, AgoraActivity.OnAgoraEvent {

  public static final String NAME = "CapacitorPluginAgora";
  public IrisApiEngine irisApiEngine;
  private CapacitorPluginAgora implementation = new CapacitorPluginAgora();

  String TAG="CapacitorPluginAgoraPlugin";


  public void sendEvent(JSObject data) {
    notifyListeners("onEventReceived", data);
  }

  @PluginMethod
  public void joinChannel(PluginCall call) {
    String room = call.getString("room");
    Integer uid = call.getInt("uid");

    Intent i = new Intent(getActivity(), AgoraActivity.class);
    i.putExtra("room",room);
    i.putExtra("uid",uid);
    getActivity().startActivity(i);
    
    JSObject ret = new JSObject();
    call.resolve(ret);
    setOnAgoraEvent(this);
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
    Log.e("OnEvent","event: "+event+" data: "+data);
    Log.e("OnEvent","list: "+buffee.size());
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
