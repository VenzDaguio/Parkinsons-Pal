package com.example.parkinsonspal.Fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CalendarView
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.parkinsonspal.Model.DataBaseHelper
import com.example.parkinsonspal.R
import com.example.parkinsonspal.Adapter.SymptomAdapter
import com.example.parkinsonspal.TrackHealthViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class TrackFragment : Fragment() {
    private lateinit var viewModel: TrackHealthViewModel
    private lateinit var calendarView: CalendarView
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: SymptomAdapter
    private lateinit var dbHelper: DataBaseHelper
    private var selectedDate: LocalDate = LocalDate.now()
    private lateinit var tvMood: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_track, container, false)
        calendarView = view.findViewById(R.id.calendarView)
        recyclerView = view.findViewById(R.id.recyclerView)

        dbHelper = DataBaseHelper(requireContext())
        viewModel = ViewModelProvider(requireActivity()).get(TrackHealthViewModel::class.java)
        adapter = SymptomAdapter(emptyList())

        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = adapter

        tvMood = view.findViewById(R.id.tvMood)

        // Listen for date changes
        calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
            selectedDate = LocalDate.of(year, month + 1, dayOfMonth)
            updateRecyclerView()

            // Update the current date text view
            val currentDateText = selectedDate.format(DateTimeFormatter.ofPattern("EE, dd MMM"))
            view.findViewById<TextView>(R.id.tv_current_date).text = currentDateText
        }

        // Set the initial selected date to today
        selectedDate = LocalDate.now()

        // Set the calendar view to the current date
        calendarView.date = selectedDate.toEpochDay() * 86400000

        // Update the current date text view
        val currentDateText = selectedDate.format(DateTimeFormatter.ofPattern("EE, dd MMM"))
        view.findViewById<TextView>(R.id.tv_current_date).text = currentDateText

        // Update the recycler view with the symptoms for today's date
        updateRecyclerView()

        return view
    }

    private fun updateRecyclerView() {
        GlobalScope.launch(Dispatchers.IO) {
            val symptoms = dbHelper.getSymptomsForPatient(viewModel.patientId, selectedDate)
            val mood = dbHelper.getMoodForPatient(viewModel.patientId, selectedDate)
            withContext(Dispatchers.Main) {
                adapter.symptoms = symptoms
                adapter.notifyDataSetChanged()
                tvMood.text = "Mood: ${mood ?: ""}"
            }
        }
    }
}
