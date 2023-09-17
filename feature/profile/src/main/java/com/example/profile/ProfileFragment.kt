package com.example.profile

import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.profile.databinding.FragmentProfileBinding
import com.github.anastr.speedviewlib.components.Style
import com.google.firebase.auth.FirebaseAuth


class ProfileFragment : Fragment() {
    private lateinit var binding : FragmentProfileBinding
    lateinit var profileNavigator : ProfileNavigator
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentProfileBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.btnLogout.setOnClickListener {
            val currentUser = FirebaseAuth.getInstance().currentUser
            if(currentUser != null){
                FirebaseAuth.getInstance().signOut()
                Thread.sleep(3000)
                profileNavigator.navigateScreen()
            }
        }

        val speedOMeter = binding.speedView
        speedOMeter.makeSections(6, Color.GREEN, Style.BUTT)
        speedOMeter.sections[0].color = requireActivity().getColor(R.color.colorNeutral04)
        speedOMeter.sections[1].color = requireActivity().getColor(R.color.colorNeutral03)
        speedOMeter.sections[2].color = Color.GREEN
        speedOMeter.sections[3].color = requireActivity().getColor(R.color.DarkGreen)
        speedOMeter.sections[4].color = requireActivity().getColor(R.color.Orange)
        speedOMeter.sections[5].color = Color.RED
        binding.btnUpdate.isEnabled = !(binding.weightInput.text?.isEmpty() == true || binding.heightInput.text?.isEmpty() == true)
        binding.btnUpdate.setOnClickListener {
            setTheBMI()
        }
        binding.heightInput.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(arg0: Editable) {
                binding.btnUpdate.isEnabled = (binding.weightInput.text?.isNotEmpty() == true && binding.heightInput.text?.isNotEmpty() == true)
            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
        })
        binding.weightInput.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(arg0: Editable) {
                binding.btnUpdate.isEnabled = (binding.weightInput.text?.isNotEmpty() == true && binding.heightInput.text?.isNotEmpty() == true)
            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
        })
    }

    private fun setTheBMI(){
        val height : Double = binding.heightInput.text.toString().toDouble()
        val weight : Double = binding.weightInput.text.toString().toDouble()
        if(height >= 0.0 && weight >= 0.0){
            binding.tvBmi.text = String.format("%.2f", calculateBMI(weight, height).toFloat())
            binding.speedView.speedTo(calculateBMI(weight, height).toFloat())
        }
        binding.tvBmiHealthStatus.text = interpretBMI(calculateBMI(weight, height))
        binding.tvBmiHealthStatusDescription.text = getString(interpretBMIDescription(binding.tvBmiHealthStatus.text.toString()))
    }

    private fun interpretBMI(bmi: Double): String {
        return when {
            bmi < 18.5 -> "Underweight"
            bmi < 24.9 -> "Normal Weight"
            bmi < 29.9 -> "Overweight"
            bmi < 34.9 -> "Obesity Class I"
            bmi < 39.9 -> "Obesity Class II"
            else -> "Obesity Class III"
        }
    }

    private fun interpretBMIDescription(bmiStatus : String): Int {
        return when (bmiStatus) {
            "Underweight" -> R.string.underweight
            "Normal Weight" -> R.string.normal_weight
            "Overweight" -> R.string.overweight
            "Obesity Class I" -> R.string.obeseI
            "Obesity Class II" -> R.string.obeseII
            else -> R.string.obeseIII
        }
    }


    private fun calculateBMI(weightKg: Double, heightCentimeters: Double): Double {
        val heightMeters = heightCentimeters / 100.0
        return weightKg / (heightMeters * heightMeters)
    }
    companion object {
        fun newInstance(): ProfileFragment {
            return ProfileFragment()
        }
    }
}