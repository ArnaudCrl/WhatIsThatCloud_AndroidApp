package com.arnaudcayrol.WhatIsThatCloud.network

import java.io.Serializable

class CloudList  (
    val altocumulus: Double,
    val altostratus: Double,
    val cirrocumuls: Double,
    val cirrostratus: Double,
    val cirrus: Double,
    val cumulonimbus: Double,
    val cumulus: Double,
    val nimbostratus: Double,
    val stratocumulus: Double,
    val stratus: Double
) : Serializable {

     val result_list= listOf(
        "altocumulus" to altocumulus,
        "altostratus" to altostratus,
        "cirrocumuls" to cirrocumuls,
        "cirrostratus" to cirrostratus,
        "cirrus" to cirrus,
        "cumulonimbus" to cumulonimbus,
        "cumulus" to cumulus,
        "nimbostratus" to nimbostratus,
        "stratocumulus" to stratocumulus,
        "stratus" to stratus
    ).sortedByDescending(){ it.second }


    fun getBest(): String {
        return "The most plausible result is ${result_list[0].first} with ${(result_list[0].second * 100).toInt()}% probability"
    }

    fun getBests(): String {
        return "RESULT :\n" +
                "${result_list[0].first} with ${(result_list[0].second * 100).toInt()}% confidence\n" +
                "${result_list[1].first} with ${(result_list[1].second * 100).toInt()}% confidence\n" +
                "${result_list[2].first} with ${(result_list[2].second * 100).toInt()}% confidence"
    }
}