package com.example.stateflowdemo.ui.home

import androidx.lifecycle.ViewModel
import com.example.stateflowdemo.data.UserPreferencesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ViewModelHome @Inject constructor(userPreferencesRepository: UserPreferencesRepository) : ViewModel() {

    val userPreferencesFlow = userPreferencesRepository.userPreferencesFlow
}