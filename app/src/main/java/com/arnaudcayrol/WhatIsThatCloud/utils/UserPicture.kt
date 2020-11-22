package com.arnaudcayrol.WhatIsThatCloud.utils

import android.os.Parcelable
import com.google.firebase.database.Exclude
import com.google.firebase.database.IgnoreExtraProperties
import kotlinx.android.parcel.Parcelize
import java.sql.Timestamp


@IgnoreExtraProperties
data class UserPicture(
    var uid : String? = "",
    var url: String? = "",
    var author: String? = "",
    var prediction: String? = "",
    var fav_count: Int = 0,
    var fav: MutableMap<String, Boolean> = HashMap(),
    var date_uploaded : Long = System.currentTimeMillis(),
    var agree_with_prediction: MutableMap<String, Boolean> = HashMap(),
    var disagree_with_prediction: MutableMap<String, Boolean> = HashMap()
) {

    @Exclude
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "uid" to uid,
            "url" to url,
            "author" to author,
            "prediction" to prediction,
            "fav_count" to fav_count,
            "fav" to fav,
            "date_uploaded" to date_uploaded,
            "disagree_with_prediction" to disagree_with_prediction,
            "agree_with_prediction" to agree_with_prediction
        )
    }
}