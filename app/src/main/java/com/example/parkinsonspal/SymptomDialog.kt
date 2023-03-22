package com.example.parkinsonspal

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.*
import androidx.fragment.app.DialogFragment
import com.example.parkinsonspal.Model.Symptom
import com.example.parkinsonspal.Model.DataBaseHelper
import java.time.LocalDate
import java.time.LocalTime

class SymptomDialog(context: Context, private val patientId: Int, private val symptomAdapter: SymptomAdapter) : Dialog(context) {

    private var startTime: LocalTime? = null
    private var endTime: LocalTime? = null
    private var symptomDescription: String? = null
    private val mContext = context
    private val dbHelper: DataBaseHelper = DataBaseHelper(mContext)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.symptom_dialog)

        val window = window
        window?.setLayout((context.resources.displayMetrics.widthPixels * 0.9).toInt(), WindowManager.LayoutParams.WRAP_CONTENT)

        // initialize start and end time pickers
        val startTimePicker = findViewById<Spinner>(R.id.startTimeSpinner)
        val endTimePicker = findViewById<Spinner>(R.id.endTimeSpinner)

        // set up the adapters for the spinners
        val times = ArrayList<String>()
        for (hour in 0..23) {
            for (minute in 0..59 step 15) {
                val time = String.format("%02d:%02d", hour, minute)
                times.add(time)
            }
        }
        val adapter = ArrayAdapter(context, android.R.layout.simple_spinner_dropdown_item, times)
        startTimePicker.adapter = adapter
        endTimePicker.adapter = adapter

        // set click listener for save button
        val saveButton = findViewById<Button>(R.id.saveButton)
        saveButton.setOnClickListener {
            // save selected times and symptom description
            startTime = LocalTime.parse(startTimePicker.selectedItem as String)
            endTime = LocalTime.parse(endTimePicker.selectedItem as String)
            symptomDescription = findViewById<EditText>(R.id.descriptionEditText).text.toString()

            // log patientId and symptom data
            Log.d("SymptomDialog", "Patient ID: $patientId")
            Log.d("SymptomDialog", "Symptom Description: $symptomDescription")
            Log.d("SymptomDialog", "Start Time: $startTime")
            Log.d("SymptomDialog", "End Time: $endTime")

            val currentDate = LocalDate.now()
            dbHelper.insertSymptom(patientId, symptomDescription!!, startTime!!, endTime!!, currentDate)

            symptomAdapter.notifyDataSetChanged()

            dismiss()
        }

        // set click listener for cancel button
        val cancelButton = findViewById<Button>(R.id.cancelButton)
        cancelButton.setOnClickListener {

            dismiss()
        }
    }

    fun getStartTime(): LocalTime? {
        return startTime
    }

    fun getEndTime(): LocalTime? {
        return endTime
    }

    fun getSymptomDescription(): String? {
        return symptomDescription
    }
}


