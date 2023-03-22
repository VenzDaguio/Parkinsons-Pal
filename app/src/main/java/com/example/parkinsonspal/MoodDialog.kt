package com.example.parkinsonspal

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import com.example.parkinsonspal.Model.DataBaseHelper
import java.time.LocalDate

class MoodDialog(context: Context, private val patientId: Int, private val btnMood: Button) : Dialog(context) {

    private val dbHelper = DataBaseHelper(context)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.mood_dialog)

        val btnHappy = findViewById<Button>(R.id.btnHappy)
        val btnNeutral = findViewById<Button>(R.id.btnNeutral)
        val btnSad = findViewById<Button>(R.id.btnSad)

        btnHappy.setOnClickListener {
            btnMood.text = "Happy"
            insertMoodData("Happy")
            dismiss()
        }

        btnNeutral.setOnClickListener {
            btnMood.text = "Neutral"
            insertMoodData("Neutral")
            dismiss()
        }

        btnSad.setOnClickListener {
            btnMood.text = "Sad"
            insertMoodData("Sad")
            dismiss()
        }
    }

    private fun insertMoodData(mood: String) {
        val currentDate = LocalDate.now()
        dbHelper.insertOrUpdateMood(patientId, mood, currentDate)
        btnMood.text = mood
    }

    fun getMoodForPatient(patientId: Int, date: LocalDate): String? {
        return dbHelper.getMoodForPatient(patientId, date)
    }
}