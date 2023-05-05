package com.example.parkinsonspal.Dialog

import android.app.AlarmManager
import android.app.AlertDialog
import android.app.Dialog
import android.app.PendingIntent
import android.content.Context
import android.content.Context.ALARM_SERVICE
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
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

class EditMedicationDialog(
    context: Context,
    private val patientId: Int,
    private val medicationId: Int,
    private val medicationAdapter: MedicationAdapter
) : Dialog(context) {

    var mName: String? = null
    var mTime: LocalTime? = null
    var mQuantity: Int? = null
    private var medicationTaken: Boolean = false

    private val dbHelper: DataBaseHelper = DataBaseHelper(context)
    private val ReminderBroadcastReceiver: ReminderBroadcastReceiver = ReminderBroadcastReceiver()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.edit_medication_dialog)

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

        // initialise views
        val nameEditText = findViewById<EditText>(R.id.nameEditText)
        val timeSpinner = findViewById<Spinner>(R.id.time_spinner)
        val quantityEditText = findViewById<EditText>(R.id.quantityEditText)

        val spinnerAdapter = ArrayAdapter<String>(
            context,
            android.R.layout.simple_spinner_dropdown_item,
            times
        )
        timeSpinner.adapter = spinnerAdapter

        // pre-fill input fields
        if (!mName.isNullOrEmpty()) {
            nameEditText.setText(mName)
        }
        if (mTime != null) {
            timeSpinner.setSelection(times.indexOf(mTime.toString()))
        }
        if (mQuantity != null) {
            quantityEditText.setText(mQuantity.toString())
        }
        // set click listener for add button
        val editButton = findViewById<Button>(R.id.editButton)
        editButton.setOnClickListener {
            // save medication data
            mName = nameEditText.text.toString()
            mTime = LocalTime.parse(timeSpinner.selectedItem.toString())
            mQuantity = quantityEditText.text.toString().toIntOrNull()

            if (mName.isNullOrEmpty()) {
                Toast.makeText(context, "Please enter a medication name", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (mQuantity == null) {
                Toast.makeText(context, "Please enter a medication quantity", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // insert medication into database
            dbHelper.editMedication(
                patientId,
                medicationId,
                mName!!,
                mTime!!,
                mQuantity!!
            )

            // set up a reminder for the medication
            val reminderTime = LocalTime.parse(mTime.toString())
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

        val deleteButton = findViewById<Button>(R.id.deleteButton)
        deleteButton.setOnClickListener {
            val confirmationDialog = AlertDialog.Builder(context)
                .setTitle("Confirm Deletion")
                .setMessage("Are you sure you want to delete this medication?")
                .setPositiveButton("Delete") { _, _ ->
                    // delete medication from database
                    dbHelper.deleteMedication(medicationId)

                    // cancel any reminders for the medication
                    val reminderIntent = Intent(context, ReminderBroadcastReceiver::class.java)
                    val pendingIntent = PendingIntent.getBroadcast(
                        context,
                        0,
                        reminderIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT
                    )
                    val alarmManager = context.getSystemService(ALARM_SERVICE) as AlarmManager
                    alarmManager.cancel(pendingIntent)

                    // notify adapter that data has changed
                    medicationAdapter.notifyDataSetChanged()

                    dismiss()
                }
                .setNegativeButton("Cancel", null)
                .create()

            confirmationDialog.setOnShowListener {
                val buttonPositive = confirmationDialog.getButton(DialogInterface.BUTTON_POSITIVE)
                val buttonNegative = confirmationDialog.getButton(DialogInterface.BUTTON_NEGATIVE)

                buttonPositive.setBackgroundResource(R.color.gray_blue)
                buttonNegative.setBackgroundResource(R.color.gray_blue)

                buttonPositive.setTextColor(Color.RED)
                buttonNegative.setTextColor(Color.RED)
            }
            confirmationDialog.show()
        }
    }

    fun getMedicationName(): String? {
        return mName
    }

    fun getMedicationTime(): LocalTime? {
        return mTime
    }

    fun getMedicationQuantity(): Int? {
        return mQuantity
    }
}



