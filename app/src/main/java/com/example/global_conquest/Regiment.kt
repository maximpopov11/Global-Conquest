package com.example.global_conquest

private const val TRAINING_COST = 10
private const val UPKEEP_COST = 5

class Regiment(private val empire: Empire, private var province: Province) {
    private var health = 1000
    private var order: Province? = null
}