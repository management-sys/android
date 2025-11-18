package com.example.attendancemanagementapp.ui.login

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.attendancemanagementapp.data.datastore.TokenDataStore
import com.example.attendancemanagementapp.data.dto.AuthDTO
import com.example.attendancemanagementapp.data.repository.AuthRepository
import com.example.attendancemanagementapp.ui.base.UiEffect
import com.example.attendancemanagementapp.util.ErrorHandler
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(private val authRepository: AuthRepository, private val tokenDataStore: TokenDataStore) : ViewModel() {
    companion object {
        private const val TAG = "LoginViewModel"
    }

    private val _uiEffect = MutableSharedFlow<UiEffect>(extraBufferCapacity = 1)
    val uiEffect = _uiEffect.asSharedFlow()

    private val _loginState = MutableStateFlow(LoginState())
    val loginState = _loginState.asStateFlow()

    fun onEvent(e: LoginEvent) {
        _loginState.update { LoginReducer.reduce(it, e) }

        when (e) {
            LoginEvent.ClickedLogin -> { login() }
            else -> Unit
        }
    }

    fun init() {
        _loginState.value = LoginState()
    }

    /* 로그인 */
    fun login() {
        val request = AuthDTO.LoginRequest(
            id = loginState.value.id,
            password = loginState.value.password
        )

        viewModelScope.launch {
            authRepository.login(request).collect { result ->
                result
                    .onSuccess { tokenData ->
                        tokenDataStore.saveTokens(tokenData)    // DataStore 토큰 저장

                        _uiEffect.emit(UiEffect.Navigate("home"))
                        _uiEffect.emit(UiEffect.ShowToast("로그인 되었습니다"))

                        Log.d(TAG, "[login] 로그인 성공\n${tokenData}")
                    }
                    .onFailure { e ->
                        ErrorHandler.handle(e, TAG, "login")
                    }
            }
        }
    }
}