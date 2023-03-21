package com.example.parkinsonspal.Model

data class DoctorModel(var name: String?, var email: String?, var password: String?){

    override fun toString(): String {
        return "Doctor(name='$name', email='$email', password=$password)"
    }
}