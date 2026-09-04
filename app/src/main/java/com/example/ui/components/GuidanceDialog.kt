package com.example.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun GuidanceDialog(onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Column {
                Text(
                    text = "Qaza Namaz Rules & Niyyah",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "قضا نماز کا طریقہ اور نیت",
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
                // Niyyah Card
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = "نیت کا آسان طریقہ (Niyyah)",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Text(
                            text = "دل میں یہ ارادہ کریں کہ: \"میں اپنے ذمہ باقی رہنے والی نمازوں میں سے سب سے پہلی (یا سب سے آخری) فجر/ظہر وغیرہ کی قضا پڑھتا ہوں۔\"",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Text(
                            text = "In heart intend: \"I intend to offer the first (or last) unfulfilled Fajr/Dhuhr etc. due on me for Allah Almighty.\"",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // Rakats summary
                Text(
                    text = "Which prayers have Qaza? (کن نمازوں کی قضا ہے؟)",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Only Farz and Witr prayers require Qaza (Sunnah prayers do not have qaza except Fajr sunnah on the same morning before zawal):\n" +
                            "• Fajr: 2 Farz\n" +
                            "• Dhuhr: 4 Farz\n" +
                            "• Asr: 4 Farz\n" +
                            "• Maghrib: 3 Farz\n" +
                            "• Isha: 4 Farz\n" +
                            "• Witr: 3 Wajib\n" +
                            "Total = 20 Rakats per day.",
                    style = MaterialTheme.typography.bodyMedium
                )

                HorizontalDivider()

                // Forbidden Times
                Text(
                    text = "Times when Qaza CANNOT be prayed (ممنوع اوقات):",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.error
                )
                Text(
                    text = "Qaza namaz can be prayed at any time day or night EXCEPT the 3 forbidden times:\n" +
                            "1. Sunrise (سورج طلوع ہوتے وقت - approx 15-20 mins)\n" +
                            "2. Exact Noon / Zawal (نصف النہار / زوال کا وقت)\n" +
                            "3. Sunset (سورج غروب ہوتے وقت - approx 15-20 mins before Maghrib)",
                    style = MaterialTheme.typography.bodySmall
                )

                HorizontalDivider()

                // Practical tip
                Text(
                    text = "آسان طریقہ (Practical Tip):",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "ہر روز ہر وقتی نماز کے ساتھ ایک ایک قضا نماز پڑھنے کا معمول بنائیں۔ یوں ہر روز ایک دن کی قضا باآسانی ادا ہوتی جائے گی!",
                    style = MaterialTheme.typography.bodySmall
                )
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                modifier = Modifier.testTag("close_guidance_button")
            ) {
                Text("Got It (سمجھ گیا)")
            }
        }
    )
}
