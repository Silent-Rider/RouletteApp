package com.example.rouletteapp

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.util.AttributeSet
import android.util.Log
import android.view.Choreographer
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
    private val icons = listOf("💰", "🍌", "🚀", "🎲", "🍒", "💎", "\uD83C\uDF54", "\uD83C\uDFAE")
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
    private val sectorPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
    }
    private val dividerPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = "#DEB887".toColorInt()
        style = Paint.Style.STROKE
        strokeWidth = 12f
        strokeCap = Paint.Cap.ROUND
    }
    private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.WHITE
        textSize = 120f
        textAlign = Paint.Align.CENTER
        typeface = Typeface.create(Typeface.MONOSPACE, Typeface.BOLD)
    }
    private var currentAngle = 0f
    private var angularVelocity = 0f
    private var isSpinning = false
    private val choreographer = Choreographer.getInstance()
    private var lastFrameTime = 0L
    private val frameCallback = object : Choreographer.FrameCallback {
        override fun doFrame(frameTimeNanos: Long) {
            if (!isSpinning) return

            if (lastFrameTime != 0L) {
                val deltaSeconds = (frameTimeNanos - lastFrameTime) / 1_000_000_000f
                currentAngle += angularVelocity * deltaSeconds
                angularVelocity *= 0.98f // трение

                if (angularVelocity < 0.5f) {
                    angularVelocity = 0f
                    isSpinning = false

                    val angleUnderPointer = ((90f - currentAngle) % 360f + 360f) % 360f
                    val sectorAngle = 360f / sectorsCount
                    val sectorIndex = (angleUnderPointer / sectorAngle).toInt() % sectorsCount
                    Log.d("Roulette", "currentAngle=$currentAngle, angleUnderPointer=$angleUnderPointer, sectorIndex=$sectorIndex, icon=${icons[sectorIndex]}")


                    val result = calculateResultSector()
                    listener?.onRouletteFinished(result)

                }
            }

            lastFrameTime = frameTimeNanos
            invalidate()
            choreographer.postFrameCallback(this)
        }
    }
    var listener:RouletteListener? = null

    fun spin(targetVelocity: Float) {
        if (isSpinning) return
        angularVelocity = targetVelocity.coerceIn(100f, 2000f)
        isSpinning = true
        lastFrameTime = 0L
        choreographer.postFrameCallback(frameCallback)
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
            canvas.rotate(currentAngle)

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
                    drawText(icons[i], 0f, 0f, textPaint)
                }
            }
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val size = minOf(measuredWidth, measuredHeight)
        setMeasuredDimension(size, size)
    }

    private fun calculateResultSector(): String {
        val sectorAngle = 360f / sectorsCount

        val normalizedAngle = ((120f - currentAngle) % 360f + 360f) % 360f

        val drawOffset = 360f / sectorsCount * 1.5f
        val adjustedAngle = ((normalizedAngle - drawOffset) % 360f + 360f) % 360f

        val sectorIndex = (adjustedAngle / sectorAngle).toInt() % sectorsCount

        val angleInSector = adjustedAngle % sectorAngle
        val borderThreshold = 3f

        if (angleInSector < borderThreshold || angleInSector > sectorAngle - borderThreshold) {
            return "?"
        }

        return icons[sectorIndex]
    }
}