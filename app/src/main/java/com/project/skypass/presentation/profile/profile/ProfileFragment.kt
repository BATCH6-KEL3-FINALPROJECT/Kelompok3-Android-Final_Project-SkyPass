package com.project.skypass.presentation.profile.profile

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import coil.load
import com.project.skypass.R
import com.project.skypass.core.BaseActivity
import com.project.skypass.databinding.FragmentProfileBinding
import com.project.skypass.presentation.profile.changeprofile.ChangeProfileActivity
import com.project.skypass.presentation.profile.settingaccount.SettingsAccountActivity
import com.project.skypass.utils.proceedWhen
import io.github.muddz.styleabletoast.StyleableToast
import org.koin.androidx.viewmodel.ext.android.viewModel

class ProfileFragment : Fragment() {
    private lateinit var binding: FragmentProfileBinding
    private val profileViewModel: ProfileViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)
        setClickAction()
    }

    override fun onResume() {
        super.onResume()
        displayProfileData()
    }

    private fun setClickAction() {
        binding.llSettingsAccount.setOnClickListener {
            val intent = Intent(activity, SettingsAccountActivity::class.java)
            startActivity(intent)
        }
        binding.llEditProfile.setOnClickListener {
            val intent = Intent(activity, ChangeProfileActivity::class.java)
            startActivity(intent)
        }
        binding.llLogout.setOnClickListener {
            showLogoutDialog()
        }
    }

    private fun displayProfileData() {
        val userId = profileViewModel.getUserId()
        profileViewModel.showDataUser(userId).observe(viewLifecycleOwner) { result ->
            result.proceedWhen(
                doOnSuccess = {
                    val user = it.payload
                    binding.tvNameProfile.text = user?.name
                    binding.tvEmailProfile.text = user?.email
                    binding.tvNumberPhoneProfile.text = user?.phoneNumber
                    binding.ivProfile.load(user?.photoUrl) {
                        fallback(R.drawable.iv_profile)
                    }
                    updateVerificationStatus(user?.isVerified == true)
                },
                doOnLoading = {
                },
                doOnError = {
                }
            )
        }
    }

    private fun updateVerificationStatus(isVerified: Boolean) {
        val verificationIconRes =
            if (isVerified) {
                R.drawable.ic_verified_user
            } else {
                R.drawable.ic_not_verified
            }
        binding.ivVerifyStatus.setImageResource(verificationIconRes)
    }

    private fun showLogoutDialog() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle(getString(R.string.title_logout))
        builder.setMessage(getString(R.string.text_apakah_kamu_yakin_ingin_keluar))
        builder.setPositiveButton(getString(R.string.yes)) { dialog, _ ->
            logout()
            deleteOrderHistoryUser()
            dialog.dismiss()
        }
        builder.setNegativeButton(getString(R.string.cancel)) { dialog, _ ->
            dialog.dismiss()
        }
        builder.create().show()
    }

    private fun deleteOrderHistoryUser() {
        profileViewModel.deleteOrderHistoryUser().observe(viewLifecycleOwner) {
            it.proceedWhen(doOnSuccess = {
                Toast.makeText(
                    requireContext(),
                    getString(R.string.text_hapus_riwayat_pemesanan),
                    Toast.LENGTH_SHORT,
                ).show()
            }, doOnLoading = {
            }, doOnError = { err ->
                StyleableToast.makeText(requireContext(),
                    getString(R.string.failed_delete_order), Toast.LENGTH_SHORT).show()
            })
        }
    }

    private fun logout() {
        (activity as BaseActivity).handleUnAuthorize()
    }
}
