package com.example.rouletteapp

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.util.AttributeSet
import android.view.View

class RouletteView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val sectorsCount = 8
    private val numbers = (1..sectorsCount).toList()
    private val sectorColors = listOf(
        Color.parseColor("#FF0055"), // Розовый
        Color.parseColor("#00FFAA"), // Мятный
        Color.parseColor("#00CCFF"), // Голубой
        Color.parseColor("#AA00FF"), // Фиолетовый
        Color.parseColor("#FFDD00"), // Желтый
        Color.parseColor("#FF5500"), // Оранжевый
        Color.parseColor("#00FF00"), // Зеленый
        Color.parseColor("#FF00CC")  // Маджента
    )
    private val dividerColor = Color.WHITE
    private val textColor = Color.WHITE


    // Кисть для фона сектора
    private val sectorPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
    }

    // Кисть для разделительных линий
    private val dividerPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = dividerColor
        style = Paint.Style.STROKE
        strokeWidth = 10f
        strokeCap = Paint.Cap.ROUND
    }

    // Кисть для текста
    private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = textColor
        textSize = 70f
        textAlign = Paint.Align.CENTER
        typeface = Typeface.create(Typeface.MONOSPACE, Typeface.BOLD)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val width = width.toFloat()
        val height = height.toFloat()

        // Защита от отрисовки до получения размеров
        if (width <= 0 || height <= 0) return

        val centerX = width / 2
        val centerY = height / 2
        val radius = minOf(width, height) / 2 - 10 // Небольшой отступ от краев View

        // Сохраняем состояние канваса, чтобы крутить всё колесо вокруг центра
        canvas.save()
        canvas.translate(centerX, centerY)

        // Пока угол поворота 0 (статика). Потом сюда подставим currentRotation

        val sectorAngle = 360f / sectorsCount

        for (i in 0 until sectorsCount) {
            val startAngle = i * sectorAngle

            sectorPaint.color = sectorColors[i % sectorColors.size]

            // Координаты прямоугольника, в который вписана окружность:
            // left = -radius, top = -radius, right = radius, bottom = radius
            // (так как мы уже сделали translate в центр)
            canvas.drawArc(
                -radius, -radius, radius, radius,
                startAngle,
                sectorAngle,
                true,
                sectorPaint
            )

            canvas.drawArc(
                -radius, -radius, radius, radius,
                startAngle,
                sectorAngle,
                true,
                dividerPaint
            )

            // 4. РИСУЕМ ЦИФРУ
            canvas.save()
            // Поворот на середину сектора
            canvas.rotate(startAngle + sectorAngle / 2)
            canvas.translate(radius * 0.85f, 0f)

            // 3. ПОВОРАЧИВАЕМ НА 270 ГРАДУСОВ (или -90)
            // Это развернет текст так, что его "низ" будет смотреть в центр (0,0)
            canvas.rotate(270f)

            // Рисуем текст.
            // X: отступаем от центра на 65% радиуса
            // Y: 0 (по центру оси поворота)
            canvas.drawText(numbers[i].toString(), 0f, 0f, textPaint)

            canvas.restore() // Сброс поворота текста
        }

        canvas.restore()
    }
}