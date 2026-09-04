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
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
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

@Composable
fun DailyTargetDialog(
    currentGoal: Int,
    totalRemaining: Int,
    onDismiss: () -> Unit,
    onSaveGoal: (Int) -> Unit,
    onResetAll: () -> Unit,
    onClearHistory: () -> Unit
) {
    var goalText by remember { mutableStateOf(currentGoal.toString()) }
    var showConfirmReset by remember { mutableStateOf(false) }

    val goalInt = goalText.toIntOrNull() ?: currentGoal
    val estimatedDays = if (goalInt > 0 && totalRemaining > 0) {
        (totalRemaining + goalInt - 1) / goalInt
    } else 0

    val estMonths = estimatedDays / 30
    val estYears = estimatedDays / 365
    val remainderDays = estimatedDays % 30

    if (showConfirmReset) {
        AlertDialog(
            onDismissRequest = { showConfirmReset = false },
            title = { Text("Confirm Reset") },
            text = { Text("Are you sure you want to reset all Qaza counts to 0? This cannot be undone.") },
            confirmButton = {
                Button(
                    onClick = {
                        onResetAll()
                        showConfirmReset = false
                        onDismiss()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Yes, Reset All")
                }
            },
            dismissButton = {
                TextButton(onClick = { showConfirmReset = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Column {
                Text(
                    text = "Daily Target & Settings",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "روزانہ کا ہدف اور ترتیبات",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Text(
                    text = "Set how many Qaza prayers you aim to offer each day:",
                    style = MaterialTheme.typography.bodyMedium
                )

                OutlinedTextField(
                    value = goalText,
                    onValueChange = { if (it.all { c -> c.isDigit() }) goalText = it },
                    label = { Text("Daily Qaza Goal (روزانہ کا ہدف)") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth().testTag("daily_goal_input")
                )

                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f)
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = "Estimated Completion Time:",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                        if (totalRemaining > 0 && goalInt > 0) {
                            Text(
                                text = "At $goalInt prayers/day, you will finish in approx $estimatedDays days" +
                                        if (estYears > 0) " (~$estYears years, $estMonths months)"
                                        else if (estMonths > 0) " (~$estMonths months, $remainderDays days)"
                                        else "",
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.SemiBold
                            )
                        } else {
                            Text(
                                text = "No remaining prayers or target is 0.",
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                }

                HorizontalDivider()

                Text(
                    text = "Data Management (ڈیٹا)",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    TextButton(
                        onClick = onClearHistory,
                        modifier = Modifier.testTag("clear_history_button")
                    ) {
                        Text("Clear Activity Log")
                    }

                    TextButton(
                        onClick = { showConfirmReset = true },
                        colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error),
                        modifier = Modifier.testTag("reset_all_button")
                    ) {
                        Text("Reset All Records")
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onSaveGoal(goalInt)
                    onDismiss()
                },
                modifier = Modifier.testTag("save_daily_goal_button")
            ) {
                Text("Save Target")
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                modifier = Modifier.testTag("cancel_settings_button")
            ) {
                Text("Cancel")
            }
        }
    )
}
