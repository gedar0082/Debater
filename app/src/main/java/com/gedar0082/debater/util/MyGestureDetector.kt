package com.gedar0082.debater.util

import android.view.GestureDetector
import android.view.MotionEvent

class MyGestureDetector: GestureDetector.SimpleOnGestureListener() {

    override fun onSingleTapConfirmed(e: MotionEvent?): Boolean {
        return super.onSingleTapConfirmed(e)
    }

    override fun onLongPress(e: MotionEvent?) {
        super.onLongPress(e)
    }
}