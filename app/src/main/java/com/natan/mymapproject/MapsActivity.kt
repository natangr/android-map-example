package com.natan.mymapproject

import android.Manifest
import android.content.Context
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.os.Looper
import android.support.v4.app.FragmentActivity
import android.widget.Toast
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.tbruyelle.rxpermissions2.RxPermissions

class MapsActivity: FragmentActivity(), OnMapReadyCallback, LocationListener {

    private var mMap: GoogleMap? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        requestLocationPermission()
    }

    private fun requestLocationPermission() {
        val rxPermissions = RxPermissions(this) // where this is an Activity instance
        // Must be done during an initialization phase like onCreate
        rxPermissions
                .request(Manifest.permission.ACCESS_FINE_LOCATION)
                .subscribe { granted ->
                    if (granted) { // Always true pre-M
                        Toast.makeText(this, "location permission granted", Toast.LENGTH_SHORT).show()
                        requestLocation()
                        // I can control the camera now
                    } else {
                        // Oups permission denied
                        Toast.makeText(this, "location permission denied", Toast.LENGTH_SHORT).show()
                    }
                }
    }

    private fun requestLocation() {
        val service = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val enabled = service
                .isProviderEnabled(LocationManager.GPS_PROVIDER)
        if (enabled) {
            // GPS enabled
            Toast.makeText(this, "GPS enabled", Toast.LENGTH_SHORT).show()
            getCurrentLocation()
        } else {
            Toast.makeText(this, "GPS disabled", Toast.LENGTH_SHORT).show()
        }
    }

    private fun getCurrentLocation() {
        val locationManager = getSystemService(LOCATION_SERVICE) as LocationManager
        locationManager.requestSingleUpdate(LocationManager.GPS_PROVIDER, this, Looper.getMainLooper())
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
    }


    private fun updateMapLocation(latLong: LatLng) {
        mMap?.addMarker(MarkerOptions().position(latLong)
                .title("My Current Location"))
        mMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(latLong, 17f))
    }

    override fun onLocationChanged(p0: Location?) {
        val latitude = p0?.latitude
        val longitude = p0?.longitude
        if (latitude != null && longitude != null) {
            Toast.makeText(this, "Latitude $latitude, Longitude $longitude", Toast.LENGTH_LONG).show()

            val latLong = LatLng(latitude, longitude)
            updateMapLocation(latLong)
        }
    }

    override fun onStatusChanged(p0: String?, p1: Int, p2: Bundle?) { /*empty*/ }

    override fun onProviderEnabled(p0: String?) { /*empty*/ }

    override fun onProviderDisabled(p0: String?) { /*empty*/ }
}
