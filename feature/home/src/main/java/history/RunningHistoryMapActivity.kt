package history

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.home.R
import com.example.home.databinding.ActivityRunningHistoryMapBinding

class RunningHistoryMapActivity : AppCompatActivity(){
    private lateinit var binding : ActivityRunningHistoryMapBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRunningHistoryMapBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val receivedData = intent.getParcelableExtra("polyLines", PolyLines::class.java)
        val kcal = intent.getDoubleExtra("kcal", 0.00)
        val distance = intent.getDoubleExtra("distance", 0.00)
        val duration = intent.getIntExtra("duration", 0)
        val date = intent.getLongExtra("date", 0L)
        val dataBundle = Bundle()
        dataBundle.putStringArrayList("polyLines", receivedData!!.stringList as ArrayList<String>)
        dataBundle.putDouble("kcal", kcal)
        dataBundle.putDouble("distance", distance)
        dataBundle.putInt("duration", duration)
        dataBundle.putLong("date", date)
        val fragment = RunningHistoryMapFragment()

        fragment.arguments = dataBundle
        supportFragmentManager.beginTransaction()
            .replace(R.id.running_history_map_fragment_container, fragment)
            .commit()
    }
}