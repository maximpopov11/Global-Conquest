package com.example.global_conquest

class Empire(private val name: String) {
    private var gold = 20
    val regiments = arrayListOf<Regiment>()
    private val selectedRegiments = arrayListOf<Regiment>()

    /**
     * Throws IllegalStateException if the Empire does not have enough gold.
     */
    fun trainRegiment(province: Province) {
        if (gold < TRAINING_COST) {
            throw IllegalStateException("Not enough gold: $gold")
        }
        gold -= TRAINING_COST
        val regiment = Regiment(this, province)
        regiments.add(regiment)
        province.regiments.add(regiment)
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
}