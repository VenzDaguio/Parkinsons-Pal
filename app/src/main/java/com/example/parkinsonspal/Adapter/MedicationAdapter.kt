package com.example.parkinsonspal.Adapter

import android.content.ContentValues.TAG
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.parkinsonspal.Dialog.EditMedicationDialog
import com.example.parkinsonspal.Medication
import com.example.parkinsonspal.Model.DataBaseHelper
import com.example.parkinsonspal.R
import java.text.SimpleDateFormat
import java.util.*

class MedicationAdapter(private val context: Context, private val patientId: Int, var medications: List<Medication>) :
    RecyclerView.Adapter<MedicationAdapter.MedicationViewHolder>() {

    private val dbHelper: DataBaseHelper = DataBaseHelper(context)

    class MedicationViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nameTextView: TextView = itemView.findViewById(R.id.nameTextView)
        val timeTextView: TextView = itemView.findViewById(R.id.timeTextView)
        val quantityTextView: TextView = itemView.findViewById(R.id.quantityTextView)
        val takenCheckBox: CheckBox = itemView.findViewById(R.id.takenCheckBox)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MedicationViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_medication, parent, false)
        return MedicationViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MedicationViewHolder, position: Int) {
        val currentMedication = medications[position]
        holder.nameTextView.text = currentMedication.name
        holder.timeTextView.text = currentMedication.time.toString()
        holder.quantityTextView.text = currentMedication.quantity.toString()
        holder.takenCheckBox.isChecked = currentMedication.taken == 1
        holder.takenCheckBox.setOnCheckedChangeListener { buttonView, isChecked ->
            val medicationName = currentMedication.name
            val takenValue = if (isChecked) 1 else 0
            val takenDate = if (isChecked) {
                SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
            } else {
                null
            }
            dbHelper.updateMedicationTaken(patientId, medicationName, takenValue, takenDate)

            Log.d(TAG, "updateMedicationTaken() called")
            Log.d(TAG, "medicationName: $medicationName, takenValue: $takenValue")

            currentMedication.taken = takenValue
            currentMedication.takenDate = takenDate

            notifyDataSetChanged()
        }

        holder.itemView.setOnClickListener {
            val editMedicationDialog = EditMedicationDialog(
                context,
                patientId,
                currentMedication.id,
                this
            )
            editMedicationDialog.mName = currentMedication.name
            editMedicationDialog.mTime = currentMedication.time
            editMedicationDialog.mQuantity = currentMedication.quantity
            editMedicationDialog.show()
        }
    }

    override fun getItemCount() = medications.size

}

