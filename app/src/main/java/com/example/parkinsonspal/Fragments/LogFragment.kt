package com.example.parkinsonspal.Fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.parkinsonspal.*
import com.example.parkinsonspal.Model.DataBaseHelper
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.util.*

class LogFragment : Fragment() {

    private lateinit var viewModel: TrackHealthViewModel
    private lateinit var symptomAdapter: SymptomAdapter
    private lateinit var dbHelper: DataBaseHelper
    private lateinit var btnMood: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = ViewModelProvider(requireActivity()).get(TrackHealthViewModel::class.java)
        dbHelper = DataBaseHelper(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_log, container, false)

        val btnAdd = view.findViewById<Button>(R.id.btnAdd)
        btnMood = view.findViewById<Button>(R.id.btnMood)

        btnAdd.setOnClickListener {
            val symptomDialog = SymptomDialog(requireContext(), viewModel.patientId, symptomAdapter)
            symptomDialog.show()
        }

        // Set the current date text view
        val currentDateTextView = view.findViewById<TextView>(R.id.tv_current_date)
        val calendar = Calendar.getInstance()
        val sdf = SimpleDateFormat("dd MMM", Locale.getDefault())
        val currentDate = sdf.format(calendar.time)
        currentDateTextView.text = currentDate

        val recyclerView: RecyclerView = view.findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(context)

        symptomAdapter = SymptomAdapter(emptyList())
        recyclerView.adapter = symptomAdapter

        val currentDateStr = LocalDate.now()
        val symptoms = dbHelper.getSymptomsForPatient(viewModel.patientId, currentDateStr)
        symptomAdapter.symptoms = symptoms
        symptomAdapter.notifyDataSetChanged()

        updateMoodButtonText()

        btnMood.setOnClickListener {
            val moodDialog = MoodDialog(requireContext(), viewModel.patientId, btnMood)
            moodDialog.setOnDismissListener {
                updateMoodButtonText()
            }
            moodDialog.show()
        }

        return view
    }

    private fun updateMoodButtonText() {
        val currentDate = LocalDate.now()
        val mood = dbHelper.getMoodForPatient(viewModel.patientId, currentDate)
        if (mood != null) {
            btnMood.text = mood
        } else {
            btnMood.text = "Change Mood"
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(patientId: Int) =
            LogFragment().apply {
                arguments = Bundle().apply {
                    putInt("patient_id", patientId)
                }
            }
    }
}

