package com.basindevapp.speed_0.util

import android.view.View

abstract class DoubleClickListener : View.OnClickListener {
    private val DOUBLE_CLICK_TIME_DELTA: Long = 300 // milliseconds
    private var lastClickTime: Long = 0

    override fun onClick(v: View) {
        val clickTime = System.currentTimeMillis()
        if (clickTime - lastClickTime < DOUBLE_CLICK_TIME_DELTA) {
            onDoubleClick(v)
        } else {
            onSingleClick(v)
        }
        lastClickTime = clickTime
    }

    abstract fun onSingleClick(v: View)
    abstract fun onDoubleClick(v: View)
}