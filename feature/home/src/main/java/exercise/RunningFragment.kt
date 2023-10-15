package exercise

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import com.example.home.R
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
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions

class RunningFragment : Fragment(), OnMapReadyCallback{
    private var myGoogleMap : GoogleMap ? = null
    private lateinit var binding : FragmentRunningBinding
    private var currentLocationLatLng : LatLng ? = null
    private var currentlyRunning : Boolean = false

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
        val mapFragment = childFragmentManager.findFragmentById(R.id.map_fragment) as SupportMapFragment
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
        setMapDefaultSetting(googleMap)
        if(checkGpsEnabled()){
            requestLocationUpdates()
        }
        binding.btnStartWorkout.setOnClickListener {
            currentlyRunning = true
            binding.btnStopWorkout.visibility = View.VISIBLE
            binding.btnStartWorkout.visibility = View.GONE
            currentLocationLatLng?.let { currentLatLng -> animateCameraToRunningLocation(googleMap, currentLatLng) }
        }
        binding.btnStopWorkout.setOnClickListener {
            currentlyRunning = false
            binding.btnStopWorkout.visibility = View.GONE
            binding.btnStartWorkout.visibility = View.VISIBLE
            currentLocationLatLng?.let { currentLatLng -> animateCameraToCurrentLocation(googleMap, currentLatLng) }
            myGoogleMap!!.clear()
        }
    }
    private fun setMapDefaultSetting(googleMap: GoogleMap){
        myGoogleMap = googleMap
        myGoogleMap!!.clear()
        currentLocationLatLng?.let { CameraUpdateFactory.newLatLng(it) }
            ?.let { myGoogleMap!!.moveCamera(it) }
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

        getLocationCallBack(polylineOptions)

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
        //Get Current Location LatLng and move the camera to it
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location: Location? ->
                if (location != null) {
                    val latitude = location.latitude
                    val longitude = location.longitude
                    val latLng = LatLng(latitude, longitude)
                    currentLocationLatLng = latLng
                    myGoogleMap?.let { animateCameraToCurrentLocation(it, latLng) }
                }
            }
        myGoogleMap!!.isMyLocationEnabled = true
        fusedLocationClient.requestLocationUpdates(locationRequest, getLocationCallBack(polylineOptions), null)
    }
    private fun getLocationCallBack(polylineOptions: PolylineOptions) : LocationCallback{
        val locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                locationResult.locations.forEach { location ->
                    val latLng = LatLng(location.latitude, location.longitude)
                    binding.btnCurrentLocation.setOnClickListener {
                        myGoogleMap?.let { map -> animateCameraToCurrentLocation(map, latLng) }
                    }
                    if(currentlyRunning){
                        currentLocationLatLng = latLng
                        polylineOptions.add(latLng)
                        myGoogleMap!!.addPolyline(polylineOptions)
                    }
                }
            }
        }
        return locationCallback
    }
    private fun animateCameraToCurrentLocation(map: GoogleMap, location: LatLng) {
        val cameraUpdate = CameraUpdateFactory.newLatLngZoom(location, DEFAULT_ZOOM_LEVEL)
        map.animateCamera(cameraUpdate, ZOOM_DURATION, null)
    }

    private fun animateCameraToRunningLocation(map: GoogleMap, location: LatLng) {
        val cameraUpdate = CameraUpdateFactory.newLatLngZoom(location, RUNNING_ZOOM_LEVEL)
        map.animateCamera(cameraUpdate, ZOOM_DURATION, null)
    }

    companion object {
        const val ZOOM_DURATION = 1000
        const val DEFAULT_ZOOM_LEVEL = 15.0f
        const val RUNNING_ZOOM_LEVEL = 18.0f
    }
}