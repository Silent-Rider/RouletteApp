package com.example.rouletteapp

interface RouletteListener {
    fun onRouletteStarted()
    fun onRouletteFinished(result: String?)
}