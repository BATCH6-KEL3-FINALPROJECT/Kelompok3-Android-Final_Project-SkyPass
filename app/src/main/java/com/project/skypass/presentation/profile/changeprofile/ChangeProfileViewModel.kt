package com.project.skypass.presentation.profile.changeprofile

import androidx.lifecycle.ViewModel
import com.project.skypass.data.repository.profile.ProfileRepository

class ChangeProfileViewModel(private val repository: ProfileRepository) : ViewModel() {
    fun getProfile() = repository.getProfile()
}