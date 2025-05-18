package com.example.pomoplanner.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.interaction.Interaction
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.pomoplanner.model.DBHelper
import com.example.pomoplanner.model.Profile
import com.example.pomoplanner.ui.MainActivityViewModel.TabBarItem
import com.example.pomoplanner.ui.theme.PomoPlannerTheme
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow

// application entry point
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // log out of any profile on launch
        val dbHelper = DBHelper(this)

        val profile: Profile = dbHelper.getSelectedProfile()
        if (profile.profileId != -1) {
            profile.profileIsSelected = false
            dbHelper.updateProfile(profile)
        }

        setContent {
            PomoPlannerTheme {
                PomoPlannerApp()
            }
        }
    }
}

// app navigation controller
@Composable
private fun PomoPlannerApp(
    mainActivityViewModel: MainActivityViewModel = viewModel(),
) {
    mainActivityViewModel.getCurrentTasks()

    // creating navController
    val navController = rememberNavController()

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Scaffold(
            bottomBar = {
                TabView(
                    mainActivityViewModel.tabBarItems
                ) { location ->
                    navController.navigate(location)
                }
            }
        ) { innerPadding ->
            NavHost(
                modifier = Modifier.padding(innerPadding),
                navController = navController,
                startDestination = mainActivityViewModel.profileTab.title
            ) {
                composable(mainActivityViewModel.profileTab.title) {
                    BackHandler(true) {
                        // disable back navigating
                    }
                    ProfileTab(mainActivityViewModel)
                }
                composable(mainActivityViewModel.tasksTab.title) {
                    BackHandler(true) {
                        // disable back navigating
                    }
                    TasksTab(mainActivityViewModel)
                }
                composable(mainActivityViewModel.pomodoroTab.title) {
                    BackHandler(true) {
                        // disable back navigating
                    }
                    PomodoroTab(mainActivityViewModel)
                }
                composable(mainActivityViewModel.settingsTab.title) {
                    BackHandler(true) {
                        // disable back navigating
                    }
                    SettingsTab(mainActivityViewModel)
                }
            }
        }
    }
}

// generates navigation bar and handles active navigation tab and switching
@Composable
private fun TabView(
    tabBarItems: List<TabBarItem>,
    onClick: (String) -> Unit
) {
    var selectedTabIndex by rememberSaveable {
        mutableIntStateOf(0)
    }

    class NoAnimationInteractionSource : MutableInteractionSource {
        override val interactions: Flow<Interaction> = emptyFlow()
        override suspend fun emit(interaction: Interaction) {}
        override fun tryEmit(interaction: Interaction) = true
    }

    NavigationBar {
        tabBarItems.forEachIndexed { index, tabBarItem ->
            val noAnimationInteractionSource = remember { NoAnimationInteractionSource() }
            val interactionSource = remember { MutableInteractionSource() }

            NavigationBarItem(
                selected = selectedTabIndex == index,
                onClick = {
                    if (!tabBarItem.isDisabled) {
                        selectedTabIndex = index
                        onClick(tabBarItem.title)
                    }
                },
                interactionSource = if (tabBarItem.isDisabled) {
                    noAnimationInteractionSource
                } else {
                    interactionSource
                },
                icon = {
                    TabBarIconView(
                        isSelected = selectedTabIndex == index,
                        selectedIcon = tabBarItem.selectedIcon,
                        unselectedIcon = tabBarItem.unselectedIcon,
                        title = tabBarItem.title,
                        isDisabled = tabBarItem.isDisabled,
                        badgeAmount = tabBarItem.badgeAmount
                    )
                },
                label = {
                    Text(
                        modifier = if (tabBarItem.isDisabled) {
                            Modifier.alpha(0.5f)
                        } else {
                            Modifier.alpha(1f)
                        },
                        text = tabBarItem.title
                    )
                })
        }
    }
}

// generates icons for navigation tab
// selects which icon to use and displays notifications using a badge
@Composable
private fun TabBarIconView(
    isSelected: Boolean,
    selectedIcon: ImageVector,
    unselectedIcon: ImageVector,
    title: String,
    isDisabled: Boolean,
    badgeAmount: Int? = null,
) {
    BadgedBox(badge = { TabBarBadgeView(badgeAmount) }) {
        Icon(
            modifier = if (isDisabled) {
                Modifier.alpha(0.5f)
            } else {
                Modifier.alpha(1f)
            },
            imageVector = if (isSelected) {
                selectedIcon
            } else {
                unselectedIcon
            },
            contentDescription = title
        )
    }
}

// generates badges from int values for use when generating icons
@Composable
private fun TabBarBadgeView(count: Int? = null) {
    if (count != null) {
        Badge {
            Text(count.toString())
        }
    } else {
        Badge(modifier = Modifier.alpha(0f))
    }
}