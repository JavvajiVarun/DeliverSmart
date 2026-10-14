package app.deliversmart.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import app.deliversmart.ui.theme.*

enum class AppTab { HOME, HISTORY, SETTINGS }

@Composable
fun BottomNav(current: AppTab, onHome: () -> Unit, onHistory: () -> Unit, onSettings: () -> Unit) {
    NavigationBar(containerColor = Background, tonalElevation = 0.dp) {
        NavigationBarItem(
            selected = current == AppTab.HOME,
            onClick = onHome,
            icon = { Icon(Icons.Outlined.Home, contentDescription = "Home") },
            label = { Text("Home", fontSize = 9.sp) },
            colors = navColors()
        )
        NavigationBarItem(
            selected = current == AppTab.HISTORY,
            onClick = onHistory,
            icon = { Icon(Icons.Outlined.History, contentDescription = "History") },
            label = { Text("History", fontSize = 9.sp) },
            colors = navColors()
        )
        NavigationBarItem(
            selected = current == AppTab.SETTINGS,
            onClick = onSettings,
            icon = { Icon(Icons.Outlined.Settings, contentDescription = "Settings") },
            label = { Text("Settings", fontSize = 9.sp) },
            colors = navColors()
        )
    }
}

@Composable
fun navColors() = NavigationBarItemDefaults.colors(
    selectedIconColor = Teal,
    selectedTextColor = Teal,
    unselectedIconColor = Color(0xFF3A3A3C),
    unselectedTextColor = Color(0xFF3A3A3C),
    indicatorColor = Color.Transparent
)
