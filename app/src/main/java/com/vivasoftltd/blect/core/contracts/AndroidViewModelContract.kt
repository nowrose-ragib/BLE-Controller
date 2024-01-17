package com.vivasoftltd.blect.core.contracts

import android.app.Application

open class AndroidViewModelContract<S>(private val application: Application) :
  ViewModelContract<S>()