package com.example.attendancemanagementapp.ui.mypage

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class MyPageViewModel @Inject constructor() : ViewModel() {
    companion object {
        private const val TAG = "MyPageViewModel"
    }

    private val _myPageState = MutableStateFlow(MyPageState())
    val myPageState = _myPageState.asStateFlow()

    fun onEvent(e: MyPageEvent) {
        _myPageState.update { MyPageReducer.reduce(it, e) }

        when (e) {

            else -> Unit
        }
    }
}