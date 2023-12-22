package com.hunglvv.stickmananimation.utils

import timber.log.Timber

fun Any.log() {
    Timber.tag("Hunglv").d(if (this is String) this else this.toString())
}