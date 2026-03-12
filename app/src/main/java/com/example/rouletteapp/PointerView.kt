package com.example.rouletteapp

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.view.View
import androidx.core.graphics.toColorInt
import kotlin.math.tan

class PointerView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
): View(context, attrs, defStyleAttr) {

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = "#3D2B1F".toColorInt()
        style = Paint.Style.FILL_AND_STROKE
        strokeWidth = 12f
        strokeCap = Paint.Cap.ROUND
    }
    private val path = Path()

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val cx = width / 2f
        val cy = height / 2f
        val arrowHeight = height * 0.9f
        val arrowHalfWidth = width * 0.3f
        val stemHalfWidth = width * 0.1f

        val top = cy - arrowHeight / 2f
        val bottom = cy + arrowHeight / 2f
        val baseHeight = height * 0.06f
        val stemBottom = bottom - baseHeight
        val tipY = top
        val headBottom = cy - arrowHeight * 0.05f
        val angleOffset = (arrowHalfWidth - stemHalfWidth) * tan(Math.toRadians(-30.0)).toFloat()

        path.apply {
            reset()
            moveTo(cx, tipY)
            lineTo(cx + arrowHalfWidth, headBottom)
            lineTo(cx + stemHalfWidth, headBottom + angleOffset)
            lineTo(cx + stemHalfWidth, stemBottom)
            lineTo(cx + arrowHalfWidth, stemBottom)
            lineTo(cx + arrowHalfWidth, bottom)
            lineTo(cx - arrowHalfWidth, bottom)
            lineTo(cx - arrowHalfWidth, stemBottom)
            lineTo(cx - stemHalfWidth, stemBottom)
            lineTo(cx - stemHalfWidth, headBottom + angleOffset)
            lineTo(cx - arrowHalfWidth, headBottom)
            close()
        }

        canvas.drawPath(path, paint)
    }
}