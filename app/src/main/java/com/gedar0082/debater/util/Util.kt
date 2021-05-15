package com.gedar0082.debater.util

import android.annotation.SuppressLint
import java.sql.Timestamp
import java.text.SimpleDateFormat

object Util {

    @SuppressLint("SimpleDateFormat")
    fun getCurrentDate() : Timestamp {
        val pattern = "yyyy-MM-dd HH:mm:ss.SSS"
        val simpleDateFormat = SimpleDateFormat(pattern)
        return Timestamp.valueOf(simpleDateFormat.format(System.currentTimeMillis()))
    }



}