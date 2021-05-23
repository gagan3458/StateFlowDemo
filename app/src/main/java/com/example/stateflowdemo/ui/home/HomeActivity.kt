package com.example.stateflowdemo.ui.home

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.stateflowdemo.R
import com.example.stateflowdemo.data.UserStatus
import com.example.stateflowdemo.ui.login.LoginActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

private const val TAG = "HomeActivity"

@AndroidEntryPoint
class HomeActivity : AppCompatActivity() {

    private val viewModelHomeActivity by viewModels<ViewModelHome>()
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        lifecycleScope.launch {
            when (viewModelHomeActivity.userPreferencesFlow.first().userStatus) {
                UserStatus.NEW -> {
                    goToLoginScreen()
                }
                UserStatus.NOT_APPROVED -> {
                    goToWaitListScreen()
                }
                UserStatus.APPROVED -> {
                    // Do Nothing
                }
            }
        }
        setTheme(R.style.Theme_StateFlowDemo)
        setContentView(R.layout.activity_home)
    }

    private fun goToWaitListScreen() {
        TODO("Not yet implemented")
    }

    private fun goToLoginScreen() {
        startActivity(Intent(this, LoginActivity::class.java).also {
            it.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        })
    }
}