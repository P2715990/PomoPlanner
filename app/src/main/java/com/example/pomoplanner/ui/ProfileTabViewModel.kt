package com.example.pomoplanner.ui

import android.app.Application
import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import com.example.caferose.model.HashHelper
import com.example.pomoplanner.model.DBHelper
import com.example.pomoplanner.model.Profile
import kotlin.collections.List

class ProfileTabViewModel(application: Application) : AndroidViewModel(application) {
    private val context: Context
        get() = getApplication<Application>().applicationContext
    val dbHelper: DBHelper = DBHelper(context)

    val hashHelper: HashHelper = HashHelper()

    private var _profiles by mutableStateOf<List<Profile>>(listOf<Profile>())
    val profiles: List<Profile>
        get() = _profiles

    private var _interactedProfile by mutableStateOf<Profile?>(null)
    val interactedProfile: Profile?
        get() = _interactedProfile

    private var _selectedProfile by mutableStateOf<Profile?>(null)
    val selectedProfile: Profile?
        get() = _selectedProfile

    private var _addProfileErrorMessage by mutableStateOf("")
    val addProfileErrorMessage: String
        get() = _addProfileErrorMessage

    private var _deleteProfileErrorMessage by mutableStateOf("")
    val deleteProfileErrorMessage: String
        get() = _deleteProfileErrorMessage

    private var _enterPasswordErrorMessage by mutableStateOf("")
    val enterPasswordErrorMessage: String
        get() = _enterPasswordErrorMessage

    private var _showAddProfilePopup by mutableStateOf(false)
    val showAddProfilePopup: Boolean
        get() = _showAddProfilePopup

    private var _showConfirmDeletePopup by mutableStateOf(false)
    val showConfirmDeletePopup: Boolean
        get() = _showConfirmDeletePopup

    private var _showPasswordPopup by mutableStateOf(false)
    val showPasswordPopup: Boolean
        get() = _showPasswordPopup

    // retrieve data from model

    fun getProfiles() {
        _profiles = dbHelper.getProfiles()
    }

    fun getSelectedProfile() {
        _selectedProfile = dbHelper.getSelectedProfile()
    }

    // add data to model

    fun addProfile(profile: Profile) {
        _addProfileErrorMessage = ""
        if (profile.profileUsername == "") {
            _addProfileErrorMessage += "Please Enter a Username"
        }
        if (profile.profileUsername.length > 30) {
            _addProfileErrorMessage += "Username Shouldn't Be Longer Than 30 Characters"
        }

        val checkProfile = dbHelper.getProfile(profile.profileUsername)
        if (checkProfile.profileId != -1) {
            if (addProfileErrorMessage != "") {
                _addProfileErrorMessage += "\n\n"
            }
            _addProfileErrorMessage += "Username is Taken, Please Try Another"
        }

        if (profile.profilePassword != null) {
            if (profile.profilePassword!!.length > 50) {
                if (addProfileErrorMessage != "") {
                    _addProfileErrorMessage += "\n\n"
                }
                _addProfileErrorMessage += "Password Shouldn't Be Longer Than 50 Characters"
            }
        }

        if (addProfileErrorMessage == "") {
            if (profile.profilePassword != null) {
                val hashedPassword = hashHelper.getHashCode(profile.profilePassword!!)
                profile.profilePassword = hashedPassword
            }

            dbHelper.addProfile(profile)
            getProfiles()
            setShowAddProfilePopup(false)
        }
    }

    // update data in model

    fun swapSelectedProfile(profile: Profile) {
        if(selectedProfile != null) {
            _selectedProfile?.profileIsSelected = false
            dbHelper.updateProfile(selectedProfile!!)
        }
        profile.profileIsSelected = true
        dbHelper.updateProfile(profile)
        getProfiles()
        getSelectedProfile()
    }

    // delete data from model

    fun deleteProfile(profile: Profile, password: String) {
        _deleteProfileErrorMessage = ""
        if (profile.profilePassword != null) {
            val hashedPassword = hashHelper.getHashCode(password)
            if (profile.profilePassword != hashedPassword) {
                _deleteProfileErrorMessage += "Password is Incorrect"
            }
        }

        if (deleteProfileErrorMessage == "") {
            dbHelper.deleteProfile(profile)
            getProfiles()
            setShowConfirmDeletePopup(false)
        }
    }

    // update data in view model

    fun onProfileClicked(profile: Profile) {
        setInteractedProfile(profile)
        if (interactedProfile?.profilePassword != null) {
            setShowPasswordPopup(true)
        } else {
            swapSelectedProfile(profile)
        }
    }

    fun onLoginClicked(profile: Profile, password: String) {
        _enterPasswordErrorMessage = ""

        val hashedPassword = hashHelper.getHashCode(password)
        if (profile.profilePassword != hashedPassword) {
            _enterPasswordErrorMessage += "Password is Incorrect"
        }

        if (enterPasswordErrorMessage == "") {
            swapSelectedProfile(profile)
            setShowPasswordPopup(false)
        }
    }

    fun onDeleteClicked(profile: Profile) {
        setInteractedProfile(profile)
        setShowConfirmDeletePopup(true)
    }

    fun setInteractedProfile(profile: Profile) {
        _interactedProfile = profile
    }

    // display and hide popups

    fun setShowAddProfilePopup(show: Boolean) {
        if (!show) {
            _showAddProfilePopup = false
        } else {
            setShowConfirmDeletePopup(false)
            setShowPasswordPopup(false)
            _showAddProfilePopup = true
        }
    }

    fun setShowConfirmDeletePopup(show: Boolean) {
        if (!show) {
            _showConfirmDeletePopup = false
        } else {
            setShowAddProfilePopup(false)
            setShowPasswordPopup(false)
            _showConfirmDeletePopup = true
        }
    }

    fun setShowPasswordPopup(show: Boolean) {
        if (!show) {
            _showPasswordPopup = false
        } else {
            setShowAddProfilePopup(false)
            setShowConfirmDeletePopup(false)
            _showPasswordPopup = true
        }
    }

}