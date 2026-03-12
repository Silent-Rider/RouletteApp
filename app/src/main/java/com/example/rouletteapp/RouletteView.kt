package com.example.rouletteapp

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.util.AttributeSet
import android.view.View
import androidx.core.graphics.withRotation
import androidx.core.graphics.withTranslation
import androidx.core.graphics.toColorInt

class RouletteView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val sectorsCount = 8
    private val numbers = (1..sectorsCount).toList()
    val icons = listOf("💰", "🍌", "🚀", "🎲", "🍒", "💎", "\uD83C\uDF54", "\uD83C\uDFAE")
    private val sectorColors = listOf(
        "#FF0055".toColorInt(), // Розовый
        "#00FFAA".toColorInt(), // Мятный
        "#00CCFF".toColorInt(), // Голубой
        "#AA00FF".toColorInt(), // Фиолетовый
        "#FFDD00".toColorInt(), // Желтый
        "#FF5500".toColorInt(), // Оранжевый
        "#00FF00".toColorInt(), // Зеленый
        "#FF00CC".toColorInt()  // Маджента
    )
    private val dividerColor = "#DEB887".toColorInt()
    private val textColor = Color.WHITE
    private val sectorPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
    }
    private val dividerPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = dividerColor
        style = Paint.Style.STROKE
        strokeWidth = 12f
        strokeCap = Paint.Cap.ROUND
    }
    private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = textColor
        textSize = 120f
        textAlign = Paint.Align.CENTER
        typeface = Typeface.create(Typeface.MONOSPACE, Typeface.BOLD)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val width = width.toFloat()
        val height = height.toFloat()

        if (width <= 0 || height <= 0) return

        val centerX = width / 2
        val centerY = height / 2
        val radius = minOf(width, height) / 2 - 10

        canvas.withTranslation(centerX, centerY) {
            canvas.rotate(360f / sectorsCount * 1.5f)

            val sectorAngle = 360f / sectorsCount

            for (i in 0 until sectorsCount) {
                val startAngle = i * sectorAngle

                sectorPaint.color = sectorColors[i % sectorColors.size]

                drawArc(
                    -radius, -radius, radius, radius,
                    startAngle,
                    sectorAngle,
                    true,
                    sectorPaint
                )

                drawArc(
                    -radius, -radius, radius, radius,
                    startAngle,
                    sectorAngle,
                    true,
                    dividerPaint
                )

                withRotation(startAngle + sectorAngle / 2) {
                    translate(radius * 0.85f, 0f)
                    rotate(270f)
                    drawText(icons[i].toString(), 0f, 0f, textPaint)
                }
            }
        }
    }
}