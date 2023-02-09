package vn.wellcare.plugins.capacitor.starter

import android.util.Log

class CapacitorPluginAgora {
    fun echo(value: String): String {
        Log.i("CapacitorPluginAgora", "Echo$value")
        return "Echo: $value"
    }
}