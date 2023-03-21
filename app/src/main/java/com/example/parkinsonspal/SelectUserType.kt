package com.example.parkinsonspal

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.RadioButton
import android.widget.RadioGroup

class SelectUserType : AppCompatActivity() {

    private lateinit var userTypeGroup: RadioGroup

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_select_user_type)

        userTypeGroup = findViewById(R.id.userTypeGroup)
    }

    fun btNext(view: View) {
        val name = intent.getStringExtra("name")
        val email = intent.getStringExtra("email")
        val password = intent.getStringExtra("password")

        val selectedId = userTypeGroup.checkedRadioButtonId

        val selectedRadioButton = findViewById<RadioButton>(selectedId)

        val userType = selectedRadioButton.text.toString()

        val intent = Intent(this, AddUser::class.java)
        intent.putExtra("userType", userType)
        intent.putExtra("name", name)
        intent.putExtra("email", email)
        intent.putExtra("password", password)

        startActivity(intent)
    }
}

