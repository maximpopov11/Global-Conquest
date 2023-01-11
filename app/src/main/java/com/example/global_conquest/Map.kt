package com.example.global_conquest

import android.content.Context

class Map(private val context: Context, private val mapDirectoryPath: String) {
    private val provinces = arrayListOf<Province>()
    private val empires = arrayListOf<Empire>()

    init {
        createProvinces()
        setProvinceAdjacencies()
        createEmpires()
    }

    private fun createProvinces() {
        val provincesPath = mapDirectoryPath.plus("Provinces")
        context.resources.assets.open(provincesPath).bufferedReader().forEachLine { line ->
            val parts = line.split(',')
            val id = parts[0].toInt()
            val name = parts[1]
            provinces.add(Province(id, name))
        }
    }

    private fun setProvinceAdjacencies() {
        val provinceAdjacenciesPath = mapDirectoryPath.plus("ProvinceAdjacencies")
        context.resources.assets.open(provinceAdjacenciesPath).bufferedReader().forEachLine { line ->
            val parts = line.split(',')
            val province1 = provinces[parts[0].toInt()]
            val province2 = provinces[parts[1].toInt()]
            province1.adjacentProvinces.add(province2)
            province2.adjacentProvinces.add(province1)
        }
    }

    private fun createEmpires() {
        val empiresPath = mapDirectoryPath.plus("Empires")
        context.resources.assets.open(empiresPath).bufferedReader().forEachLine { line ->
            val parts = line.split(',')
            val empireId = parts[0].toInt()
            val name = parts[1]
            val provinceId = parts[2].toInt()
            val empire = Empire(empireId, name)
            empires.add(empire)
            provinces[provinceId].empire = empire
        }
    }
}