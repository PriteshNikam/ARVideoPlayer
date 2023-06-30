package com.example.arvideoplayer.utils

import android.util.Log

object Logger {

    private const val TAG = "ARTownhallapp"

    fun debug(value: String){
        Log.d(TAG,value)
    }
}