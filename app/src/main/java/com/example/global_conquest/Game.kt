package com.example.global_conquest

class Game(private val map: Map) {
    private var turn = 1
    private val victoryProvinceCount = map.provinces.size

    fun endTurn() {
        executeOrders()
        resolveCombats()
        conductAdjustments()
        turn++
    }

    private fun executeOrders() {
        trainRegiments()
        moveRegiments()
    }

    private fun trainRegiments() {
        map.provinces.forEach { province ->
            for (i in 0..province.trainingRegiments) {
                if (province.empire != null) {
                    val empire = province.empire!!
                    val regiment = Regiment(empire, province)
                    empire.regiments.add(regiment)
                    // TODO: test that this correctly:
                    //  adds to set if empire already has unit(s) there
                    //  creates new set and adds otherwise
                    province.regiments.compute(empire) { _, set ->
                        (set ?: HashSet()).apply { add(regiment) }
                    }
                }
            }
            province.trainingRegiments = 0
        }
    }

    private fun moveRegiments() {
        map.empires.forEach { empire -> empire.regiments.forEach { it.followOrder() } }
    }

    private fun resolveCombats() {
        map.provinces.forEach { it.resolveCombat() }
    }

    private fun conductAdjustments() {
        victoryCheck()
        defeatCheck()
        bailDebt()
        provinceYield()
        healRegiments()
        payRegiments()
    }

    private fun victoryCheck() {
        map.empires.forEach {
            if (it.provinces.size > victoryProvinceCount) {
                gameWon(it)
            }
        }
    }

    private fun gameWon(empire: Empire) {
        println(empire.name + " won on turn " + turn + "!")
    }

    private fun defeatCheck() {
        map.empires.forEach {
            if (it.provinces.size == 0) {
                gameLost(it)
            }
        }
    }

    private fun gameLost(empire: Empire) {
        println(empire.name + " was defeated on turn " + turn + "!")
    }

    private fun bailDebt() {
        map.empires.forEach {
            if (it.gold < 0) {
                it.gold = 0
            }
        }
    }

    private fun provinceYield() {
        map.empires.forEach { it.gold += it.provinces.size * GOLD_YIELD }
    }

    private fun healRegiments() {
        map.empires.forEach { empire -> empire.regiments.forEach { it.heal() } }
    }

    private fun payRegiments() {
        map.empires.forEach { it.gold -= it.regiments.size * UPKEEP_COST }
    }
}