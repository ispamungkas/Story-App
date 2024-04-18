package com.example.submissionaplikasistory.view

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.app.Dialog
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.submissionaplikasistory.R
import com.example.submissionaplikasistory.databinding.ActivityRegisterBinding
import com.example.submissionaplikasistory.databinding.DialogCustomResponseBinding
import com.example.submissionaplikasistory.di.Injection
import com.example.submissionaplikasistory.utils.Resources
import com.example.submissionaplikasistory.utils.Utils
import com.example.submissionaplikasistory.utils.dataStore
import com.example.submissionaplikasistory.view.viewmodel.UserViewModel

class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding
    private lateinit var dialogBinding: DialogCustomResponseBinding
    private val userViewModel: UserViewModel by viewModels {
        Injection.getUserRepositoryInstance(application.dataStore)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        dialogBinding = DialogCustomResponseBinding.inflate(layoutInflater)
        setContentView(binding.root)

        showAnimation()
        val customDialog = Utils.dialogInstance(this)

        userViewModel.getRegisterResponseResult.observe(this) {
            when (it) {
                is Resources.Loading -> { binding.loadingProcess.visibility = View.VISIBLE }
                is Resources.OnFailure -> {
                    binding.loadingProcess.visibility = View.GONE
                    customDialog.show()
                    customDialog.setContentView(dialogBinding.root)
                    dialogAction(dialogBinding, false, customDialog, it.message)
                }
                is Resources.OnSuccess -> {
                    binding.loadingProcess.visibility = View.GONE
                    it.data.message?.let { value ->
                        customDialog.show()
                        customDialog.setContentView(dialogBinding.root)
                        dialogAction(dialogBinding, true, customDialog, value)
                    }
                }
            }
        }
        binding.btnRegister.setOnClickListener { actionRegister() }
    }

    private fun actionRegister() {
        val name = binding.edRegisterName.text.toString().trim()
        val email = binding.edRegisterEmail.text.toString().trim()
        val password = binding.edRegisterPassword.text.toString().trim()

        userViewModel.requestRegisterAccountStory(name, email, password)
    }

    private fun dialogAction(dialogCustomResponseBinding: DialogCustomResponseBinding, isSuccess: Boolean, dialog: Dialog, messages: String) {
        dialog.setCancelable(false)
        if (isSuccess) {
            dialogCustomResponseBinding.apply {
                message.text = messages
                imageStatus.setImageResource(R.drawable.icon_check)
                actionButton.text = ContextCompat.getString(this@RegisterActivity, R.string.login)
                actionButton.setOnClickListener {
                    dialog.dismiss()
                    this@RegisterActivity.finish()
                }
            }
        } else {
            dialogCustomResponseBinding.apply {
                imageStatus.setImageResource(R.drawable.icon_close)
                message.text = messages
                actionButton.text = ContextCompat.getString(this@RegisterActivity, R.string.back)
                actionButton.setOnClickListener { dialog.dismiss() }
            }
        }
    }

    private fun showAnimation() {
        ObjectAnimator.ofFloat(binding.dicodingBanner, View.TRANSLATION_X, -20f, 20f).apply {
            duration = 50000
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
        }.start()

        val labelName = ObjectAnimator.ofFloat(binding.nameIdentifier, View.ALPHA, 1f).setDuration(1000)
        val labelEmail = ObjectAnimator.ofFloat(binding.emailIdentifier, View.ALPHA, 1f).setDuration(1000)
        val labelPassword = ObjectAnimator.ofFloat(binding.passwordIdentifier, View.ALPHA, 1f).setDuration(1000)
        val email = ObjectAnimator.ofFloat(binding.emailLayout, View.ALPHA, 1f).setDuration(1000)
        val password = ObjectAnimator.ofFloat(binding.passwordLayout, View.ALPHA, 1f).setDuration(1000)
        val name = ObjectAnimator.ofFloat(binding.nameLayout, View.ALPHA, 1f).setDuration(1000)
        val button = ObjectAnimator.ofFloat(binding.btnRegister, View.ALPHA, 1f).setDuration(1000)

        val tName = AnimatorSet().apply { playTogether(labelName, name) }
        val tEmail = AnimatorSet().apply { playTogether(labelEmail, email) }
        val tPassword = AnimatorSet().apply { playTogether(labelPassword, password) }

        AnimatorSet().apply {
            playSequentially(tName, tEmail, tPassword, button)
        }.start()
    }

}