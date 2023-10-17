package exercise

import android.Manifest
import android.app.AlertDialog
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.os.Handler
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
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Polyline
import com.google.android.gms.maps.model.PolylineOptions

class RunningFragment : Fragment(), OnMapReadyCallback{
    private var myGoogleMap : GoogleMap ? = null
    private lateinit var binding : FragmentRunningBinding
    private var currentLocationLatLng : LatLng ? = null
    private var currentlyRunning : Boolean = false
    private var polylineOptions = PolylineOptions()
        .color(Color.BLUE)
        .width(10f)
    private val polylines = mutableListOf<Polyline>()
    private var lastLocation: Location? = null
    private var totalDistance: Float = 0f
    private var currentKcal: Double = 0.0
    private val handler = Handler()
    private var elapsedTimeInSeconds = 0L
    private val updateDuration = object : Runnable {
        override fun run() {
            elapsedTimeInSeconds++
            updateDurationText()
            if (currentlyRunning) {
                handler.postDelayed(this, 1000)
            }
            else{
                binding.tvDuration.text = "0:00"
            }
        }
    }

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
        binding.btnStartWorkout.setOnClickListener {
            currentlyRunning = true
            binding.btnStopWorkout.visibility = View.VISIBLE
            binding.btnStartWorkout.visibility = View.GONE
            currentLocationLatLng?.let { currentLatLng -> myGoogleMap?.let { map ->
                animateCameraToRunningLocation(
                    map, currentLatLng)
            } }
            handler.post(updateDuration)
        }
        binding.btnStopWorkout.setOnClickListener {
            val alertDialog = AlertDialog.Builder(requireContext())
            alertDialog.setTitle("Confirm Stop Workout")
            alertDialog.setMessage("Are you sure you want to stop the workout?")
            alertDialog.setPositiveButton("Yes") { _, _ ->
                currentlyRunning = false
                binding.btnStopWorkout.visibility = View.GONE
                binding.btnStartWorkout.visibility = View.VISIBLE
                currentLocationLatLng?.let { currentLatLng -> myGoogleMap?.let { map ->
                    animateCameraToCurrentLocation(
                        map, currentLatLng)
                } }
                myGoogleMap!!.clear()
                polylineOptions = PolylineOptions()
                    .color(Color.BLUE)
                    .width(10f)
                binding.tvKcal.text = "0"
                binding.tvDistance.text = "0"
                binding.tvDuration.text = "0"
            }
            alertDialog.setNegativeButton("No") { _, _ -> }
            alertDialog.show()
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        setMapDefaultSetting(googleMap)
        myGoogleMap?.let { currentLocationLatLng?.let { latLng ->
            animateCameraToCurrentLocation(it,
                latLng)
        } }
        if(checkGpsEnabled()){
            requestLocationUpdates()
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
        val  locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 10000)
            .setWaitForAccurateLocation(false)
            .setMinUpdateIntervalMillis(5000)
            .setMaxUpdateDelayMillis(10000)
            .build()

        getLocationCallBack()

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
        fusedLocationClient.requestLocationUpdates(locationRequest, getLocationCallBack(), null)
    }
    private fun getLocationCallBack() : LocationCallback{
        val locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                locationResult.locations.forEach { location ->
                    val latLng = LatLng(location.latitude, location.longitude)
                    binding.btnCurrentLocation.setOnClickListener {
                        myGoogleMap?.let { map -> animateCameraToCurrentLocation(map, latLng) }
                    }
                    if(currentlyRunning){
                        //When running each time location update animate camera to running location
                        myGoogleMap?.let { map -> animateCameraToRunningLocation(map, latLng) }
                        currentLocationLatLng = latLng
                        polylineOptions.add(latLng)
                        myGoogleMap!!.addPolyline(polylineOptions)
                        //Running Distance
                        val distance = lastLocation?.distanceTo(location)
                        if (distance != null) {
                            totalDistance += distance
                        }
                        val totalDistanceKilometers = totalDistance / 1000.0
                        binding.tvDistance.text = String.format("%.2f", totalDistanceKilometers)
                        //Running Kcal
                        if (distance != null) {
                            currentKcal = (totalDistanceKilometers * DISTANCE_MULTIPLIER)
                            binding.tvKcal.text = String.format("%.2f", currentKcal)
                        } }
                    lastLocation = location
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

    private fun updateDurationText() {
        val minutes = elapsedTimeInSeconds / 60
        val seconds = elapsedTimeInSeconds % 60
        val formattedTime = String.format("%02d:%02d", minutes, seconds)
        binding.tvDuration.text = formattedTime
    }

    companion object {
        const val DISTANCE_MULTIPLIER = 60
        const val ZOOM_DURATION = 1000
        const val DEFAULT_ZOOM_LEVEL = 15.0f
        const val RUNNING_ZOOM_LEVEL = 18.0f
    }
}