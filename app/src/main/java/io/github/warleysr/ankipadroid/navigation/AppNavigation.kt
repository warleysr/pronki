package io.github.warleysr.ankipadroid.navigation

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
import io.github.warleysr.ankipadroid.screens.FlashcardsScreen
import io.github.warleysr.ankipadroid.screens.HomeScreen
import io.github.warleysr.ankipadroid.screens.SettingsScreen
import io.github.warleysr.ankipadroid.screens.VocabularyScreen
import io.github.warleysr.ankipadroid.viewmodels.SettingsViewModel

@Composable
fun AppNavigation(settingsViewModel: SettingsViewModel) {

    val navController : NavHostController = rememberNavController()

    Scaffold(
        bottomBar = {
            NavigationBar {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination: NavDestination? = navBackStackEntry?.destination

                listOfNavItems.forEach { navItem: NavItem ->
                    NavigationBarItem(
                        selected = currentDestination?.hierarchy?.any { it.route == navItem.route } == true,
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
                                   imageVector = navItem.icon,
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
            startDestination = Screens.HomeScreen.name,
            modifier = Modifier.padding(paddingValues)
        )  {
            composable(route = Screens.HomeScreen.name) {
                HomeScreen()
            }
            composable(route = Screens.FlashcardsScreen.name) {
                FlashcardsScreen()
            }
            composable(route = Screens.VocabularyScreen.name) {
                VocabularyScreen()
            }
            composable(route = Screens.SettingsScreen.name) {
                SettingsScreen(settingsViewModel)
            }
        }
    }
}