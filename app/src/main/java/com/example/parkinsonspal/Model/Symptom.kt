package com.example.parkinsonspal.Model

import java.time.LocalDate
import java.time.LocalTime

data class Symptom(val patientId: Int, val description: String, val startTime: LocalTime, val endTime: LocalTime, val date: LocalDate) {
    override fun toString(): String {
        return "Symptom(patientId=$patientId, description='$description', startTime=$startTime, endTime=$endTime, date=$date)"
    }
}
