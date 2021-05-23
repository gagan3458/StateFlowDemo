package com.example.stateflowdemo.ui.login

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doOnTextChanged
import androidx.datastore.preferences.preferencesDataStore
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.stateflowdemo.LoginErrorConstants
import com.example.stateflowdemo.databinding.FragmentLoginBinding
import com.example.stateflowdemo.ui.home.HomeActivity
import com.example.stateflowdemo.utils.hideKeyboardFrom
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect


private val Context.datastore by preferencesDataStore(name = "user_prefs")

@AndroidEntryPoint
class FragmentLogin : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!
    private val viewModelLogin by viewModels<ViewModelLogin>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModelLogin.loginUiState.collect { uiState ->
                when (uiState) {
                    is ViewModelLogin.LoginUiStates.Success -> {
                        binding.progressIndicatorLogin.visibility = View.INVISIBLE
                        goToHomeActivity()
                    }
                    is ViewModelLogin.LoginUiStates.Loading -> {
                        binding.progressIndicatorLogin.visibility = View.VISIBLE
                    }
                    is ViewModelLogin.LoginUiStates.Error -> {
                        binding.progressIndicatorLogin.visibility = View.INVISIBLE
                        val errorMessage = when (uiState.errorCode) {
                            LoginErrorConstants.USER_NOT_EXIST -> "User not found in Database"
                            LoginErrorConstants.WRONG_PASS -> "Wrong Password"
                            else -> "Unknown Error"
                        }
                        showSnackBar(errorMessage)
                    }
                    is ViewModelLogin.LoginUiStates.NoActivity -> {
                        binding.apply {
                            binding.progressIndicatorLogin.visibility = View.INVISIBLE
                        }
                    }
                }
            }
        }
        binding.apply {
            buttonLogin.apply {
                setOnClickListener {
                    hideKeyboardFrom(context, binding.root)
                    val email = textFieldEmail.editText?.text.toString()
                    val password = textFieldPassword.editText?.text.toString()
                    viewModelLogin.login(email, password)
                }
            }
            textFieldEmail.editText?.doOnTextChanged { text, _, _, _ ->
                buttonLogin.isEnabled =
                    !text.isNullOrBlank() && !textFieldPassword.editText?.text.isNullOrBlank()
            }
            textFieldPassword.editText?.doOnTextChanged { text, _, _, _ ->
                buttonLogin.isEnabled =
                    !text.isNullOrBlank() && !textFieldEmail.editText?.text.isNullOrBlank()
            }
        }
    }

    private fun goToHomeActivity() {
        startActivity(Intent(activity, HomeActivity::class.java).also {
            it.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        })
    }

    private fun showSnackBar(message: String) {
        Snackbar.make(binding.root, message, Snackbar.LENGTH_SHORT).show()
    }

}