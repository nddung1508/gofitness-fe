package com.example.gofitness.ui.login

import PersonalInformationViewModel
import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import com.example.gofitness.R
import com.example.gofitness.databinding.FragmentLoginBinding
import com.example.gofitness.ui.AuthenticationNavigator
import com.example.gofitness.ui.MainActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.gms.common.api.ApiException
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import java.util.*


class LoginFragment : Fragment() {
    private lateinit var binding : FragmentLoginBinding
    private val imageList = listOf(R.drawable.starting_image1, R.drawable.starting_image2, R.drawable.starting_image3, R.drawable.starting_image4)
    private var currentImageIndex : Int = 0
    private val timer = Timer()
    private lateinit var timerTask: TimerTask
    private val handler = Handler(Looper.getMainLooper())
    private lateinit var viewPager: ViewPager
    lateinit var authenticationNavigator : AuthenticationNavigator
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var mAuth : FirebaseAuth
    private lateinit var personalInformationViewModel : PersonalInformationViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLoginBinding.inflate(inflater, container, false)
        viewPager = binding.vpImg
        viewPager.adapter = ImagePagerAdapter()
        viewPager.setPageTransformer(false, SlideRightTransformer())
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        mAuth = FirebaseAuth.getInstance()
        val googleSignInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(requireContext(), googleSignInOptions)

        binding.bottomSheetLogin.clGoogle.setOnClickListener {
            val signInIntent = googleSignInClient.signInIntent
            startActivityForResult(signInIntent, RC_SIGN_IN)
        }
        super.onViewCreated(view, savedInstanceState)
        //List of Message
        val messageList = listOf(getString(R.string.lets_workout_today), getString(R.string.manage_your_own_workout), getString(R.string.important_nutrition), getString(R.string.broader_view))
        val messageDetailList = listOf(getString(R.string.start_workout_today), getString(R.string.planning_and_tracking),
            getString(R.string.help_recommend_diet), getString(R.string.help_recommend_diet))
        val bottomSheetBehavior = BottomSheetBehavior.from(binding.flBottomSheet)
        //Bottom Sheet
        bottomSheetBehavior.peekHeight = 200
        bottomSheetBehavior.isHideable = true
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
        binding.root.setOnClickListener {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
        }
        binding.btnLogin.setOnClickListener {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
            binding.bottomSheetRegister.root.visibility = View.GONE
            binding.bottomSheetLogin.root.visibility = View.VISIBLE
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED)
        }
        binding.btnRegister.setOnClickListener {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
            binding.bottomSheetLogin.root.visibility = View.GONE
            binding.bottomSheetRegister.root.visibility = View.VISIBLE
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED)
        }

        binding.bottomSheetRegister.clUser.setOnClickListener {
            authenticationNavigator.navigateScreen(NAVIGATE_TO_REGISTER)
        }
        binding.bottomSheetLogin.clUser.setOnClickListener {
            authenticationNavigator.navigateScreen(NAVIGATE_TO_LOGIN_FORM)
        }
        //Timer
        timerTask = object : TimerTask() {
            override fun run() {
                handler.post {
                    val nextItem = (viewPager.currentItem + 1) % imageList.size
                    viewPager.setCurrentItem(nextItem, true)
                    binding.tvMessage.text = messageList[currentImageIndex]
                    binding.tvMessageDetail.text = messageDetailList[currentImageIndex]
                    binding.tvMessageDetail.gravity = Gravity.CENTER
                }
            }
        }
        timer.schedule(timerTask, 5000, 5000)
        viewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
                currentImageIndex = position
            }

            override fun onPageSelected(position: Int) {
            }

            override fun onPageScrollStateChanged(state: Int) {
                when(state){
                    ViewPager.SCROLL_STATE_IDLE -> {
                        changeTextWithAnimation(binding.tvMessage, messageList[currentImageIndex])
                        changeTextWithAnimation(binding.tvMessageDetail, messageDetailList[currentImageIndex])
                        binding.tvMessageDetail.gravity = Gravity.CENTER
                    }
                }
            }
        })

        val springDotsIndicator = binding.springDotsIndicator
        springDotsIndicator.attachTo(viewPager)
    }

    private inner class ImagePagerAdapter : PagerAdapter() {
        override fun getCount(): Int {
            return imageList.size
        }

        override fun instantiateItem(container: ViewGroup, position: Int): Any {
            val imageView = ImageView(container.context)
            imageView.scaleType = ImageView.ScaleType.FIT_XY
            imageView.setImageResource(imageList[position])
            container.addView(imageView)
            return imageView
        }

        override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
            container.removeView(`object` as View)
        }

        override fun isViewFromObject(view: View, `object`: Any): Boolean {
            return view == `object`
        }
    }

    private fun changeTextWithAnimation(textView: TextView, newText: String) {
        val fadeOut = ObjectAnimator.ofFloat(textView, "alpha", 1.0f, 0.0f)
        val fadeIn = ObjectAnimator.ofFloat(textView, "alpha", 0.0f, 1.0f)

        fadeOut.duration = 250
        fadeIn.duration = 250

        fadeOut.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                textView.text = newText
                fadeIn.start()
            }
        })

        val animatorSet = AnimatorSet()
        animatorSet.playSequentially(fadeOut, fadeIn)
        animatorSet.start()
    }

    //Avoid memory leak
    override fun onDestroy() {
        super.onDestroy()
        timerTask.cancel()
        timer.cancel()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)
                firebaseAuthWithGoogle(account?.idToken)
            } catch (e: ApiException) {
                Log.e("GoogleSignIn", "Sign-in failed with code: ${e.statusCode}, message: ${e.message}")
                Toast.makeText(requireContext(), "Sign-in with Google failed.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String?) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        mAuth.signInWithCredential(credential)
            .addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful) {
                    val user = mAuth.currentUser
                    handleSignInSuccess(user)
                } else {
                    Toast.makeText(requireContext(), "Sign-in with Google failed.", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun handleSignInSuccess(user: FirebaseUser?) {
        personalInformationViewModel = ViewModelProvider(this).get(PersonalInformationViewModel::class.java)
        personalInformationViewModel.getPersonalInformation().observe(viewLifecycleOwner){
            if(it == null){
                if (user != null) {
                    Toast.makeText(requireContext(), "Signed in as ${user.displayName}", Toast.LENGTH_SHORT).show()
                }
                authenticationNavigator.navigateScreen(NAVIGATE_TO_USER_INFORMATION)
            }
            else{
                if (user != null) {
                    val intent = Intent(requireContext(), MainActivity::class.java)
                    startActivity(intent)
                }
            }
        }
    }

    companion object{
        const val NAVIGATE_TO_REGISTER = "LOGIN_TO_REGISTER_FORM"
        const val NAVIGATE_TO_LOGIN_FORM = "LOGIN_TO_LOGIN_FORM"
        const val NAVIGATE_TO_USER_INFORMATION = "LOGIN_TO_USER_INFORMATION"
        const val RC_SIGN_IN = 9001
    }
}