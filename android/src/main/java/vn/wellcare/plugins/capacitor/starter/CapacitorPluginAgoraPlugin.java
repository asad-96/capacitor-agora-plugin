package vn.wellcare.plugins.capacitor.starter;

import android.content.Intent;

import com.getcapacitor.JSObject;
import com.getcapacitor.Plugin;
import com.getcapacitor.PluginCall;
import com.getcapacitor.PluginMethod;
import com.getcapacitor.annotation.CapacitorPlugin;
import io.agora.iris.IrisApiEngine;
import io.agora.iris.IrisEventHandler;

import java.util.List;

@CapacitorPlugin(name = "CapacitorPluginAgora")
public class CapacitorPluginAgoraPlugin
  extends Plugin
  implements IrisEventHandler {

  public static final String NAME = "CapacitorPluginAgora";
  public IrisApiEngine irisApiEngine;
  private CapacitorPluginAgora implementation = new CapacitorPluginAgora();

  @PluginMethod
  public void echo(PluginCall call) {
    String value = call.getString("value");

    JSObject ret = new JSObject();
    ret.put("value", implementation.echo(value));
    call.resolve(ret);
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
  }

 @PluginMethod
 public void leaveChannel(PluginCall call) {
   String value = call.getString("room");
   JSObject ret = new JSObject();
   ret.put("value", implementation.echo(value));
   call.resolve(ret);
 }

  @Override
  public void OnEvent(String s, String s1, List<byte[]> list) {

  }

}
