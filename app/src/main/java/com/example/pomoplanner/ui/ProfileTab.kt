package com.example.pomoplanner.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.pomoplanner.model.Profile
import com.example.pomoplanner.ui.theme.TaskGreen

@Composable
fun ProfileTab(
    profileTabViewModel: ProfileTabViewModel = viewModel(),
) {
    profileTabViewModel.getProfiles()
    profileTabViewModel.getSelectedProfile()

    Scaffold(
        contentWindowInsets = WindowInsets(0.dp),
        topBar = {
            ProfileTopBar { profileTabViewModel.setShowAddProfilePopup(true) }
        }
    ) { innerPadding ->
        ProfileListView(
            profiles = profileTabViewModel.profiles,
            onProfileClicked = { profile ->
                profileTabViewModel.onProfileClicked(profile)
            },
            onDeleteButtonClicked = { profile ->
                profileTabViewModel.onDeleteClicked(profile)
            },
            padding = innerPadding,
        )
    }

    CustomPopupHelper(
        showPopup = profileTabViewModel.showAddProfilePopup,
        onClickOutside = { profileTabViewModel.setShowAddProfilePopup(false) },
        content = {
            AddProfileView(
                { profile ->
                    profileTabViewModel.addProfile(profile)
                },
                profileTabViewModel.addProfileErrorMessage
            )
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileTopBar(
    onAddButtonClicked: () -> Unit,
) {
    TopAppBar(
        title = {
            Text(
                text = "Profiles",
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        },
        actions = {
            IconButton(onClick = { onAddButtonClicked() }) {
                Icon(
                    imageVector = Icons.Filled.Add,
                    contentDescription = "Add Profile"
                )
            }
        }
    )
}

@Composable
fun ProfileListView(
    profiles: List<Profile>,
    onProfileClicked: (Profile) -> Unit,
    onDeleteButtonClicked: (Profile) -> Unit,
    padding: PaddingValues,
) {
    val interactionSource = remember { MutableInteractionSource() }

    LazyColumn(
        modifier = Modifier
            .padding(padding)
            .fillMaxSize(),
        state = rememberLazyListState(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        profiles.forEachIndexed { index, profile ->
            item {
                Surface(
                    modifier = Modifier
                        .fillMaxSize()
                        .clickable(
                            interactionSource = interactionSource,
                            indication = null
                        ) { onProfileClicked(profile) },
                    shape = RoundedCornerShape(16.dp),
                    color = if (profile.profileIsSelected) TaskGreen else MaterialTheme.colorScheme.primary
                ) {
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            modifier = Modifier
                                .padding(start = 16.dp),
                            text = profile.profileUsername
                        )

                        IconButton(onClick = { onDeleteButtonClicked(profile) }
                        ) {
                            Icon(Icons.Filled.Delete, "Delete")
                        }
                    }
                }
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddProfileView(
    onAddButtonClicked: (Profile) -> Unit,
    addProfileErrorMessage: String,
) {
    var profileUsernameText by remember { mutableStateOf("") }
    var profilePasswordText by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .padding(24.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Add New Profile",
            color = MaterialTheme.colorScheme.onBackground
        )

        OutlinedTextField(
            modifier = Modifier
                .fillMaxWidth(),
            value = profileUsernameText,
            onValueChange = { profileUsernameText = it },
            label = { Text("Username") },
            colors = ExposedDropdownMenuDefaults.textFieldColors()
        )

        OutlinedTextField(
            modifier = Modifier
                .fillMaxWidth(),
            value = profilePasswordText,
            onValueChange = { profilePasswordText = it },
            label = { Text("Password (Optional)") },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password
            ),
            visualTransformation = PasswordVisualTransformation(),
            colors = ExposedDropdownMenuDefaults.textFieldColors()
        )

        Button(
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(8.dp))
                .background(MaterialTheme.colorScheme.primary),
            onClick = {
                onAddButtonClicked(
                    Profile(
                        -1,
                        profileUsernameText.toString(),
                        if (profilePasswordText.toString() == "") null else profilePasswordText.toString(),
                        false
                    )
                )
            },
            shape = RectangleShape
        ) {
            Text(
                text = "Add Profile",
                color = MaterialTheme.colorScheme.onPrimary
            )
        }

        if (addProfileErrorMessage != "") {
            Text(
                text = addProfileErrorMessage,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.error
            )
        }
    }
}