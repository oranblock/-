package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Style
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.screens.DailyMysteryScreen
import com.example.ui.screens.DeckScreen
import com.example.ui.screens.DirectoryScreen
import com.example.ui.screens.PartyRushScreen
import com.example.ui.screens.StatsScreen
import com.example.ui.theme.CinemaBg
import com.example.ui.theme.CinemaSurface
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonIndigo
import com.example.ui.theme.PhonoFlicksTheme
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.viewmodel.PhonoFlicksViewModel
import com.example.viewmodel.ScreenTab

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PhonoFlicksTheme {
                val viewModel: PhonoFlicksViewModel = viewModel()
                PhonoFlicksApp(viewModel = viewModel)
            }
        }
    }
}

data class NavigationTabItem(
    val tab: ScreenTab,
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val testTag: String
)

@Composable
fun PhonoFlicksApp(viewModel: PhonoFlicksViewModel) {
    val currentTab by viewModel.currentTab.collectAsState()

    // Android predictive/system back handling: Return to Deck from any secondary screen
    if (currentTab != ScreenTab.DECK) {
        BackHandler {
            viewModel.selectTab(ScreenTab.DECK)
        }
    }

    val navItems = listOf(
        NavigationTabItem(
            tab = ScreenTab.DECK,
            title = "Deck",
            selectedIcon = Icons.Default.Style,
            unselectedIcon = Icons.Default.Style,
            testTag = "nav_tab_deck"
        ),
        NavigationTabItem(
            tab = ScreenTab.PARTY_RUSH,
            title = "Party",
            selectedIcon = Icons.Default.Groups,
            unselectedIcon = Icons.Default.Groups,
            testTag = "nav_tab_party"
        ),
        NavigationTabItem(
            tab = ScreenTab.DIRECTORY,
            title = "Directory",
            selectedIcon = Icons.Default.List,
            unselectedIcon = Icons.Default.List,
            testTag = "nav_tab_directory"
        ),
        NavigationTabItem(
            tab = ScreenTab.DAILY,
            title = "Daily",
            selectedIcon = Icons.Default.CalendarMonth,
            unselectedIcon = Icons.Default.CalendarMonth,
            testTag = "nav_tab_daily"
        ),
        NavigationTabItem(
            tab = ScreenTab.STATS,
            title = "Stats",
            selectedIcon = Icons.Default.EmojiEvents,
            unselectedIcon = Icons.Default.EmojiEvents,
            testTag = "nav_tab_stats"
        )
    )

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .background(CinemaBg),
        contentWindowInsets = WindowInsets.safeDrawing,
        bottomBar = {
            NavigationBar(
                containerColor = CinemaSurface,
                modifier = Modifier
                    .windowInsetsPadding(WindowInsets.navigationBars)
                    .testTag("main_bottom_nav_bar")
            ) {
                navItems.forEach { item ->
                    val isSelected = currentTab == item.tab
                    NavigationBarItem(
                        selected = isSelected,
                        onClick = { viewModel.selectTab(item.tab) },
                        icon = {
                            Icon(
                                imageVector = if (isSelected) item.selectedIcon else item.unselectedIcon,
                                contentDescription = item.title,
                                modifier = Modifier.size(22.dp)
                            )
                        },
                        label = {
                            Text(
                                text = item.title,
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color.White,
                            selectedTextColor = NeonCyan,
                            unselectedIconColor = TextMuted,
                            unselectedTextColor = TextMuted,
                            indicatorColor = NeonIndigo
                        ),
                        modifier = Modifier.testTag(item.testTag)
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (currentTab) {
                ScreenTab.DECK -> DeckScreen(viewModel = viewModel)
                ScreenTab.PARTY_RUSH -> PartyRushScreen(viewModel = viewModel)
                ScreenTab.DIRECTORY -> DirectoryScreen(viewModel = viewModel)
                ScreenTab.DAILY -> DailyMysteryScreen(viewModel = viewModel)
                ScreenTab.STATS -> StatsScreen(viewModel = viewModel)
            }
        }
    }
}
