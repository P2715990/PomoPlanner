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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.automirrored.outlined.List
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Settings
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.pomoplanner.model.DBHelper
import com.example.pomoplanner.model.Profile
import com.example.pomoplanner.ui.theme.PomoPlannerTheme
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow

class TabBarItem(
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    initialIsDisabled: Boolean = false,
    initialBadgeAmount: Int? = null,
) {
    var isDisabled by mutableStateOf(initialIsDisabled)
    var badgeAmount by mutableStateOf(initialBadgeAmount)
}

// setup of individual navigation tabs
val profileTab = TabBarItem(
    title = "Profile",
    selectedIcon = Icons.Filled.Person,
    unselectedIcon = Icons.Outlined.Person
)
val tasksTab = TabBarItem(
    title = "Tasks",
    selectedIcon = Icons.AutoMirrored.Filled.List,
    unselectedIcon = Icons.AutoMirrored.Outlined.List,
    true
)
val pomodoroTab = TabBarItem(
    title = "Pomodoro",
    selectedIcon = Icons.Filled.Notifications,
    unselectedIcon = Icons.Outlined.Notifications,
)
val settingsTab = TabBarItem(
    title = "Settings",
    selectedIcon = Icons.Filled.Settings,
    unselectedIcon = Icons.Outlined.Settings,
)

// creating list of navigation tabs
val tabBarItems = listOf(profileTab, tasksTab, pomodoroTab, settingsTab)

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
private fun PomoPlannerApp() {
    // creating navController
    val navController = rememberNavController()

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Scaffold(
            bottomBar = { TabView(tabBarItems, navController) }
        ) { innerPadding ->
            NavHost(
                modifier = Modifier.padding(innerPadding),
                navController = navController,
                startDestination = profileTab.title
            ) {
                composable(profileTab.title) {
                    BackHandler(true) {
                        // disable back navigating
                    }
                    ProfileTab()
                }
                composable(tasksTab.title) {
                    BackHandler(true) {
                        // disable back navigating
                    }
                    TasksTab()
                }
                composable(pomodoroTab.title) {
                    BackHandler(true) {
                        // disable back navigating
                    }
                    PomodoroTab()
                }
                composable(settingsTab.title) {
                    BackHandler(true) {
                        // disable back navigating
                    }
                    SettingsTab()
                }
            }
        }
    }
}

// generates navigation bar and handles active navigation tab and switching
@Composable
private fun TabView(tabBarItems: List<TabBarItem>, navController: NavController) {
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
                        navController.navigate(tabBarItem.title)
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