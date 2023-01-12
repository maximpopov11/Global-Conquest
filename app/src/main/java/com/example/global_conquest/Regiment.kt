package com.example.global_conquest

const val TRAINING_COST = 10
private const val UPKEEP_COST = 5
const val MAX_HEALTH = 1000

class Regiment(private val empire: Empire, private var province: Province) {
    private var health = MAX_HEALTH
    private var order: Province? = null
    private var selected = false

    fun changeSelection() {
        selected = !selected
        if (selected) {
            empire.selectRegiment(this)
        } else {
            empire.deselectRegiment(this)
        }
    }

    /**
     * Returns true if legal order and false otherwise in which case the order was not given.
     */
    fun order(province: Province): Boolean {
        return if (this.province.adjacentProvinces.contains(province)) {
            order = province
            true
        } else {
            false
        }
    }

    fun disband() {
        empire.regiments.remove(this)
        province.regiments.remove(this)
    }
}