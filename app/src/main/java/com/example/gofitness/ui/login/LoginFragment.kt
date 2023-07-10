package com.example.gofitness.ui.login

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import com.example.gofitness.R
import com.example.gofitness.databinding.FragmentLoginBinding
import java.util.*

class LoginFragment : Fragment() {
    private lateinit var binding : FragmentLoginBinding
    private val imageList = listOf(R.color.white, R.color.black, R.color.teal_200)
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
        viewPager = binding.ivText
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
            }
        })
    }

    private inner class ImagePagerAdapter : PagerAdapter() {
        override fun getCount(): Int {
            return imageList.size
        }

        override fun instantiateItem(container: ViewGroup, position: Int): Any {
            val imageView = ImageView(container.context)
            imageView.scaleType = ImageView.ScaleType.CENTER_CROP
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

    //Avoid memory leak
    override fun onDestroy() {
        super.onDestroy()
        timerTask.cancel()
        timer.cancel()
    }
}