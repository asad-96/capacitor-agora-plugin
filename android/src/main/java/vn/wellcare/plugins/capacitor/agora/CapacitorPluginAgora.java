package vn.wellcare.plugins.capacitor.agora;

import android.util.Log;

public class CapacitorPluginAgora {

  public String echo(String value) {
    Log.i("CapacitorPluginAgora", "Echo" + value);
    return "Echo: " + value;
  }
}