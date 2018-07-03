package com.shiftpsh.sensortester

import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.LifecycleObserver
import android.arch.lifecycle.OnLifecycleEvent
import android.databinding.BaseObservable

abstract class BaseViewModel : LifecycleObserver, BaseObservable() {

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    abstract fun onCreate()

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    abstract fun onResume()

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    abstract fun onPause()

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    abstract fun onDestroy()
}