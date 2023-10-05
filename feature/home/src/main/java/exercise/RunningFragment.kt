package exercise

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.home.databinding.FragmentRunningBinding
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class RunningFragment : Fragment(), OnMapReadyCallback{
    private var myGoogleMap : GoogleMap ? = null
    private lateinit var binding : FragmentRunningBinding
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
    }

    override fun onMapReady(googleMap: GoogleMap) {
        myGoogleMap = googleMap
        val sydney = LatLng(-34.0, 151.0)
        myGoogleMap!!.addMarker(MarkerOptions().position(sydney).title("Sydney"))
        myGoogleMap!!.moveCamera(CameraUpdateFactory.newLatLng(sydney))
    }
}