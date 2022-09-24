package com.codetron.stepcustomview

import android.content.Context
import android.content.res.Resources
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.util.AttributeSet
import android.util.TypedValue
import android.view.View
import androidx.core.content.ContextCompat

class StepView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : View(context, attrs) {

    private val defaultWidth = 45F.dpToPx()
    private val defaultHeight = 45F.dpToPx()
    private val strokeSize = 2F.dpToPx()
    private val textSize = 16F.dpToPx()

    private val bgActiveColor = ContextCompat.getColor(context, R.color.purple_500)
    private val strokeInactiveColor = ContextCompat.getColor(context, R.color.black)
    private val bgInactiveColor = ContextCompat.getColor(context, R.color.white)
    private val textActiveColor = ContextCompat.getColor(context, R.color.white)
    private val textInactiveColor = ContextCompat.getColor(context, R.color.black)
    private val lineColor = ContextCompat.getColor(context, R.color.black)

    // jumlah indikator yang tampil
    private var displayedIndicator = 5
    private var activeIndicator = 0

    var maxIndicator = 8
        private set
    var currentMaxIndicator = 5
        private set
    var currentMinIndicator = 0
        private set

    init {

    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        drawLine(canvas)
        drawCircle(canvas)
        drawText(canvas)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val height =
            getMeasurementSize(heightMeasureSpec, defaultHeight.toInt())
        val width = measurementWidthSize(widthMeasureSpec, defaultWidth.toInt())
        setMeasuredDimension(width, height)
    }

    private fun drawLine(canvas: Canvas?) {
        val startStopY = defaultHeight / 2F

        val startX = if (activeIndicator >= displayedIndicator) {
            0F
        } else {
            ((measuredWidth / displayedIndicator) / 2F)
        }

        val stopX = if (activeIndicator == maxIndicator - 1) {
            measuredWidth - ((measuredWidth / displayedIndicator) / 2F)
        } else {
            measuredWidth
        }.toFloat()

        canvas?.drawLine(startX, startStopY, stopX, startStopY, linePaint)
    }

    private fun drawCircle(canvas: Canvas?) {
        for ((index, i) in (currentMinIndicator until currentMaxIndicator).withIndex()) {
            val cx = measuredWidth / displayedIndicator * index +
                    ((measuredWidth / displayedIndicator) / 2F)
            val cy = defaultHeight / 2F

            canvas?.drawCircle(cx, cy, cy, indicatorPaint.apply {
                color = if (i > activeIndicator) bgInactiveColor else bgActiveColor
                style = Paint.Style.FILL
            })

            if (i > activeIndicator) {
                canvas?.drawCircle(cx, cy, cy - (strokeSize / 2F), indicatorPaint.apply {
                    color = strokeInactiveColor
                    style = Paint.Style.STROKE
                    strokeWidth = strokeSize
                })
            }
        }
    }

    private fun drawText(canvas: Canvas?) {
        for ((index, i) in (currentMinIndicator until currentMaxIndicator).withIndex()) {
            val text = (i + 1).toString()
            textPaint.getTextBounds(text, 0, text.length, textBounds)

            val x = measuredWidth / displayedIndicator * index +
                    ((measuredWidth / displayedIndicator) / 2F) - textBounds.centerX().toFloat()

            val y = defaultHeight / 2F - textBounds.centerY().toFloat()

            canvas?.drawText(text, x, y, textPaint.apply {
                color = if (i <= activeIndicator) {
                    textActiveColor
                } else {
                    textInactiveColor
                }
            })
        }
    }

    fun setCurrentIndicator(indicator: Int) {
        if (indicator >= maxIndicator) return
        if (indicator < currentMinIndicator) return

        activeIndicator = indicator

        currentMinIndicator =
            if (indicator < displayedIndicator) 0
            else (indicator - displayedIndicator) + 1

        currentMaxIndicator =
            if (indicator < displayedIndicator) displayedIndicator
            else indicator + 1

        invalidate()
    }

    private fun getMeasurementSize(measureSpec: Int, defaultSize: Int): Int {
        val mode = MeasureSpec.getMode(measureSpec)
        val size = MeasureSpec.getSize(measureSpec)

        return when (mode) {
            MeasureSpec.EXACTLY -> size
            MeasureSpec.AT_MOST -> defaultSize.coerceAtMost(size)
            MeasureSpec.UNSPECIFIED -> defaultSize
            else -> defaultSize
        }
    }

    private fun measurementWidthSize(measureSpec: Int, defaultSize: Int): Int {
        val mode = MeasureSpec.getMode(measureSpec)
        val size = MeasureSpec.getSize(measureSpec)

        return if (mode == MeasureSpec.EXACTLY) {
            getMeasurementSize(measureSpec, defaultSize)
        } else {
            measureWidthWarpContent(size)
        }
    }

    private fun measureWidthWarpContent(size: Int): Int {
        // dikurang 1 karena fungsi repeat di onDraw dilakukan sampai iterasi ke (displayredIndicator-1)
        return (size / displayedIndicator) *
                (displayedIndicator - 1) +
                (size / displayedIndicator / 2F).toInt()
    }

    private fun Float.dpToPx(): Float {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            this,
            Resources.getSystem().displayMetrics
        )
    }

    private val indicatorPaint by lazy {
        Paint().apply {
            isAntiAlias = true
        }
    }

    private val textPaint by lazy {
        Paint().apply {
            isAntiAlias = true
            style = Paint.Style.FILL
            textSize = this@StepView.textSize
            flags = Paint.FAKE_BOLD_TEXT_FLAG
        }
    }

    private val textBounds by lazy {
        Rect()
    }

    private val linePaint by lazy {
        Paint().apply {
            isAntiAlias = true
            style = Paint.Style.STROKE
            color = lineColor
            strokeWidth = strokeSize
        }
    }

}