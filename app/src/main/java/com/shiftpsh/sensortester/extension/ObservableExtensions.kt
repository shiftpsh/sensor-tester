package com.shiftpsh.sensortester.extension

import android.databinding.Observable
import io.reactivex.disposables.Disposables

fun Observable.onPropertyChanged(body: (sender: Observable?, propertyId: Int) -> Unit) = object : Observable.OnPropertyChangedCallback() {
    override fun onPropertyChanged(sender: Observable?, propertyId: Int) = body(sender, propertyId)
}.also {
    addOnPropertyChangedCallback(it)
}.let {
    Disposables.fromAction { removeOnPropertyChangedCallback(it) }
}