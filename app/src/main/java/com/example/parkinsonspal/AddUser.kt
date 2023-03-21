package com.example.parkinsonspal

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.parkinsonspal.Model.CarerModel
import com.example.parkinsonspal.Model.DataBaseHelper
import com.example.parkinsonspal.Model.DoctorModel
import com.example.parkinsonspal.Model.PatientModel

class AddUser : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_user)

        val dbHelper = DataBaseHelper(this)

        val name = intent.getStringExtra("name")
        val email = intent.getStringExtra("email")
        val password = intent.getStringExtra("password")
        val userType = intent.getStringExtra("userType")

        val patient = PatientModel(name, email, password)
        val carer = CarerModel(name, email, password)
        val doctor = DoctorModel(name, email, password)

        if (userType == "Patient") {
            dbHelper.addPatient(patient)
        } else if (userType == "Carer") {
            dbHelper.addCarer(carer)
        } else if (userType == "Doctor") {
            dbHelper.addDoctor(doctor)
        }

        val intent = Intent(this, Login::class.java)
        startActivity(intent)
    }
}
