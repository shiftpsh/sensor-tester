package com.shiftpsh.sensortester

import android.app.Application
import android.support.annotation.Nullable
import android.util.Log
import android.util.Log.INFO
import timber.log.Timber
import timber.log.Timber.DebugTree

class SensorTesterApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        if (BuildConfig.DEBUG) {
            Timber.plant(DebugTree())
        } else {
            Timber.plant(CrashReportingTree())
        }
    }

    // TODO implement crash reporting
    private class CrashReportingTree : Timber.Tree() {
        override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
        }

        fun isLoggable(priority: Int, @Nullable tag: String): Boolean {
            return priority >= INFO
        }

        protected fun log(priority: Int, tag: String, t: Throwable?, message: String) {
            if (priority == Log.VERBOSE || priority == Log.DEBUG) {
                return
            }
        }
    }
}