package com.gedar0082.debater.util

import android.annotation.SuppressLint
import com.gedar0082.debater.model.net.pojo.ArgumentJson
import de.blox.graphview.Edge
import de.blox.graphview.Graph
import de.blox.graphview.Node
import java.sql.Timestamp
import java.text.SimpleDateFormat

object Util {

    @SuppressLint("SimpleDateFormat")
    public fun getCurrentDate() : Timestamp {
        val pattern = "yyyy-MM-dd HH:mm:ss.SSSSSSSSS"
        val simpleDateFormat = SimpleDateFormat(pattern)
        return Timestamp.valueOf(simpleDateFormat.format(System.currentTimeMillis()))
    }



}