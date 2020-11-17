package com.arnaudcayrol.WhatIsThatCloud.utils

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
class UserImage(val url: String, val prediction: String, var ratings : MutableList<String>): Parcelable {
    constructor() : this("", "",  mutableListOf<String>())
}