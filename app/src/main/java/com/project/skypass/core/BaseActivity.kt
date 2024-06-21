package com.project.skypass.core

import android.app.Dialog
import android.content.Intent
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.project.skypass.databinding.LayoutStateErrorBinding
import com.project.skypass.presentation.auth.login.LoginActivity
import com.project.skypass.utils.UnauthorizedException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

open class BaseActivity : AppCompatActivity() {
    private val baseViewModel: BaseViewModel by viewModel()

    fun handleUnAuthorize() {
        baseViewModel.clearSession()
        navigateToLogin()
    }

    private fun navigateToLogin() {
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }
}
