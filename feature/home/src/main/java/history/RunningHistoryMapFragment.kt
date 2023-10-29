package history

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.home.R
import com.example.home.databinding.FragmentRunningHistoryMapBinding
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng

class RunningHistoryMapFragment : Fragment(), OnMapReadyCallback{
    private var myGoogleMap : GoogleMap? = null
    private lateinit var binding : FragmentRunningHistoryMapBinding
    private var latLngList = arrayListOf<LatLng>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentRunningHistoryMapBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map_fragment_running_history) as SupportMapFragment
        mapFragment.getMapAsync(this)
        binding.btnBack.setOnClickListener {
            requireActivity().onBackPressed()
        }
        val polyLines = arguments?.getStringArrayList("polyLines")

        if (polyLines != null) {
            for(latLng in polyLines){
                val latLngArray = latLng.split(",")
                if (latLngArray.size == 2) {
                    val latitude = latLngArray[0].toDoubleOrNull()
                    val longitude = latLngArray[1].toDoubleOrNull()
                    latLngList.add(LatLng(latitude!!, longitude!!))
                }
            }
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        setMapDefaultSetting(googleMap)
        animateCameraToCurrentLocation(googleMap, latLngList.first())
    }

    private fun setMapDefaultSetting(googleMap: GoogleMap){
        myGoogleMap = googleMap
        myGoogleMap!!.clear()
        myGoogleMap!!.uiSettings.isMapToolbarEnabled = false
        myGoogleMap!!.uiSettings.isZoomControlsEnabled = false
    }

    private fun animateCameraToCurrentLocation(map: GoogleMap, location: LatLng) {
        val cameraUpdate = CameraUpdateFactory.newLatLngZoom(location,
            15.0f
        )
        map.animateCamera(cameraUpdate, 500, null)
    }
}