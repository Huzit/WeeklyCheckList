package com.weekly.weeklychecklist

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class WeeklyApplication: Application() {
    override fun onCreate() {
        super.onCreate()
    }
}