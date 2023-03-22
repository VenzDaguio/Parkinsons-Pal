package com.example.parkinsonspal.Model

import java.time.LocalDate
import java.time.LocalTime

data class Symptom(val description: String, val startTime: String, val endTime: String) {
    override fun toString(): String {
        return "Symptom(description='$description', startTime=$startTime, endTime=$endTime"
    }
}
