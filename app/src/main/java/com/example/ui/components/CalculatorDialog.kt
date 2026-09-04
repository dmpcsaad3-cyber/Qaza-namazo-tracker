package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp

@Composable
fun CalculatorDialog(
    onDismiss: () -> Unit,
    onApply: (totalDays: Int, overwrite: Boolean) -> Unit
) {
    var selectedTab by remember { mutableIntStateOf(0) } // 0: By Duration, 1: By Age

    // Duration state
    var yearsText by remember { mutableStateOf("1") }
    var monthsText by remember { mutableStateOf("0") }
    var daysText by remember { mutableStateOf("0") }
    var exemptedDaysText by remember { mutableStateOf("0") }

    // Age state
    var pubertyAgeText by remember { mutableStateOf("15") }
    var regularPrayerAgeText by remember { mutableStateOf("20") }
    var annualExemptDaysText by remember { mutableStateOf("0") }

    // Mode: Overwrite vs Add
    var isOverwrite by remember { mutableStateOf(false) }

    val calculatedDays = if (selectedTab == 0) {
        val y = yearsText.toIntOrNull() ?: 0
        val m = monthsText.toIntOrNull() ?: 0
        val d = daysText.toIntOrNull() ?: 0
        val ex = exemptedDaysText.toIntOrNull() ?: 0
        ((y * 365) + (m * 30) + d - ex).coerceAtLeast(0)
    } else {
        val puberty = pubertyAgeText.toIntOrNull() ?: 15
        val regular = regularPrayerAgeText.toIntOrNull() ?: 20
        val exPerYear = annualExemptDaysText.toIntOrNull() ?: 0
        val yearsMissed = (regular - puberty).coerceAtLeast(0)
        val rawDays = (yearsMissed * 365) - (yearsMissed * exPerYear)
        rawDays.coerceAtLeast(0)
    }

    val totalNamazen = calculatedDays * 6

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Column {
                Text(
                    text = "Qaza-e-Umri Calculator",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "قضا عمری کا حساب لگائیں",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                TabRow(selectedTabIndex = selectedTab) {
                    Tab(
                        selected = selectedTab == 0,
                        onClick = { selectedTab = 0 },
                        text = { Text("By Duration (مدت)") },
                        modifier = Modifier.testTag("tab_duration")
                    )
                    Tab(
                        selected = selectedTab == 1,
                        onClick = { selectedTab = 1 },
                        text = { Text("By Age (عمر)") },
                        modifier = Modifier.testTag("tab_age")
                    )
                }

                if (selectedTab == 0) {
                    Text(
                        text = "Enter the approximate time period you missed prayers:",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = yearsText,
                            onValueChange = { if (it.all { c -> c.isDigit() }) yearsText = it },
                            label = { Text("Years (سال)") },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f).testTag("calc_years_input")
                        )
                        OutlinedTextField(
                            value = monthsText,
                            onValueChange = { if (it.all { c -> c.isDigit() }) monthsText = it },
                            label = { Text("Months (ماہ)") },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f).testTag("calc_months_input")
                        )
                        OutlinedTextField(
                            value = daysText,
                            onValueChange = { if (it.all { c -> c.isDigit() }) daysText = it },
                            label = { Text("Days (دن)") },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f).testTag("calc_days_input")
                        )
                    }

                    OutlinedTextField(
                        value = exemptedDaysText,
                        onValueChange = { if (it.all { c -> c.isDigit() }) exemptedDaysText = it },
                        label = { Text("Exempted Days (بیماری / رخصت کے دن)") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth().testTag("calc_exempt_input")
                    )
                } else {
                    Text(
                        text = "Calculate missed prayers between puberty and when you started praying:",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    OutlinedTextField(
                        value = pubertyAgeText,
                        onValueChange = { if (it.all { c -> c.isDigit() }) pubertyAgeText = it },
                        label = { Text("Puberty Age (بالغ ہونے کی عمر - مثلاً 15)") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth().testTag("calc_puberty_input")
                    )

                    OutlinedTextField(
                        value = regularPrayerAgeText,
                        onValueChange = { if (it.all { c -> c.isDigit() }) regularPrayerAgeText = it },
                        label = { Text("Age when regular (نماز شروع کرنے کی عمر)") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth().testTag("calc_regular_age_input")
                    )

                    OutlinedTextField(
                        value = annualExemptDaysText,
                        onValueChange = { if (it.all { c -> c.isDigit() }) annualExemptDaysText = it },
                        label = { Text("Exempt Days / Year (سالانہ رخصت کے دن)") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth().testTag("calc_annual_exempt_input")
                    )
                }

                // Calculation Preview Box
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = "Calculation Result (حساب کا نتیجہ):",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Text(
                            text = "Total Days: $calculatedDays days",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = "Missed for Each Prayer: $calculatedDays times",
                            style = MaterialTheme.typography.bodySmall
                        )
                        Text(
                            text = "Total Combined Prayers: $totalNamazen namazen (including Witr)",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }

                // Action mode selection
                Text(
                    text = "Apply Method (طریقہ کار):",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .clickable { isOverwrite = false }
                        .padding(horizontal = 4.dp, vertical = 2.dp)
                ) {
                    RadioButton(
                        selected = !isOverwrite,
                        onClick = { isOverwrite = false }
                    )
                    Text(
                        text = "Add to existing count (موجودہ میں شامل کریں)",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .clickable { isOverwrite = true }
                        .padding(horizontal = 4.dp, vertical = 2.dp)
                ) {
                    RadioButton(
                        selected = isOverwrite,
                        onClick = { isOverwrite = true }
                    )
                    Text(
                        text = "Set as new total (نیا ریکارڈ بنائیں)",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onApply(calculatedDays, isOverwrite)
                    onDismiss()
                },
                enabled = calculatedDays > 0,
                modifier = Modifier.testTag("apply_calculator_button")
            ) {
                Text("Apply to Record")
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                modifier = Modifier.testTag("cancel_calculator_button")
            ) {
                Text("Close")
            }
        }
    )
}
