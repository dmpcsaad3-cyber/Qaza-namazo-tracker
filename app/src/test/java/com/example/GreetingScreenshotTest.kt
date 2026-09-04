package com.example

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onRoot
import com.example.ui.components.SummaryCard
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.DashboardSummary
import com.github.takahirom.roborazzi.RobolectricDeviceQualifiers
import com.github.takahirom.roborazzi.captureRoboImage
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(qualifiers = RobolectricDeviceQualifiers.Pixel8, sdk = [36])
class GreetingScreenshotTest {

  @get:Rule val composeTestRule = createComposeRule()

  @Test
  fun greeting_screenshot() {
    composeTestRule.setContent {
      MyApplicationTheme {
        SummaryCard(
          summary = DashboardSummary(
            totalRemaining = 120,
            totalCompleted = 45,
            totalCombined = 165,
            progressPercentage = 0.27f,
            todayOfferedCount = 3,
            dailyGoal = 6,
            estimatedDaysRemaining = 20
          ),
          onMarkOneDayPrayed = {},
          onAddOneDayMissed = {},
          onOpenCalculator = {}
        )
      }
    }

    composeTestRule.onRoot().captureRoboImage(filePath = "src/test/screenshots/greeting.png")
  }
}
