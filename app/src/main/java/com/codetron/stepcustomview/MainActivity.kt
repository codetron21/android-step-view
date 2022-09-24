package com.codetron.stepcustomview

import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val view = findViewById<StepView>(R.id.step_view)
        var indicator = 0

        findViewById<Button>(R.id.button_next).setOnClickListener {
            indicator += 1
            view.setCurrentIndicator(indicator)
        }

        findViewById<Button>(R.id.button_prev).setOnClickListener {
            indicator -= 1
            view.setCurrentIndicator(indicator)
        }

    }
}