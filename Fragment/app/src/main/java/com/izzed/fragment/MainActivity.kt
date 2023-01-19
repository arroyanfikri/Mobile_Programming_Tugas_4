package com.izzed.fragment

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import com.izzed.fragment.databinding.ActivityMainBinding
import com.jakewharton.rxbinding2.widget.RxTextView
import io.reactivex.Observable

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        processedLogin()
        noAction()
    }

    @SuppressLint("checkResult")
    private fun processedLogin() {
        binding.apply {
            val emailStream = RxTextView.textChanges(edtEmail)
                .skipInitialValue()
                .map { email ->
                    !Patterns.EMAIL_ADDRESS.matcher(email).matches()
                }
            emailStream.subscribe {
                showEmailExistAlert(it)
            }

            val passwordStream = RxTextView.textChanges(edtPassword)
                .skipInitialValue()
                .map { password ->
                    password.length < 8
                }
            passwordStream.subscribe {
                showPasswordExistAlert(it)
            }

            val invalidFieldStream = Observable.combineLatest(
                emailStream,
                passwordStream
            ) {
                    emailInvalid, passwordInvalid ->
                !emailInvalid && !passwordInvalid
            }
            invalidFieldStream.subscribe {
                showButtonLogin(it)
            }
        }
    }

    private fun showEmailExistAlert(state: Boolean) {
        binding.edtEmail.error = if (state) "Email not valid" else null
    }

    private fun showPasswordExistAlert(state: Boolean) {
        binding.edtPassword.error = if (state) "Password length less then 8" else null
    }

    private fun showButtonLogin(state: Boolean) {
        binding.btnLogin.isEnabled = state
    }

    private fun noAction() {
        binding.apply {
            btnLogin.setOnClickListener {
                Intent(this@MainActivity, HomeActivity::class.java).also { intent ->
                    startActivity(intent)
                }
            }
        }
    }
}