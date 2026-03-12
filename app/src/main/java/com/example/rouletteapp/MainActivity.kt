package com.example.rouletteapp

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.animation.DecelerateInterpolator
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.animation.doOnEnd
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.rouletteapp.databinding.ActivityMainBinding
import kotlin.random.Random

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        enableEdgeToEdge()

        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val drawer = binding.drawerLayout
        val roulette = binding.roulette
        val toolbar = binding.toolbar
        val launch = binding.launch

        val actionBarDrawerToggle = object : ActionBarDrawerToggle(
            this,
            drawer,
            toolbar,
            R.string.open_drawer,
            R.string.close_drawer
        ) {
            override fun onDrawerOpened(drawerView: View) {
                super.onDrawerOpened(drawerView)
            }
        }

        drawer.addDrawerListener(actionBarDrawerToggle)
        actionBarDrawerToggle.syncState()

        roulette.listener = object : RouletteListener {
            override fun onRouletteStarted() {
                launch.isEnabled = false
                launch.alpha = 0.5f
            }

            override fun onRouletteFinished(result: String) {
                launch.isEnabled = true
                launch.alpha = 1.0f
                showResult(result)
            }
        }

        launch.setOnClickListener {
            val rand = (Random.nextFloat() * 1000f) + 300f
            roulette.spin(rand)
            roulette.listener?.onRouletteStarted()
        }

        binding.navView.menu.getItem(0).setOnMenuItemClickListener {
            AlertDialog.Builder(this)
                .setTitle(getString(R.string.about_button))
                .setMessage(getString(R.string.about_text))
                .setPositiveButton(getString(R.string.ok)) { dialog, _ -> dialog.dismiss() }
                .create()
                .show()
            return@setOnMenuItemClickListener true
        }
    }

    private fun showResult(result: String){
        if (result == "?") return
        val roulette = binding.roulette

        val rouletteLocation = IntArray(2)
        roulette.getLocationOnScreen(rouletteLocation)
        val rouletteCenterX = rouletteLocation[0] + roulette.width / 2f
        val rouletteCenterY = rouletteLocation[1] + roulette.height / 2f

        val winnerText = TextView(this).apply {
            text = result
            textSize = 40f
            gravity = Gravity.CENTER
        }

        val rootView = window.decorView as ViewGroup
        rootView.addView(winnerText, ViewGroup.LayoutParams(200, 200))

        val startX = rouletteCenterX - 100f
        val startY = rouletteLocation[1] + roulette.height - 200f

        winnerText.x = startX
        winnerText.y = startY
        winnerText.scaleX = 0.5f
        winnerText.scaleY = 0.5f
        winnerText.alpha = 0f

        val targetX = rouletteCenterX - 100f
        val targetY = rouletteCenterY - 100f

        AnimatorSet().apply {
            playTogether(
                ObjectAnimator.ofFloat(winnerText, "x", startX, targetX),
                ObjectAnimator.ofFloat(winnerText, "y", startY, targetY),
                ObjectAnimator.ofFloat(winnerText, "scaleX", 0.5f, 4f),
                ObjectAnimator.ofFloat(winnerText, "scaleY", 0.5f, 4f),
                ObjectAnimator.ofFloat(winnerText, "alpha", 0f, 1f)
            )
            duration = 800
            interpolator = DecelerateInterpolator()
            doOnEnd {
                winnerText.animate()
                    .alpha(0f)
                    .setStartDelay(1000)
                    .setDuration(400)
                    .withEndAction { rootView.removeView(winnerText) }
                    .start()
            }
            start()
        }
    }

}