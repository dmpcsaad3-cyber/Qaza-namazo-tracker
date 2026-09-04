package com.example.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.data.model.PrayerRecord

@Composable
fun EditPrayerDialog(
    prayer: PrayerRecord,
    onDismiss: () -> Unit,
    onSave: (remaining: Int, completed: Int) -> Unit
) {
    var remainingText by remember { mutableStateOf(prayer.remainingCount.toString()) }
    var completedText by remember { mutableStateOf(prayer.completedCount.toString()) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Column {
                Text(
                    text = "${prayer.englishName} (${prayer.urduName})",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Adjust Qaza counts manually",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                OutlinedTextField(
                    value = remainingText,
                    onValueChange = { if (it.all { char -> char.isDigit() }) remainingText = it },
                    label = { Text("Remaining Qaza (باقی قضا)") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("edit_remaining_input")
                )

                OutlinedTextField(
                    value = completedText,
                    onValueChange = { if (it.all { char -> char.isDigit() }) completedText = it },
                    label = { Text("Already Offered (ادا شدہ)") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("edit_completed_input")
                )

                Text(
                    text = "Total Farz/Wajib Rakats: ${prayer.rakats} ${prayer.rakatType}",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val rem = remainingText.toIntOrNull() ?: prayer.remainingCount
                    val comp = completedText.toIntOrNull() ?: prayer.completedCount
                    onSave(rem, comp)
                    onDismiss()
                },
                modifier = Modifier.testTag("save_prayer_button")
            ) {
                Text("Save Changes")
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                modifier = Modifier.testTag("cancel_edit_button")
            ) {
                Text("Cancel")
            }
        }
    )
}
