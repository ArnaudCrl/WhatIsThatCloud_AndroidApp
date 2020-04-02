package com.example.cameratest.network

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

    private val result_list= mapOf(
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
    )

    fun getBest(): String {
        var max_key = ""
        var max_value = 0.0
        for (elt in result_list){
            if (elt.value > max_value){
                max_value = elt.value
                max_key = elt.key
            }
        }
        return "The most plausible result is ${max_key} with ${(max_value * 100).toInt()}% probability"
    }
}