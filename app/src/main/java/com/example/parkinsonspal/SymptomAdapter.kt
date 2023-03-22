package com.example.parkinsonspal

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.parkinsonspal.Model.Symptom

class SymptomAdapter(var symptoms: List<Symptom>) :
    RecyclerView.Adapter<SymptomAdapter.SymptomViewHolder>() {

    class SymptomViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val symptomTextView: TextView = itemView.findViewById(R.id.tv_symptom)
        val startTimeTextView: TextView = itemView.findViewById(R.id.tv_startTime)
        val endTimeTextView: TextView = itemView.findViewById(R.id.tv_endTime)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SymptomViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.symptom_item, parent, false)
        return SymptomViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: SymptomViewHolder, position: Int) {
        val currentSymptom = symptoms[position]
        holder.symptomTextView.text = currentSymptom.description
        holder.startTimeTextView.text = currentSymptom.startTime
        holder.endTimeTextView.text = currentSymptom.endTime
    }

    override fun getItemCount() = symptoms.size
}
