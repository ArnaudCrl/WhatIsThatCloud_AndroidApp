package com.arnaudcayrol.WhatIsThatCloud.network

import java.io.Serializable

class CloudList  (
    private val altocumulus: Double,
    private val altostratus: Double,
    private val cirrocumuls: Double,
    private val cirrostratus: Double,
    private val cirrus: Double,
    private val cumulonimbus: Double,
    private val cumulus: Double,
    private val nimbostratus: Double,
    private val stratocumulus: Double,
    private val stratus: Double
) : Serializable {

     val resultList= listOf(
        "Altocumulus" to altocumulus,
        "Altostratus" to altostratus,
        "Cirrocumuls" to cirrocumuls,
        "Cirrostratus" to cirrostratus,
        "Cirrus" to cirrus,
        "Cumulonimbus" to cumulonimbus,
        "Cumulus" to cumulus,
        "Nimbostratus" to nimbostratus,
        "Stratocumulus" to stratocumulus,
        "Stratus" to stratus
    ).sortedByDescending{it.second}
//
//
//    fun getBest(): String {
//        return "The most plausible result is ${result_list[0].first} with ${(result_list[0].second * 100).toInt()}% probability"
//    }
//
//    fun getBests(): String {
//        return "RESULT :\n" +
//                "${result_list[0].first} with ${(result_list[0].second * 100).toInt()}% confidence\n" +
//                "${result_list[1].first} with ${(result_list[1].second * 100).toInt()}% confidence\n" +
//                "${result_list[2].first} with ${(result_list[2].second * 100).toInt()}% confidence"
//    }
}