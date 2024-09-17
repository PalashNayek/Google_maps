package com.palash.google_maps.broadcast

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.location.LocationManager
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

class LocationProviderReceiver : BroadcastReceiver() {

    private var _islocationData = MutableLiveData<String>()
    val isLocationData: LiveData<String> get() = _islocationData

    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent?.action == LocationManager.PROVIDERS_CHANGED_ACTION) {
            // Location provider setting has changed
            val locationManager = context?.getSystemService(Context.LOCATION_SERVICE) as LocationManager
            val isLocationEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)

            if (isLocationEnabled) {
                // Location is enabled
                //isLocationData = "enable"
                _islocationData.value="enable"
                //Toast.makeText(context, "Location is enabled", Toast.LENGTH_SHORT).show()
            } else {
                // Location is disabled
                //isLocationData = "disable"
                _islocationData.value="disable"
                //Toast.makeText(context, "Location is disabled", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
