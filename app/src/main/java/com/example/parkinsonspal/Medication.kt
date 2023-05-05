package com.example.parkinsonspal;

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime;

data class Medication(
    val id: Int,
    val name: String,
    val time: LocalTime,
    val quantity: Int,
    var taken: Int = 0,
    var takenDate: String?
)



