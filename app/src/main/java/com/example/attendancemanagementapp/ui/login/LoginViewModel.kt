package com.example.attendancemanagementapp.ui.login

import androidx.lifecycle.ViewModel
import com.example.attendancemanagementapp.ui.hr.department.add.DepartmentAddState
import com.example.attendancemanagementapp.ui.hr.department.detail.DepartmentDetailEvent
import com.example.attendancemanagementapp.ui.hr.department.detail.DepartmentDetailReducer
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor() : ViewModel() {
    companion object {
        private const val TAG = "LoginViewModel"
    }

    private val _loginState = MutableStateFlow(LoginState())
    val loginState = _loginState.asStateFlow()

    fun onEvent(e: LoginEvent) {
        _loginState.update { LoginReducer.reduce(it, e) }

        when (e) {
            LoginEvent.ClickedLogin -> {  }
            else -> Unit
        }
    }
}