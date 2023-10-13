package exercise

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.LocationManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import com.example.home.databinding.FragmentRunningBinding
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions

class RunningFragment : Fragment(), OnMapReadyCallback{
    private var myGoogleMap : GoogleMap ? = null
    private lateinit var binding : FragmentRunningBinding
    private var currentLocationLatLng : LatLng ? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentRunningBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment = childFragmentManager.findFragmentById(com.example.home.R.id.map_fragment) as SupportMapFragment
        mapFragment.getMapAsync(this)
        binding.btnBack.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }
        binding.btnCurrentLocation.setOnClickListener {
            myGoogleMap?.let { map -> currentLocationLatLng?.let { latLng ->
                animateCameraToCurrentLocation(map,
                    latLng
                )
            } }
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        checkGpsEnabled()
        setMapDefaultSetting(googleMap)
        if(checkGpsEnabled()){
            requestLocationUpdates()
        }
    }
    private fun setMapDefaultSetting(googleMap: GoogleMap){
        myGoogleMap = googleMap
        val sydney = LatLng(-34.0, 151.0)
        myGoogleMap!!.addMarker(MarkerOptions().position(sydney).title("Sydney"))
        myGoogleMap!!.moveCamera(CameraUpdateFactory.newLatLng(sydney))
        myGoogleMap!!.uiSettings.isMapToolbarEnabled = false
        myGoogleMap!!.uiSettings.isZoomControlsEnabled = false
    }
    private fun checkGpsEnabled() : Boolean{
        val locationManager =
            requireContext().getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val isGpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
        val isNetworkEnabled =
            locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
        return !(!isGpsEnabled && !isNetworkEnabled)
    }
    private fun requestLocationUpdates() {
        val polylineOptions = PolylineOptions()
            .color(Color.BLUE)
            .width(10f)

        val  locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 10000)
            .setWaitForAccurateLocation(false)
            .setMinUpdateIntervalMillis(5000)
            .setMaxUpdateDelayMillis(10000)
            .build()

        val locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                locationResult.locations.forEach { location ->
                    val latLng = LatLng(location.latitude, location.longitude)
                    binding.btnCurrentLocation.setOnClickListener {
                        myGoogleMap?.let { map -> animateCameraToCurrentLocation(map, latLng) }
                    }
                    currentLocationLatLng = latLng
                    polylineOptions.add(latLng)
                    myGoogleMap!!.addPolyline(polylineOptions)
                }
            }
        }
        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null)
    }
    private fun animateCameraToCurrentLocation(map: GoogleMap, location: LatLng) {
        val cameraUpdate = CameraUpdateFactory.newLatLngZoom(location, MIN_ZOOM_LEVEL)
        map.animateCamera(cameraUpdate, ZOOM_DURATION, null)
    }

    companion object {
        const val ZOOM_DURATION = 3000
        const val MIN_ZOOM_LEVEL = 15.0f
    }
}