package com.example.glucodialog

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.glucodialog.data.ActivityEntry
import com.example.glucodialog.data.AppDatabase
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.flow.first
import java.text.SimpleDateFormat
import java.util.*

class ActivityEntryAdapter(
    private val entries: List<ActivityEntry>,
    private val db: AppDatabase
) : RecyclerView.Adapter<ActivityEntryAdapter.EntryViewHolder>() {

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

        val activityName = runBlocking {
            db.activityDao().getActivityById(entry.activityTypeId)?.name ?: "Неизвестно"
        }

        val formattedDate = SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault())
            .format(Date(entry.timestamp))

        holder.tvSummary.text = "$activityName – ${entry.durationMinutes} мин. ($formattedDate)"
    }

    override fun getItemCount() = entries.size
}
