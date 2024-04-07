package io.github.warleysr.ankipadroid.navigation

import android.content.Context
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import io.github.warleysr.ankipadroid.MainActivity
import io.github.warleysr.ankipadroid.R

data class NavItem(
    val label: Int,
    val icon: ImageVector,
    val route: String
)

val listOfNavItems : List<NavItem> = listOf(
    NavItem(
        label = R.string.home,
        icon = Icons.Default.Home,
        route = Screens.HomeScreen.name
    ),
    NavItem(
        label = R.string.flashcards,
        icon = Icons.Default.Menu,
        route = Screens.FlashcardsScreen.name
    ),
    NavItem(
        label = R.string.vocabulary,
        icon = Icons.Default.AddCircle,
        route = Screens.VocabularyScreen.name
    ),
    NavItem(
        label = R.string.settings,
        icon = Icons.Default.Settings,
        route = Screens.SettingsScreen.name
    ),
)