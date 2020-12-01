package com.arnaudcayrol.WhatIsThatCloud.utils

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
class User(val username: String, val experience: Int): Parcelable {
    constructor() : this("", 0)
}