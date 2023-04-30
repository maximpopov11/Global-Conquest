package com.example.global_conquest

import kotlin.collections.HashMap
import kotlin.collections.HashSet
import kotlin.math.ceil
import kotlin.random.Random

const val GOLD_YIELD = 20

class Province(private val name: String) {
    var adjacentProvinces = arrayListOf<Province>()
    var empire: Empire? = null
        set(value) {
            empire?.provinces?.remove(this)
            field = value
            field?.provinces?.add(this)
        }
    val regiments = HashMap<Empire, HashSet<Regiment>>()
    var trainingRegiments = 0

    fun orderTrainRegiment() {
        trainingRegiments++
    }

    fun resolveCombat() {
        // Until 1 nation remains
        while(regiments.size > 1) {
            val casualtiesMap = HashMap<Empire, Double>()
            regiments.forEach { (empire, set) -> fillCasualtiesMap(casualtiesMap, empire, set)}
            casualtiesMap.forEach { (empire, casualties) -> issueCasualties(empire, casualties)}
            pruneDefeatedEmpires()
        }
    }

    private fun fillCasualtiesMap(casualtiesMap: HashMap<Empire, Double>, empire: Empire,
                                  regiments: HashSet<Regiment>) {
        val soldiers = regiments.sumOf { it.health }
        val casualties = Random.nextDouble(0.1, 0.2) * soldiers *
                empire.combatUnderpaidPenalty()
        casualtiesMap[empire] = casualties
    }

    private fun issueCasualties(empire: Empire, totalCasualties: Double) {
        val empireCasualties = totalCasualties / regiments.size
        regiments.forEach { (otherEmpire, defendingRegiments) ->
            if (otherEmpire != empire) {
                // TODO: test two 1 hp units fighting results in the fight ending
                val regimentCasualties = ceil(empireCasualties / defendingRegiments.size).toInt()
                defendingRegiments.forEach { it.takeCasualties(regimentCasualties) }
            }
        }
    }

    /**
     * Remove empires without remaining regiments.
     */
    private fun pruneDefeatedEmpires() {
        val iterator = regiments.entries.iterator()
        while (iterator.hasNext()) {
            val entry = iterator.next()
            if (entry.value.isEmpty()) {
                iterator.remove()
            }
        }
    }
}