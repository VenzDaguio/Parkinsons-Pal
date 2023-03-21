package com.example.parkinsonspal

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.Toast
import com.example.parkinsonspal.Model.DataBaseHelper

class Login : AppCompatActivity() {

    private val dbHelper: DataBaseHelper = DataBaseHelper(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
    }

    fun btRegister(view: View){
        val intent = Intent(this, Register::class.java)
        startActivity(intent)
    }

    fun btLogIn(view: View) {
        val email = findViewById<EditText>(R.id.email).text.toString()
        val password = findViewById<EditText>(R.id.password).text.toString()
        val checkLogInPatient: Boolean = dbHelper.checkLogInPatient(email, password)
        val checkLogInCarer: Boolean = dbHelper.checkLogInCarer(email, password)
        val checkLogInDoctor: Boolean = dbHelper.checkLogInDoctor(email, password)

        if(email == "" || password == "")
            Toast.makeText(this, "Please enter all the fields", Toast.LENGTH_SHORT).show()
        else if (checkLogInPatient == true) {
            val patientId = dbHelper.getPatientId(email, password)
            val intent = Intent(this, TrackHealth::class.java)
            intent.putExtra("patient_id", patientId)
            Log.d("LogIn","Patient ID: $patientId")
            startActivity(intent)
        }
        else if (checkLogInCarer == true) {

        }
        else if (checkLogInDoctor == true) {

        }
        else
            Toast.makeText(this, "Invalid Credentials", Toast.LENGTH_SHORT).show()
    }
}