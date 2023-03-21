package com.example.parkinsonspal.Model

data class PatientModel(var name: String?, var email: String?, var password: String?){

    override fun toString(): String {
        return "Patient(name='$name', email='$email', password=$password)"
    }
}