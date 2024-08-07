package com.project.skypass.data.repository.user

import com.project.skypass.data.datasource.user.UserDataSource
import com.project.skypass.data.mapper.toUser
import com.project.skypass.data.model.Response
import com.project.skypass.data.model.User
import com.project.skypass.data.source.network.model.user.deleteuser.DeleteUserResponse
import com.project.skypass.data.source.network.model.user.edituser.Data
import com.project.skypass.utils.ResultWrapper
import com.project.skypass.utils.proceedFlow
import kotlinx.coroutines.flow.Flow
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

class UserRepositoryImpl(private val dataSource: UserDataSource) : UserRepository {
    override fun isUsingDarkMode(): Boolean {
        return dataSource.isUsingDarkMode()
    }

    override fun setUsingDarkMode(isUsingDarkMode: Boolean) {
        dataSource.setUsingDarkMode(isUsingDarkMode)
    }

    override fun getUser(id: String): Flow<ResultWrapper<User>> {
        return proceedFlow {
            dataSource.getUser(id).data?.user.toUser()
        }
    }

    override fun editUser(
        token: String,
        id: String,
        name: String,
        email: String,
        phoneNumber: String,
        photo: File?,
    ): Flow<ResultWrapper<Response<Data>>> {
        val tokenPart = "Bearer $token"
        val namePart = name.toRequestBody("text/plain".toMediaTypeOrNull())
        val emailPart = email.toRequestBody("text/plain".toMediaTypeOrNull())
        val phoneNumberPart = phoneNumber.toRequestBody("text/plain".toMediaTypeOrNull())
        val picturePart =
            photo?.let {
                val requestFile = it.asRequestBody("image/jpeg".toMediaTypeOrNull())
                MultipartBody.Part.createFormData("images", it.name, requestFile)
            }
        return proceedFlow {
            dataSource.editUser(tokenPart, id, namePart, emailPart, phoneNumberPart, picturePart)
        }
    }

    override fun deleteUser(token: String): Flow<ResultWrapper<DeleteUserResponse>> {
        return proceedFlow {
            val tokenPart = "Bearer $token"
            dataSource.deleteUser(tokenPart)
        }
    }
}
