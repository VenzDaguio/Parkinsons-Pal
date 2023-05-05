package com.example.parkinsonspal.Dialog

import android.app.AlarmManager
import android.app.Dialog
import android.app.PendingIntent
import android.content.Context
import android.content.Context.ALARM_SERVICE
import android.content.Intent
import android.os.Bundle
import android.view.WindowManager
import android.widget.*
import com.example.parkinsonspal.Adapter.MedicationAdapter
import com.example.parkinsonspal.Model.DataBaseHelper
import com.example.parkinsonspal.R
import com.example.parkinsonspal.Receiver.ReminderBroadcastReceiver
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.util.*

class MedicationDialog(
    context: Context,
    private val patientId: Int,
    private val medicationAdapter: MedicationAdapter
) : Dialog(context) {

    private var medicationName: String? = null
    private var medicationTime: LocalTime? = null
    private var medicationQuantity: Int? = null
    private var medicationReminder: String? = null
    private var medicationTaken: Boolean = false

    private val dbHelper: DataBaseHelper = DataBaseHelper(context)
    private val ReminderBroadcastReceiver: ReminderBroadcastReceiver = ReminderBroadcastReceiver()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.medication_dialog)

        val window = window
        window?.setLayout(
            (context.resources.displayMetrics.widthPixels * 0.9).toInt(),
            WindowManager.LayoutParams.WRAP_CONTENT
        )

        val times = ArrayList<String>()
        for (hour in 0..23) {
            for (minute in 0..59 step 15) {
                val time = String.format("%02d:%02d", hour, minute)
                times.add(time)
            }
        }

        // initialize views
        val nameEditText = findViewById<EditText>(R.id.nameEditText)
        val timeSpinner = findViewById<Spinner>(R.id.time_spinner)
        val quantityEditText = findViewById<EditText>(R.id.quantityEditText)

        val spinnerAdapter = ArrayAdapter<String>(
            context,
            android.R.layout.simple_spinner_dropdown_item,
            times
        )
        timeSpinner.adapter = spinnerAdapter

        // set click listener for add button
        val addButton = findViewById<Button>(R.id.addButton)
        addButton.setOnClickListener {
            // save medication data
            medicationName = nameEditText.text.toString()
            medicationTime = LocalTime.parse(timeSpinner.selectedItem.toString())
            medicationQuantity = quantityEditText.text.toString().toIntOrNull()

            if (medicationName.isNullOrEmpty()) {
                Toast.makeText(context, "Please enter a medication name", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (medicationQuantity == null) {
                Toast.makeText(context, "Please enter a medication quantity", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // insert medication into database
            dbHelper.insertMedication(
                patientId,
                medicationName!!,
                medicationTime!!,
                medicationQuantity!!,
                medicationTaken
            )

            // set up a reminder for the medication
            val reminderTime = LocalTime.parse(medicationTime.toString())
            val reminderIntent = Intent(context, ReminderBroadcastReceiver::class.java)
            val pendingIntent = PendingIntent.getBroadcast(
                context,
                0,
                reminderIntent,
                PendingIntent.FLAG_UPDATE_CURRENT
            )
            val alarmManager = context.getSystemService(ALARM_SERVICE) as AlarmManager
            val now = LocalDateTime.now()
            val reminderDateTime = LocalDateTime.of(now.toLocalDate(), reminderTime)
            var reminderMillis = reminderDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
            if (reminderMillis < System.currentTimeMillis()) {
                reminderMillis += 86400000 // add 24 hours if reminder time has already passed
            }
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, reminderMillis, pendingIntent)
            ReminderBroadcastReceiver.createNotificationChannel(context) // create notification channel

            // notify adapter that data has changed
            medicationAdapter.notifyDataSetChanged()

            dismiss()
        }

        val cancelButton = findViewById<Button>(R.id.cancelButton)
        cancelButton.setOnClickListener {

            dismiss()
        }
    }

    fun getMedicationName(): String? {
        return medicationName
    }

    fun getMedicationTime(): LocalTime? {
        return medicationTime
    }

    fun getMedicationQuantity(): Int? {
        return medicationQuantity
    }

    fun getMedicationReminder(): String? {
        return medicationReminder
    }
}