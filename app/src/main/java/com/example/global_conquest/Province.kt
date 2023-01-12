package com.example.global_conquest

private const val GOLD_YIELD = 20

class Province(private val name: String) {
    var adjacentProvinces = arrayListOf<Province>()
    var empire: Empire? = null
    val regiments = arrayListOf<Regiment>()
}