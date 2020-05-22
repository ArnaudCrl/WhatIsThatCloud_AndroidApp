package com.arnaudcayrol.WhatIsThatCloud.network

import java.io.Serializable

class CloudList  (
    private val altocumulus: Double,
    private val altostratus: Double,
    private val cirrocumulus: Double,
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
        "Cirrocumulus" to cirrocumulus,
        "Cirrostratus" to cirrostratus,
        "Cirrus" to cirrus,
        "Cumulonimbus" to cumulonimbus,
        "Cumulus" to cumulus,
        "Nimbostratus" to nimbostratus,
        "Stratocumulus" to stratocumulus,
        "Stratus" to stratus
    ).sortedByDescending{it.second}

    val urlList= mapOf(
        "Altocumulus" to "https://en.wikipedia.org/wiki/Altocumulus_cloud",
        "Altostratus" to "https://en.wikipedia.org/wiki/Altostratus_cloud",
        "Cirrocumulus" to "https://en.wikipedia.org/wiki/Cirrocumulus_cloud",
        "Cirrostratus" to "https://en.wikipedia.org/wiki/Cirrostratus_cloud",
        "Cirrus" to "https://en.wikipedia.org/wiki/Cirrus_cloud",
        "Cumulonimbus" to "https://en.wikipedia.org/wiki/Cumulonimbus_cloud",
        "Cumulus" to "https://en.wikipedia.org/wiki/Cumulus_cloud",
        "Nimbostratus" to "https://en.wikipedia.org/wiki/Nimbostratus_cloud",
        "Stratocumulus" to "https://en.wikipedia.org/wiki/Stratocumulus_cloud",
        "Stratus" to "https://en.wikipedia.org/wiki/Stratus_cloud"
    )

}