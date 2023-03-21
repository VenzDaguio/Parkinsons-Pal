package com.example.parkinsonspal.Fragments

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import com.example.parkinsonspal.R
import com.example.parkinsonspal.SymptomDialog
import com.example.parkinsonspal.TrackHealthViewModel
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"


class LogFragment : Fragment() {

    private lateinit var viewModel: TrackHealthViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = ViewModelProvider(requireActivity()).get(TrackHealthViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_log, container, false)

        val btnAdd = view.findViewById<Button>(R.id.btnAdd)

        btnAdd.setOnClickListener {
            Log.d("LogFragment", "Patient ID: ${viewModel.patientId}")
            val symptomDialog = SymptomDialog(requireContext(), viewModel.patientId)
            symptomDialog.show()
        }

        // Set the current date text view
        val currentDateTextView = view.findViewById<TextView>(R.id.tv_current_date)
        val calendar = Calendar.getInstance()
        val sdf = SimpleDateFormat("dd MMM", Locale.getDefault())
        val currentDate = sdf.format(calendar.time)
        currentDateTextView.text = currentDate

        return view
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

