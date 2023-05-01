package com.example.global_conquest

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        createGame()
    }

    private fun createGame() {
        val map = Map(this, "maps/small_europe/")
        val game = Game(map)
    }
}