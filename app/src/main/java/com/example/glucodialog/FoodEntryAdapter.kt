package com.example.glucodialog

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.glucodialog.data.AppDatabase
import com.example.glucodialog.data.FoodEntry
import kotlinx.coroutines.runBlocking

class FoodEntryAdapter(
    private val entries: List<FoodEntry>,
    private val db: AppDatabase
) : RecyclerView.Adapter<FoodEntryAdapter.EntryViewHolder>() {

    inner class EntryViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvSummary: TextView = view.findViewById(R.id.tvEntrySummary)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EntryViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_entry, parent, false)
        return EntryViewHolder(view)
    }

    override fun onBindViewHolder(holder: EntryViewHolder, position: Int) {
        val entry = entries[position]
        val foodName = runBlocking {
            db.foodDao().getFoodItemById(entry.foodItemId)?.name ?: "Неизвестно"
        }
        holder.tvSummary.text = "$foodName – ${entry.quantity} ${entry.unit} (${entry.timestamp})"
    }

    override fun getItemCount() = entries.size
}
