package com.example.gofitness.ui.login

import android.view.View
import androidx.viewpager.widget.ViewPager

class SlideRightTransformer : ViewPager.PageTransformer {
    override fun transformPage(page: View, position: Float) {
        val width = page.width
        when {
            position < -1 -> // Page is off-screen to the left
                page.alpha = 0f
            position <= 0 -> { // Page is transitioning to the center or to the right
                page.alpha = 1f
                page.translationX = 0f
                page.scaleX = 1f
                page.scaleY = 1f
            }
            position <= 1 -> { // Page is transitioning to the left
                page.alpha = 1 - position
                page.translationX = width * -position
                val scaleFactor = MIN_SCALE + (1 - MIN_SCALE) * (1 - Math.abs(position))
                page.scaleX = scaleFactor
                page.scaleY = scaleFactor
            }
            else -> // Page is off-screen to the right
                page.alpha = 0f
        }
    }

    companion object {
        private const val MIN_SCALE = 0.85f
    }
}