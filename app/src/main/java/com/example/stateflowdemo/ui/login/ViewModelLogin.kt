package com.example.stateflowdemo.ui.login

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.stateflowdemo.LoginErrorConstants
import com.example.stateflowdemo.data.UserPreferencesRepository
import com.example.stateflowdemo.data.UserStatus
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val TAG = "ViewModelLogin"

@HiltViewModel
class ViewModelLogin @Inject constructor(
    private val userPreferencesRepository: UserPreferencesRepository
) : ViewModel() {

    sealed class LoginUiStates {
        object Success : LoginUiStates()
        data class Error(val errorCode: Int) : LoginUiStates()
        object Loading : LoginUiStates()
        object NoActivity : LoginUiStates()
    }

    private val hashMap: HashMap<String, String> = hashMapOf()

    init {
        hashMap["armada"] = "silent"
        hashMap["cyberpunk"] = "morpheus"
        Log.d(TAG, "Users in Database : $hashMap")
    }

    private val _loginUiStates = MutableStateFlow<LoginUiStates>(LoginUiStates.NoActivity)
    val loginUiState: StateFlow<LoginUiStates> = _loginUiStates

    fun login(email: String, password: String) = viewModelScope.launch {
        Log.d(TAG, "login: email = $email, password = $password")
        _loginUiStates.value = LoginUiStates.Loading
        // Simulating Network Request Delay
        delay(2000)
        if (hashMap.containsKey(email)) {
            if (hashMap[email] == password) {
                userPreferencesRepository.updateUserStatus(UserStatus.APPROVED)
                _loginUiStates.value = LoginUiStates.Success
            } else {
                _loginUiStates.value = LoginUiStates.Error(LoginErrorConstants.WRONG_PASS)
            }
        } else {
            _loginUiStates.value = LoginUiStates.Error(LoginErrorConstants.USER_NOT_EXIST)
        }
    }
}
