package com.example.submissionaplikasistory.view

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.submissionaplikasistory.R
import com.example.submissionaplikasistory.databinding.ActivityLoginBinding
import com.example.submissionaplikasistory.databinding.DialogCustomResponseBinding
import com.example.submissionaplikasistory.di.Injection
import com.example.submissionaplikasistory.utils.Resources
import com.example.submissionaplikasistory.utils.dataStore
import com.example.submissionaplikasistory.view.home.MainActivity
import com.example.submissionaplikasistory.view.viewmodel.UserViewModel

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private lateinit var dialogBinding: DialogCustomResponseBinding
    private val userViewModel: UserViewModel by viewModels{
        Injection.getUserRepositoryInstance(application.dataStore)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityLoginBinding.inflate(layoutInflater)
        dialogBinding = DialogCustomResponseBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.apply {
            directRegister.setOnClickListener { goRegister() }
            btnLogin.setOnClickListener {
                actionLogin()
            }
        }

        showAnimation()

        userViewModel.getLoginResponseResult.observe(this) {
            when (it) {
                is Resources.Loading -> { binding.loading.visibility = View.VISIBLE }
                is Resources.OnFailure -> {
                    binding.loading.visibility = View.GONE
                    showToast(false)
                }
                is Resources.OnSuccess -> {
                    binding.loading.visibility = View.GONE
                    it.data.loginResult.let {
                        userViewModel.saveUserSession(it!!)
                    }
                    it.data.message?.let { value -> showToast(true) }
                    goToHomeScreen()
                }
            }
        }
    }

    private fun showAnimation() {
        ObjectAnimator.ofFloat(binding.bannerImageLogin, View.TRANSLATION_X, -20f, 20f).apply {
            duration = 5000
            repeatCount =ObjectAnimator.INFINITE
            repeatMode  = ObjectAnimator.REVERSE
        }.start()

        val greeting = ObjectAnimator.ofFloat(binding.greetingLayout, View.ALPHA, 1f).setDuration(1000)
        val labelEmail = ObjectAnimator.ofFloat(binding.tvEmail, View.ALPHA, 1f).setDuration(1000)
        val labelPassword = ObjectAnimator.ofFloat(binding.tvPassword, View.ALPHA, 1f).setDuration(1000)
        val email = ObjectAnimator.ofFloat(binding.emailLayout, View.ALPHA, 1f).setDuration(1000)
        val password = ObjectAnimator.ofFloat(binding.passwordLayout, View.ALPHA, 1f).setDuration(1000)
        val register_access = ObjectAnimator.ofFloat(binding.accountLayout, View.ALPHA, 1f).setDuration(1000)
        val button_signin = ObjectAnimator.ofFloat(binding.btnLogin, View.ALPHA, 1f).setDuration(1000)

        val tEmail = AnimatorSet().apply {
            playTogether(labelEmail, email)
        }

        val tPassword = AnimatorSet().apply {
            playTogether(labelPassword, password)
        }

        AnimatorSet().apply {
            playSequentially(greeting, tEmail, tPassword, register_access, button_signin)
        }.start()
    }

    private fun showToast(isLogged: Boolean) {
        if (isLogged) {
            Toast.makeText(this@LoginActivity, resources.getString(R.string.value_login), Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this@LoginActivity, resources.getString(R.string.warning), Toast.LENGTH_SHORT).show()
        }
    }

    private fun actionLogin() {
        val email = binding.edLoginEmail.text.toString().trim()
        val password = binding.edLoginPassword.text.toString().trim()
        userViewModel.requestLoginAccountStory(email, password)
    }

    private fun goRegister() {
        val intent = Intent(this, RegisterActivity::class.java)
        startActivity(intent)
    }

    private fun goToHomeScreen() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finishAffinity()
    }
}