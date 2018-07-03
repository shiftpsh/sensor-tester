package com.shiftpsh.sensortester

import android.arch.lifecycle.ViewModel

abstract class BaseItemViewModel<in T> : ViewModel() {
    abstract fun setItem(item: T)
}
