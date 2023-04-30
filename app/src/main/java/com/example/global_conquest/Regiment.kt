package com.example.global_conquest

const val TRAINING_COST = 10
const val UPKEEP_COST = 5
const val MAX_HEALTH = 1000
private const val HEALTH_RECOVERY = 100

class Regiment(private val empire: Empire, private var province: Province) {
    var health = MAX_HEALTH
        set(value) {
            if (value < 0) {
                field = 0
                disband()
            } else if (value > MAX_HEALTH) {
                field = MAX_HEALTH
            } else {
                field = value
            }
        }
    private var order: Province = province
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

    fun followOrder() {
        province.regiments[empire]?.remove(this)
        province = order
        province.regiments.compute(empire) { _, set ->
            (set ?: HashSet()).apply { add(this@Regiment) }
        }
    }

    fun disband() {
        empire.regiments.remove(this)
        province.regiments[empire]?.remove(this)
    }

    fun takeCasualties(casualties: Int) {
        health -= casualties
    }

    fun heal() {
        health += HEALTH_RECOVERY
    }
}