package com.zxzlst.zxzstoragearrangement

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context

/*
提供全局上下文
 */

class ZxzStorageApplication : Application(){
    companion object {
        @SuppressLint("StaticFieldLeak")
        lateinit var context: Context
    }

    override fun onCreate(){
        super.onCreate()
        context = applicationContext
    }
}