package com.vivasoftltd.blect.core.contracts

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

open class ViewModelContract<S> : ViewModel() {
  val mutableState = MutableLiveData<S>()
  val immutableState: LiveData<S> get() = mutableState
}