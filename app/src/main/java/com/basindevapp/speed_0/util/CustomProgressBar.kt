package com.basindevapp.speed_0.util

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View

class CustomProgressBar @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    private var maxValue = 100
    private val defaultSegmentColor = Color.GRAY
    private val firstSegmentColor = Color.GREEN
    private val secondSegmentColor = Color.RED
    var firstSegmentEndAngle = 0.0f
    private val progressPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val backgroundPaint = Paint(Paint.ANTI_ALIAS_FLAG)

    private var defaultSegmentEnd = 0
    private var firstSegmentEnd = 0
    private var secondSegmentEnd = 0
    private var progress = 0

    init {
        progressPaint.style = Paint.Style.STROKE
        progressPaint.strokeWidth = 40f

        backgroundPaint.style = Paint.Style.STROKE
        backgroundPaint.strokeWidth = 40f
        backgroundPaint.color = Color.LTGRAY
    }

    fun setProgress(progress: Int) {
        this.progress = progress.coerceIn(0, maxValue) // Ensure progress is within the range
        invalidate()
    }

    fun setMaxValue(maxValue: Int) {
        this.maxValue = maxValue
        invalidate()
    }

    fun setSegmentEnds(defaultSegmentEnd: Int, firstSegmentEnd: Int, secondSegmentEnd: Int) {
        this.defaultSegmentEnd = defaultSegmentEnd
        this.firstSegmentEnd = firstSegmentEnd
        this.secondSegmentEnd = secondSegmentEnd
        invalidate()
    }

    fun setFirstSegmentAndDefaultSegment(defaultSegmentEnd: Int,firstSegmentEnd: Int) {
        this.defaultSegmentEnd = defaultSegmentEnd
        this.firstSegmentEnd = firstSegmentEnd
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        val centerX = width / 2f
        val centerY = height / 2f
        val radius = (Math.min(width, height) - progressPaint.strokeWidth) / 2f

        val oval = RectF(centerX - radius, centerY - radius, centerX + radius, centerY + radius)

        // Draw the background circle
        canvas.drawArc(oval, 0f, 360f, false, backgroundPaint)

        // Draw the default segment
        val defaultSegmentEndAngle = 360f * defaultSegmentEnd / maxValue.toFloat()
        progressPaint.color = defaultSegmentColor
        canvas.drawArc(oval, 270f, defaultSegmentEndAngle, false, progressPaint)

        // Draw the first segment
        if (progress > 0) {
            if (progress <= firstSegmentEnd) {
                firstSegmentEndAngle = 360f * progress /  maxValue.toFloat()
                progressPaint.color = firstSegmentColor
                canvas.drawArc(
                    oval,
                    270f,
                    firstSegmentEndAngle,
                    false,
                    progressPaint
                )
            } else {
                firstSegmentEndAngle = 360f * firstSegmentEnd /  maxValue.toFloat()
                progressPaint.color = firstSegmentColor
                canvas.drawArc(
                    oval,
                    270f,
                    firstSegmentEndAngle,
                    false,
                    progressPaint
                )
            }

            // Draw the progress segment
            if (progress >= firstSegmentEnd) {
                val progressEndAngle = 360f * progress /  maxValue.toFloat()
                progressPaint.color = secondSegmentColor
                canvas.drawArc(
                    oval,
                    270f + firstSegmentEndAngle,
                    progressEndAngle - firstSegmentEndAngle,
                    false,
                    progressPaint
                )
            }
        }
    }
}
