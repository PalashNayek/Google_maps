package com.palash.google_maps.google_maps

import android.Manifest
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Location
import android.location.LocationManager
import androidx.fragment.app.Fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat.registerReceiver
import androidx.lifecycle.Observer
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions
import com.palash.google_maps.R
import com.palash.google_maps.broadcast.LocationProviderReceiver
import com.palash.google_maps.databinding.FragmentMapsBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MapsFragment : Fragment() {

    private var _binding: FragmentMapsBinding? = null
    private val binding get() = _binding!!

    private var mapFragment: SupportMapFragment? = null

    private lateinit var lastLocation: Location
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var mMap: GoogleMap
    private val locationProviderReceiver = LocationProviderReceiver()

    companion object {
        private const val LOCATION_REQUEST_CODE = 1
    }


    private val callback = OnMapReadyCallback { googleMap ->
        mMap = googleMap
        //mMap.uiSettings.isZoomControlsEnabled = true
        mMap.uiSettings.isMyLocationButtonEnabled = false
        //mMap.setOnMarkerClickListener(mMap)

        setUpMap()

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMapsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(callback)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(context!!)
        activity?.registerReceiver(
            locationProviderReceiver,
            IntentFilter(LocationManager.PROVIDERS_CHANGED_ACTION)
        )

        locationProviderReceiver.isLocationData.observe(viewLifecycleOwner, Observer {

            /*if (it.equals("enable")) {


            }*/
            Toast.makeText(context, "$it", Toast.LENGTH_SHORT).show()
        })


    }

    private fun setUpMap() {
        if (ActivityCompat.checkSelfPermission(
                context!!,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
            &&
            ActivityCompat.checkSelfPermission(
                context!!,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_REQUEST_CODE
            )
            mapFragment?.getMapAsync(callback)
            fusedLocationClient = LocationServices.getFusedLocationProviderClient(context!!)
            return
        }

        mMap.isMyLocationEnabled = true
        fusedLocationClient.lastLocation.addOnSuccessListener(requireActivity()) { location ->
            if (location != null) {
                lastLocation = location
                val currentLatLong = LatLng(location.latitude, location.longitude)
                placeMarkerOnMap(currentLatLong)
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLong, 18f))
           //     mMap.addPolyline(PolylineOptions().add(LatLng(lastLocation.latitude, lastLocation.longitude), LatLng(22.508670737180257, 88.39503432012938)).width(2f).color(Color.BLACK))
            } else {
                /*mapFragment?.getMapAsync(callback)
                fusedLocationClient = LocationServices.getFusedLocationProviderClient(context!!)*/

                Toast.makeText(context, "Please turn on your device location", Toast.LENGTH_SHORT)
                    .show()
            }
        }

    }

    private fun placeMarkerOnMap(currentLatLong: LatLng) {

        val markerOptions = MarkerOptions().position(currentLatLong)
        markerOptions.title("$currentLatLong")
        mMap.addMarker(markerOptions)
    }

    //override fun onMarkerClick(p0: Marker) = false

    override fun onDestroyView() {
        super.onDestroyView()
        activity?.unregisterReceiver(locationProviderReceiver)
        _binding = null
    }
}