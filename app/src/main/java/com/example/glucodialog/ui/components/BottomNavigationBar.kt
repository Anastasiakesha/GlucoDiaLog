package com.example.glucodialog.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.glucodialog.MainActivity.Routes
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBarItemDefaults

data class NavItem(
    val route: String,
    val label: String,
    val icon: String
)

@Composable
fun BottomNavigationBar(navController: NavController) {
    val items = listOf(
        NavItem(Routes.DASHBOARD, "Дашборд", "🏠"),
        NavItem(Routes.RECORD_SELECTOR, "Добавить", "➕"),
        NavItem(Routes.RECORD_HISTORY, "История", "📋"),
        NavItem(Routes.PROFILE, "Профиль", "👤")
    )

    val currentRoute by navController.currentBackStackEntryAsState()

    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface
    ) {
        items.forEach { item ->
            val selected = currentRoute?.destination?.route == item.route
            NavigationBarItem(
                selected = selected,
                onClick = { navController.navigate(item.route) { launchSingleTop = true } },
                icon = {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = item.icon,
                            fontSize = 20.sp
                        )
                        Text(
                            text = item.label,
                            fontSize = 10.sp,
                            modifier = Modifier.padding(top = 2.dp)
                        )
                    }
                },
                label = null,
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = MaterialTheme.colorScheme.primary,
                    unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )
        }
    }
}