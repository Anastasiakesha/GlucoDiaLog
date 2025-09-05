package com.example.glucodialog.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.glucodialog.data.GlucoseEntry
import com.example.glucodialog.data.UserProfile
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun GlucoseCard(
    record: GlucoseEntry,
    userProfile: UserProfile?,
    onEdit: (() -> Unit)? = null,
    onDelete: (() -> Unit)? = null
) {
    val label = remember(record.glucoseLevel, userProfile) {
        when {
            userProfile == null -> ""
            record.glucoseLevel < userProfile.targetGlucoseLow -> "Низкое"
            record.glucoseLevel > userProfile.targetGlucoseHigh -> "Высокое"
            else -> "В норме"
        }
    }

    val dateFormat = SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault())

    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text("${record.glucoseLevel} ${record.unit} — $label", color = Color.Black)
        record.note?.let { Text(it, color = Color.Black) }
        Text("Дата: ${dateFormat.format(Date(record.timestamp))}", color = Color.Black)

        Row(
            horizontalArrangement = Arrangement.End,
            modifier = Modifier.fillMaxWidth()
        ) {
            if (onEdit != null) {
                IconButton(onClick = onEdit) {
                    Icon(Icons.Default.Edit, contentDescription = "Редактировать", tint = Color.Black)
                }
            }
            if (onDelete != null) {
                IconButton(onClick = onDelete) {
                    Icon(Icons.Default.Delete, contentDescription = "Удалить", tint = Color.Black)
                }
            }
        }
    }
}