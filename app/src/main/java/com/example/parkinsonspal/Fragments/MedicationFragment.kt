package com.example.parkinsonspal.Fragments

import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.parkinsonspal.*
import com.example.parkinsonspal.Adapter.MedicationAdapter
import com.example.parkinsonspal.Dialog.MedicationDialog
import com.example.parkinsonspal.Model.DataBaseHelper
import com.example.parkinsonspal.Receiver.ResetTakenValueReceiver

class MedicationFragment() : Fragment() {

    private lateinit var dbHelper: DataBaseHelper
    private lateinit var adapter: MedicationAdapter
    private lateinit var viewModel: TrackHealthViewModel
    private lateinit var resetTakenValueReceiver: ResetTakenValueReceiver

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = ViewModelProvider(requireActivity()).get(TrackHealthViewModel::class.java)
        dbHelper = DataBaseHelper(requireContext())
    }

    override fun onStart() {
        super.onStart()
        resetTakenValueReceiver = ResetTakenValueReceiver()
        val filter = IntentFilter(Intent.ACTION_DATE_CHANGED)
        requireActivity().registerReceiver(resetTakenValueReceiver, filter)
    }

    override fun onStop() {
        super.onStop()
        requireActivity().unregisterReceiver(resetTakenValueReceiver)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_medication, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        dbHelper = DataBaseHelper(requireContext())

        // Set up RecyclerView
        adapter = MedicationAdapter(requireContext(), viewModel.patientId, dbHelper.getMedicationForPatient(viewModel.patientId))
        val recyclerView = view.findViewById<RecyclerView>(R.id.medications_recyclerview)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        // Set up Add Medication button
        val addButton = view.findViewById<Button>(R.id.add_medication_button)
        addButton.setOnClickListener {
            showAddMedicationDialog()
        }

    }

    private fun showAddMedicationDialog() {
        val dialog = MedicationDialog(requireContext(), viewModel.patientId, adapter)
        dialog.show()
    }

}



