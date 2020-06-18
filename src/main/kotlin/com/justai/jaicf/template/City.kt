package com.justai.jaicf.template

import kotlinx.serialization.Serializable

@Serializable
data class City(
    val name : String,
    val lat: Double,
    val lon: Double
)