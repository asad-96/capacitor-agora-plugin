package vn.wellcare.plugins.capacitor.starter;
import android.util.Log;

public class CapacitorPluginStarter  {

    public String echo(String value) {
        Log.i("CapacitorPluginStarter", "Echo" + value);
        return "Echo: " + value;
    }

}
