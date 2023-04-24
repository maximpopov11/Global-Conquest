package com.example.global_conquest

private const val STARTING_GOLD = 20

class Empire(val name: String) {
    var gold = STARTING_GOLD
    val provinces = arrayListOf<Province>()
    val regiments = arrayListOf<Regiment>()
    private val selectedRegiments = arrayListOf<Regiment>()

    /**
     * Throws IllegalStateException if the Empire does not have enough gold.
     */
    fun orderTrainRegiment(province: Province) {
        if (gold < TRAINING_COST) {
            throw IllegalStateException("Not enough gold: $gold")
        }
        gold -= TRAINING_COST
        province.orderTrainRegiment()
    }

    fun selectRegiment(regiment: Regiment) {
        selectedRegiments.add(regiment)
    }

    fun deselectRegiment(regiment: Regiment) {
        selectedRegiments.remove(regiment)
    }

    /**
     * Returns ArrayList of all regiments who were ordered illegally and thus did not obtain their
     * new orders.
     */
    fun orderSelectedRegiments(province: Province): ArrayList<Regiment> {
        val failedToOrder = arrayListOf<Regiment>()
        selectedRegiments.forEach {
            if (!it.order(province)) {
                failedToOrder.add(it)
            }
        }
        return failedToOrder
    }

    fun disbandSelectedRegiments() {
        selectedRegiments.forEach { it.disband() }
        selectedRegiments.clear()
    }

    fun combatUnderpaidPenalty(): Double {
        return if (gold >= 0) {
            1.0
        } else {
            val missingGold = -gold
            val totalMaintenanceCost = regiments.size * UPKEEP_COST
            1.0 - missingGold / totalMaintenanceCost
        }
    }
}