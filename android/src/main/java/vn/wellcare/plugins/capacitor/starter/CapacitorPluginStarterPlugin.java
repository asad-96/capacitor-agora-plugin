package vn.wellcare.plugins.capacitor.starter;

import com.getcapacitor.JSObject;
import com.getcapacitor.Plugin;
import com.getcapacitor.PluginCall;
import com.getcapacitor.PluginMethod;
import com.getcapacitor.annotation.CapacitorPlugin;

import android.util.Base64;
import androidx.annotation.NonNull;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import io.agora.iris.IrisApiEngine;
import io.agora.iris.IrisEventHandler;

@CapacitorPlugin(name = "CapacitorPluginStarter")
public class CapacitorPluginStarterPlugin extends Plugin implements IrisEventHandler {
  public static final String NAME = "CapacitorPluginStarter";
  public IrisApiEngine irisApiEngine;
  private CapacitorPluginStarter implementation = new CapacitorPluginStarter();

  @PluginMethod
  public void echo(PluginCall call) {
    String value = call.getString("value");

    JSObject ret = new JSObject();
    ret.put("value", implementation.echo(value));
    call.resolve(ret);
  }
}
