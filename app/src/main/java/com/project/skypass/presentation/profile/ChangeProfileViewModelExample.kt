package com.project.skypass.presentation.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.project.skypass.data.model.User
import com.project.skypass.data.repository.pref.PrefRepository
import com.project.skypass.data.repository.user.UserRepository
import com.project.skypass.data.source.network.model.user.edituser.EditUserResponse
import com.project.skypass.utils.ResultWrapper
import kotlinx.coroutines.Dispatchers
import java.io.File

class ChangeProfileViewModelExample(
    private val prefRepository: PrefRepository,
    private val userRepository: UserRepository
): ViewModel() {
    fun editUserData(
        token: String,
        id: String,
        name: String? = null,
        email: String? = null,
        phoneNumber: String? = null,
        password: String? = null,
        photo: File? = null
    ): LiveData<ResultWrapper<EditUserResponse>> {
        return userRepository.editUser(token, id, name, email, phoneNumber, password, photo).asLiveData(Dispatchers.IO)
    }

    fun getToken(): String {
        return prefRepository.getToken()
    }

    fun getUserId(): String {
        return prefRepository.getUserID()
    }

    fun showDataUser(id: String): LiveData<ResultWrapper<User>> {
        return userRepository.getUser(id).asLiveData(Dispatchers.IO)
    }
}