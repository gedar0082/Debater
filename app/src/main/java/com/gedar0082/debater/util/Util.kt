package com.gedar0082.debater.util


import java.sql.Timestamp
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.*

object Util {


    fun getCurrentDate() : Timestamp {
        return Timestamp(System.currentTimeMillis())
    }

    fun getLocalTimeFromGMTTimestamp(timestamp: Timestamp) : String{
        val zonedDateTime = timestamp.toInstant().atZone(ZoneId.systemDefault())
        val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy - HH:mm")
        return zonedDateTime.format(formatter)
    }



}