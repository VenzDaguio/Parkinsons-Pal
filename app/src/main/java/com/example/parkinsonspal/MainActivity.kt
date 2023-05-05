package com.example.parkinsonspal

import android.content.ContentValues.TAG
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import com.example.parkinsonspal.Model.DataBaseHelper

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    fun btSignUp(view: View){
        val intent = Intent(this, Register::class.java)
        startActivity(intent)
    }
    fun btLogIn(view: View){
        val intent = Intent(this, Login::class.java)
        startActivity(intent)
    }

}

