package com.example.gofitness.ui.login

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import com.example.gofitness.R
import com.example.gofitness.databinding.FragmentLoginBinding
import java.util.*


class LoginFragment : Fragment() {
    private lateinit var binding : FragmentLoginBinding
    private val imageList = listOf(R.drawable.starting_image1, R.drawable.starting_image2, R.drawable.starting_image3, R.drawable.starting_image4)
    private val messageList = listOf("Let’s workout today", "Manage Your Own Workout", "Nutrition is important for you", "Have a broader view")
    private val messageDetailList = listOf("Start workout today with GoFitness", "Planning, managing and tracking  your own workout activities",
        "GoFitness help recommend you the best diet based on your workout goal", "Statistic will help you get all information you need about your workout ")
    private var currentImageIndex : Int = 0
    private val timer = Timer()
    private lateinit var timerTask: TimerTask
    private val handler = Handler(Looper.getMainLooper())
    private lateinit var viewPager: ViewPager


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
        super.onViewCreated(view, savedInstanceState)
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
}