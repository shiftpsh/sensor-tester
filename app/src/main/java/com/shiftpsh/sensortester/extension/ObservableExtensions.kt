package com.shiftpsh.sensortester.extension

import android.databinding.Observable

fun Observable.onPropertyChanged(body: (sender: Observable?, propertyId: Int) -> Unit): Observable.OnPropertyChangedCallback {
    val callback = object : Observable.OnPropertyChangedCallback() {
        override fun onPropertyChanged(sender: Observable?, propertyId: Int) {
            body(sender, propertyId)
        }
    }
    addOnPropertyChangedCallback(callback)
    return callback
}