package com.gedar0082.debater.util


import java.sql.Timestamp

object Util {


    fun getCurrentDate() : Timestamp {
        return Timestamp(System.currentTimeMillis())
    }



}