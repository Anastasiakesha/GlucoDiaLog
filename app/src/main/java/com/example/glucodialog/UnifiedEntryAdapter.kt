package com.example.glucodialog

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.glucodialog.data.*
import java.text.SimpleDateFormat
import java.util.*
import com.example.glucodialog.data.UnifiedEntry

class UnifiedEntryAdapter(
    private val entries: List<UnifiedEntry>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val TYPE_DATE_HEADER = 0
        private const val TYPE_GLUCOSE = 1
        private const val TYPE_FOOD = 2
        private const val TYPE_INSULIN = 3
        private const val TYPE_ACTIVITY = 4
        private const val TYPE_MEDICATION = 5
    }

    override fun getItemViewType(position: Int): Int = when (entries[position]) {
        is UnifiedEntry.DateHeader -> TYPE_DATE_HEADER
        is UnifiedEntry.Glucose -> TYPE_GLUCOSE
        is UnifiedEntry.Food -> TYPE_FOOD
        is UnifiedEntry.Insulin -> TYPE_INSULIN
        is UnifiedEntry.Activity -> TYPE_ACTIVITY
        is UnifiedEntry.Medication -> TYPE_MEDICATION
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)

        return when (viewType) {
            TYPE_DATE_HEADER -> {
                val view = inflater.inflate(R.layout.item_date_header, parent, false)
                DateHeaderViewHolder(view)
            }
            TYPE_GLUCOSE -> {
                val view = inflater.inflate(R.layout.item_glucose, parent, false)
                GlucoseViewHolder(view)
            }
            TYPE_FOOD -> {
                val view = inflater.inflate(R.layout.item_food, parent, false)
                FoodViewHolder(view)
            }
            TYPE_INSULIN -> {
                val view = inflater.inflate(R.layout.item_insulin, parent, false)
                InsulinViewHolder(view)
            }
            TYPE_ACTIVITY -> {
                val view = inflater.inflate(R.layout.item_activity, parent, false)
                ActivityViewHolder(view)
            }
            TYPE_MEDICATION -> {
                val view = inflater.inflate(R.layout.item_medication, parent, false)
                MedicationViewHolder(view)
            }
            else -> throw IllegalArgumentException("Unknown view type")
        }
    }

    override fun getItemCount(): Int = entries.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val entry = entries[position]

        when (holder) {
            is DateHeaderViewHolder -> holder.bind((entry as UnifiedEntry.DateHeader).date)
            is GlucoseViewHolder -> {
                val e = (entry as UnifiedEntry.Glucose).entry
                holder.bind("Глюкоза: ${e.glucoseLevel} ${e.unit} в ${formatTime(e.timestamp)}")
            }
            is FoodViewHolder -> {
                val e = (entry as UnifiedEntry.Food)
                holder.bind("Еда: ${e.itemName} Количество: ${e.entry.quantity} ${e.entry.unit} в ${formatTime(e.entry.timestamp)}")
            }
            is InsulinViewHolder -> {
                val e = (entry as UnifiedEntry.Insulin)
                holder.bind("Инсулин: ${e.typeName} Доза: ${e.entry.doseUnits} Ед в ${formatTime(e.entry.timestamp)}")
            }
            is ActivityViewHolder -> {
                val e = (entry as UnifiedEntry.Activity)
                holder.bind("Активность: ${e.activityName} Длительность: ${e.entry.durationMinutes} мин в ${formatTime(e.entry.timestamp)}")
            }
            is MedicationViewHolder -> {
                val e = (entry as UnifiedEntry.Medication)
                holder.bind("Лекарство: ${e.medicationName} Доза: ${e.entry.dose} ${e.entry.unit} в ${formatTime(e.entry.timestamp)}")
            }
        }
    }

    private fun formatTime(timestamp: Long): String {
        val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
        return sdf.format(Date(timestamp))
    }

    // ViewHolders

    class DateHeaderViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val text = view.findViewById<TextView>(R.id.textViewDateHeader)
        fun bind(date: String) {
            text.text = date
        }
    }

    class GlucoseViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val text = view.findViewById<TextView>(R.id.textViewGlucose)
        fun bind(data: String) {
            text.text = data
        }
    }

    class FoodViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val text = view.findViewById<TextView>(R.id.textViewFood)
        fun bind(data: String) {
            text.text = data
        }
    }

    class InsulinViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val text = view.findViewById<TextView>(R.id.textViewInsulin)
        fun bind(data: String) {
            text.text = data
        }
    }

    class ActivityViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val text = view.findViewById<TextView>(R.id.textViewActivity)
        fun bind(data: String) {
            text.text = data
        }
    }

    class MedicationViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val text = view.findViewById<TextView>(R.id.textViewMedication)
        fun bind(data: String) {
            text.text = data
        }
    }
}
