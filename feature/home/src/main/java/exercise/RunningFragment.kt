package exercise

import KcalByDayViewModel
import RunningHistoryViewModel
import android.Manifest
import android.annotation.SuppressLint
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
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
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
import com.google.android.gms.maps.model.PolylineOptions
import entity.KcalByDay

class RunningFragment : Fragment(), OnMapReadyCallback{
    private var myGoogleMap : GoogleMap ? = null
    private lateinit var binding : FragmentRunningBinding
    private var currentLocationLatLng : LatLng ? = null
    private var currentlyRunning : Boolean = false
    private var polylineOptions = PolylineOptions()
        .color(Color.BLUE)
        .width(10f)
    private var lastLocation: Location? = null
    private var totalDistance: Float = 0f
    private var currentKcal: Double = 0.0
    private val handler = Handler()
    private var elapsedTimeInSeconds = 0L
    private lateinit var runningViewModel: RunningHistoryViewModel
    private lateinit var kcalByDayViewModel: KcalByDayViewModel
    private var latLngStringList : MutableList<String> = mutableListOf()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentRunningBinding.inflate(layoutInflater, container, false)
        checkGpsPermission()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        kcalByDayViewModel = ViewModelProvider(this)[KcalByDayViewModel::class.java]
        runningViewModel = ViewModelProvider(this)[RunningHistoryViewModel::class.java]
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
            startDurationCounter()
            latLngStringList = mutableListOf()
        }
        binding.btnStopWorkout.setOnClickListener {
            val alertDialog = AlertDialog.Builder(requireContext())
            alertDialog.setTitle("Running Session Finish")
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
                if(currentKcal > 0){
                    kcalByDayViewModel.addKcalData(kcal = currentKcal)
                    runningViewModel.addRunningHistory(kcal= currentKcal, duration = elapsedTimeInSeconds.toInt(),
                        dateInMillis = System.currentTimeMillis(), distance = totalDistance.toDouble(), polylines = latLngStringList)
                }
            }
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
        val  locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, MAX_UPDATE_DELAY)
            .setWaitForAccurateLocation(false)
            .setMinUpdateIntervalMillis(MIN_UPDATE_DELAY)
            .setMaxUpdateDelayMillis(MAX_UPDATE_DELAY)
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
                    //Add Latitude and Longitude as string
                    latLngStringList.add("${location.latitude},${location.longitude}")
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

    private fun startDurationCounter(){
        val updateDuration = object : Runnable {
            @SuppressLint("SetTextI18n")
            override fun run() {
                if (currentlyRunning) {
                    elapsedTimeInSeconds++
                    updateDurationText()
                    handler.postDelayed(this, 1000)
                }
                else{
                    elapsedTimeInSeconds = 0
                    binding.tvDuration.text = "00:00"
                }
            }
        }
        handler.post(updateDuration)
    }

     private fun checkGpsPermission() {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissions(
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                MY_PERMISSIONS_REQUEST_GPS
            )
        }
    }

    companion object {
        const val MY_PERMISSIONS_REQUEST_GPS = 2
        const val MAX_UPDATE_DELAY = 10000L
        const val MIN_UPDATE_DELAY = 5000L
        const val DISTANCE_MULTIPLIER = 60
        const val ZOOM_DURATION = 1000
        const val DEFAULT_ZOOM_LEVEL = 15.0f
        const val RUNNING_ZOOM_LEVEL = 18.0f
    }
}