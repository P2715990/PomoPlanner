package com.example.pomoplanner.model

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

class Profile (
    val profileId: Int,
    val profileUsername: String,
    var profilePassword: String?,
    initialProfileIsSelected: Boolean,
) {
    var profileIsSelected by mutableStateOf(initialProfileIsSelected)
}