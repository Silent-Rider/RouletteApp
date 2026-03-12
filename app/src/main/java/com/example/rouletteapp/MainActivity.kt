package com.example.rouletteapp

import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
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

            override fun onRouletteFinished(result: String?) {
                launch.isEnabled = true
                launch.alpha = 1.0f
//                animateWinnerToCenter(resultValue)
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
}