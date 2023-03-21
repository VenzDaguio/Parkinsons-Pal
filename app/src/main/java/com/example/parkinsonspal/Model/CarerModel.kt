package com.example.parkinsonspal.Model

data class CarerModel(var name: String?, var email: String?, var password: String?){

    override fun toString(): String {
        return "Carer(name='$name', email='$email', password=$password)"
    }
}