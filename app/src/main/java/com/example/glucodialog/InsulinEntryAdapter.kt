package com.example.glucodialog

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.glucodialog.data.AppDatabase
import com.example.glucodialog.data.InsulinEntry
import kotlinx.coroutines.runBlocking
import java.text.SimpleDateFormat
import java.util.*

class InsulinEntryAdapter(
    private val entries: List<InsulinEntry>,
    private val db: AppDatabase
) : RecyclerView.Adapter<InsulinEntryAdapter.EntryViewHolder>() {

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

        val insulinInfo = runBlocking {
            db.insulinDao().getInsulinById(entry.insulinTypeId)?.name ?: "Неизвестно"
        }

        val formattedTime = SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault())
            .format(Date(entry.timestamp))

        holder.tvSummary.text = "$insulinInfo – ${entry.doseUnits} ед. ($formattedTime)"
    }

    override fun getItemCount() = entries.size
}
