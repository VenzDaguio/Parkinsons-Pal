package com.example.parkinsonspal

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.parkinsonspal.databinding.ItemMedicationBinding

class MedicationAdapter(var medications: List<Medication>) :
    RecyclerView.Adapter<MedicationAdapter.MedicationViewHolder>() {

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
    }

    override fun getItemCount() = medications.size
}
