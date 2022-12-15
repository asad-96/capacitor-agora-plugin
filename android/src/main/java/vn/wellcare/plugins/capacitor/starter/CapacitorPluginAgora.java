package vn.wellcare.plugins.capacitor.starter;

import android.util.Log;

public class CapacitorPluginAgora {

  public String echo(String value) {
    Log.i("CapacitorPluginAgora", "Echo" + value);
    return "Echo: " + value;
  }
}
