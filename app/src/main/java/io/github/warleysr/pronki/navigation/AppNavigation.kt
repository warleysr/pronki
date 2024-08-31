package io.github.warleysr.pronki.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import io.github.warleysr.pronki.screens.flashcards.FlashcardsScreen
import io.github.warleysr.pronki.screens.settings.SettingsScreenRoot
import io.github.warleysr.pronki.screens.vocabulary.VocabularyScreen
import io.github.warleysr.pronki.viewmodels.AnkiDroidViewModel
import io.github.warleysr.pronki.viewmodels.PronunciationViewModel
import io.github.warleysr.pronki.viewmodels.SettingsViewModel
import io.github.warleysr.pronki.viewmodels.VocabularyViewModel

@Composable
fun AppNavigation(
    settingsViewModel: SettingsViewModel,
    pronunciationViewModel: PronunciationViewModel,
    ankiDroidViewModel: AnkiDroidViewModel,
    vocabularyViewModel: VocabularyViewModel
) {

    val navController : NavHostController = rememberNavController()

    Scaffold(
        bottomBar = {
            NavigationBar {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination: NavDestination? = navBackStackEntry?.destination

                listOfNavItems.forEach { navItem: NavItem ->
                    val selected = currentDestination?.hierarchy?.any { it.route == navItem.route } == true
                    NavigationBarItem(
                        selected = selected,
                        onClick = {
                                  navController.navigate(navItem.route) {
                                      popUpTo(navController.graph.findStartDestination().id) {
                                          saveState = true
                                      }
                                      launchSingleTop = true
                                      restoreState = true
                                  }
                        },
                        icon = {
                               Icon(
                                   imageVector =
                                       if (selected) navItem.selectedIcon else navItem.unselectedIcon,
                                   contentDescription = null
                               )
                        },
                        label = {
                            Text(text = stringResource(id = navItem.label))
                        }
                    )
                }
            }
        }
    ) {
        paddingValues -> NavHost(
            navController = navController,
            startDestination = Screens.FlashcardsScreen.name,
            modifier = Modifier.padding(paddingValues)
        )  {
            composable(route = Screens.FlashcardsScreen.name) {
                FlashcardsScreen(settingsViewModel, pronunciationViewModel, ankiDroidViewModel)
            }
            composable(route = Screens.VocabularyScreen.name) {
                VocabularyScreen(settingsViewModel, vocabularyViewModel, ankiDroidViewModel)
            }
            composable(route = Screens.SettingsScreen.name) {
                SettingsScreenRoot(settingsViewModel, vocabularyViewModel)
            }
        }
    }
}