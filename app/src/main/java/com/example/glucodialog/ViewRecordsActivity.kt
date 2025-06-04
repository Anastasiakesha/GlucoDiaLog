package com.example.glucodialog


import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.glucodialog.data.AppDatabase
import com.example.glucodialog.data.UnifiedEntry
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ViewRecordsActivity : AppCompatActivity() {
    private val scope = MainScope()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_records)

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerViewRecords)
        recyclerView.layoutManager = LinearLayoutManager(this)

        val db = AppDatabase.getDatabase(this)

        scope.launch {
            val unifiedList = mutableListOf<UnifiedEntry>()

            unifiedList.addAll(
                db.glucoseDao().getAllGlucoseEntriesOnce().map {
                    UnifiedEntry.Glucose(it)
                }
            )

            unifiedList.addAll(
                db.foodDao().getAllFoodEntriesOnceWithItems().map {
                    UnifiedEntry.Food(it.entry, it.foodItem.name)
                }
            )

            unifiedList.addAll(
                db.insulinDao().getAllInsulinEntriesOnceWithTypes().map {
                    UnifiedEntry.Insulin(it.entry, it.type.name)
                }
            )

            unifiedList.addAll(
                db.activityDao().getAllActivityEntriesOnceWithTypes().map {
                    UnifiedEntry.Activity(it.entry, it.type.name)
                }
            )

            unifiedList.addAll(
                db.medicationDao().getAllMedicationEntriesOnceWithTypes().map {
                    UnifiedEntry.Medication(it.entry, it.type.name)
                }
            )


            val grouped = unifiedList
                .sortedByDescending { it.getTimestamp() }
                .groupBy { timestampToDate(it.getTimestamp()) }
                .flatMap { (date, entries) ->
                    listOf(UnifiedEntry.DateHeader(date)) + entries
                }

            recyclerView.adapter = UnifiedEntryAdapter(grouped)
        }
    }

    private fun timestampToDate(timestamp: Long): String {
        val sdf = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
        return sdf.format(Date(timestamp))
    }
}
