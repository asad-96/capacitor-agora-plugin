package vn.wellcare.plugins.capacitor.starter;

import android.util.Base64;
import androidx.annotation.NonNull;
import com.getcapacitor.JSObject;
import com.getcapacitor.Plugin;
import com.getcapacitor.PluginCall;
import com.getcapacitor.PluginMethod;
import com.getcapacitor.annotation.CapacitorPlugin;
import io.agora.iris.IrisApiEngine;
import io.agora.iris.IrisEventHandler;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONException;
import org.json.JSONObject;

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
}
