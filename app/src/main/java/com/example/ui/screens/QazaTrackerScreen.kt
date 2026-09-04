package com.example.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.HelpOutline
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Mosque
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.PrayerRecord
import com.example.ui.components.CalculatorDialog
import com.example.ui.components.DailyTargetDialog
import com.example.ui.components.EditPrayerDialog
import com.example.ui.components.GuidanceDialog
import com.example.ui.components.HistoryDialog
import com.example.ui.components.PrayerCard
import com.example.ui.components.SummaryCard
import com.example.ui.theme.AmberAccent
import com.example.ui.theme.AmberAccentLight
import com.example.ui.theme.EmeraldDarkPrimary
import com.example.ui.viewmodel.PrayerViewModel
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QazaTrackerScreen(
    viewModel: PrayerViewModel,
    modifier: Modifier = Modifier
) {
    val prayers by viewModel.prayers.collectAsState()
    val summary by viewModel.summary.collectAsState()
    val history by viewModel.history.collectAsState()
    val dailyGoal by viewModel.dailyGoal.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }

    // Dialog states
    var editingPrayer by remember { mutableStateOf<PrayerRecord?>(null) }
    var showCalculator by remember { mutableStateOf(false) }
    var showHistory by remember { mutableStateOf(false) }
    var showGuidance by remember { mutableStateOf(false) }
    var showSettings by remember { mutableStateOf(false) }

    LaunchedEffect(viewModel) {
        viewModel.userMessage.collectLatest { message ->
            snackbarHostState.showSnackbar(message)
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Mosque,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(24.dp)
                        )
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "Qaza Namaz Tracker",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "قضا نمازوں کا ریکارڈ",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                },
                actions = {
                    IconButton(
                        onClick = { showCalculator = true },
                        modifier = Modifier.testTag("top_app_bar_calc_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Calculate,
                            contentDescription = "Calculator",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    IconButton(
                        onClick = { showHistory = true },
                        modifier = Modifier.testTag("top_app_bar_history_button")
                    ) {
                        BadgedBox(badge = {
                            if (history.isNotEmpty()) {
                                Badge(containerColor = AmberAccent) {
                                    Text(
                                        text = "${history.size.coerceAtMost(99)}",
                                        fontSize = 10.sp
                                    )
                                }
                            }
                        }) {
                            Icon(
                                imageVector = Icons.Default.History,
                                contentDescription = "History Log",
                                tint = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }

                    IconButton(
                        onClick = { showGuidance = true },
                        modifier = Modifier.testTag("top_app_bar_guidance_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.HelpOutline,
                            contentDescription = "Guidance & Niyyah",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    IconButton(
                        onClick = { showSettings = true },
                        modifier = Modifier.testTag("top_app_bar_settings_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Settings",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        contentWindowInsets = WindowInsets(0, 0, 0, 0)
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .testTag("prayer_list"),
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 40.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Summary Card
            item(key = "summary_card_item") {
                SummaryCard(
                    summary = summary,
                    onMarkOneDayPrayed = { viewModel.markFullDayOffered() },
                    onAddOneDayMissed = { viewModel.addFullDayMissed() },
                    onOpenCalculator = { showCalculator = true }
                )
            }

            // Section Title
            item(key = "section_header") {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp, bottom = 2.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Daily Prayers (یومیہ نمازیں)",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "5 Farz prayers + Witr Wajib",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Text(
                        text = "Total 20 Rakats/Day",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            // The 6 Prayers List
            items(prayers, key = { it.prayerId }) { prayer ->
                PrayerCard(
                    prayer = prayer,
                    onMarkOffered = { count -> viewModel.markOffered(prayer, count) },
                    onAddMissed = { count -> viewModel.addMissed(prayer, count) },
                    onEditClicked = { editingPrayer = prayer }
                )
            }

            // Spiritual Encouragement Card
            item(key = "spiritual_footer") {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = "إِنَّ الصَّلَاةَ كَانَتْ عَلَى الْمُؤْمِنِينَ كِتَابًا مَّوْقُوتًا",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                            textAlign = TextAlign.Center
                        )
                        Text(
                            text = "\"Beshak Namaz momino par apne muqarrara auqaat m farz hai.\"",
                            style = MaterialTheme.typography.bodySmall,
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "Nabi Kareem (ﷺ) ne farmaya: \"Jo shakhs namaz bhool jaye ya so jaye to uska kaffara yahi hai k jab yaad aaye to usay ada kare.\"",
                            style = MaterialTheme.typography.labelSmall,
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
                        )
                    }
                }
            }
        }
    }

    // Edit Specific Prayer Dialog
    editingPrayer?.let { prayer ->
        EditPrayerDialog(
            prayer = prayer,
            onDismiss = { editingPrayer = null },
            onSave = { remaining, completed ->
                viewModel.updatePrayerCounts(prayer, remaining, completed)
            }
        )
    }

    // Calculator Dialog
    if (showCalculator) {
        CalculatorDialog(
            onDismiss = { showCalculator = false },
            onApply = { totalDays, overwrite ->
                viewModel.calculateAndApplyQaza(
                    years = 0,
                    months = 0,
                    days = totalDays,
                    overwrite = overwrite
                )
            }
        )
    }

    // History Dialog
    if (showHistory) {
        HistoryDialog(
            historyList = history,
            onDismiss = { showHistory = false },
            onDeleteItem = { log -> viewModel.deleteHistoryItem(log) },
            onClearAll = { viewModel.clearAllHistory() }
        )
    }

    // Guidance Dialog
    if (showGuidance) {
        GuidanceDialog(onDismiss = { showGuidance = false })
    }

    // Settings / Daily Target Dialog
    if (showSettings) {
        DailyTargetDialog(
            currentGoal = dailyGoal,
            totalRemaining = summary.totalRemaining,
            onDismiss = { showSettings = false },
            onSaveGoal = { newGoal -> viewModel.setDailyGoal(newGoal) },
            onResetAll = { viewModel.resetAll() },
            onClearHistory = { viewModel.clearAllHistory() }
        )
    }
}
