package gui.charts.app

import androidx.compose.animation.Crossfade
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import gui.charts.app.screen.HomeScreen
import gui.charts.app.screen.bar.BarChartScreen
import gui.charts.app.screen.line.LineChartScreen
import gui.charts.app.screen.pie.PieChartScreen
import gui.charts.app.theme.ComposeChartsTheme

/**
 * @Author bytebeats
 * @Email <happychinapc@gmail.com>
 * @Github https://github.com/bytebeats
 * @Created at 2022/3/10 21:21
 * @Version 1.0
 * @Description TO-DO
 */

@Composable
fun ComposeCharts() {
    ComposeChartsTheme {
        ComposeChartsContent()
    }
}

@Composable
private fun ComposeChartsContent() {
    Crossfade(targetState = ScreenRouter.currentScreen) { screen ->
        Surface(color = MaterialTheme.colors.background) {
            when (screen) {
                Screen.Pie -> PieChartScreen()
                Screen.Line -> LineChartScreen()
                Screen.Bar -> BarChartScreen()
                else -> HomeScreen()
            }
        }
    }
}