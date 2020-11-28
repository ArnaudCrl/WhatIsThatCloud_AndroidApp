package com.arnaudcayrol.WhatIsThatCloud.network

import java.io.Serializable

class CloudList  (
    // Ordered pairs of (cloud name , cloud proba ) ordered by probability
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

}