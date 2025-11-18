package com.example.attendancemanagementapp.ui.mypage

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.attendancemanagementapp.data.datastore.TokenDataStore
import com.example.attendancemanagementapp.data.dto.EmployeeDTO
import com.example.attendancemanagementapp.data.repository.AuthRepository
import com.example.attendancemanagementapp.data.repository.EmployeeRepository
import com.example.attendancemanagementapp.ui.base.UiEffect
import com.example.attendancemanagementapp.util.ErrorHandler
import com.example.attendancemanagementapp.util.formatDateTime
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MyPageViewModel @Inject constructor(private val employeeRepository: EmployeeRepository, private val authRepository: AuthRepository, private val tokenDataStore: TokenDataStore) : ViewModel() {
    companion object {
        private const val TAG = "MyPageViewModel"
    }

    private val _uiEffect = MutableSharedFlow<UiEffect>(extraBufferCapacity = 1)
    val uiEffect = _uiEffect.asSharedFlow()

    private val _myPageState = MutableStateFlow(MyPageState())
    val myPageState = _myPageState.asStateFlow()

    fun onEvent(e: MyPageEvent) {
        _myPageState.update { MyPageReducer.reduce(it, e) }

        when (e) {
            MyPageEvent.ClickedUpdate -> { updateMyInfo() }
            MyPageEvent.ClickedLogout -> { logout() }
            else -> Unit
        }
    }

    fun init() {
        getMyInfo()
    }

    /* 내 정보 조회 */
    fun getMyInfo() {
        viewModelScope.launch {
            employeeRepository.getMyInfo().collect { result ->
                result
                    .onSuccess { myInfo ->
                        _myPageState.update { it.copy(myInfo = myInfo) }

                        Log.d(TAG, "[getMyInfo] 내 정보 조회 성공: ${myInfo}")
                    }
                    .onFailure { e ->
                        ErrorHandler.handle(e, TAG, "getMyInfo")
                    }
            }
        }
    }

    /* 내 정보 수정 */
    fun updateMyInfo() {
        val state = myPageState.value
        val request = EmployeeDTO.UpdateMyInfoRequest(
            name = state.myInfo.name,
            phone = state.myInfo.phone,
            birthDate = formatDateTime(state.myInfo.birthDate),
            curPassword = state.curPassword,
            newPassword = state.newPassword
        )

        viewModelScope.launch {
            employeeRepository.updateMyInfo(request).collect { result ->
                result
                    .onSuccess { newMyInfo ->
                        _myPageState.update { it.copy(myInfo = newMyInfo, curPassword = "", newPassword = "") }

                        _uiEffect.emit(UiEffect.ShowToast("수정이 완료되었습니다"))

                        Log.d(TAG, "[updateMyInfo] 내 정보 수정 성공: ${newMyInfo}")
                    }
                    .onFailure { e ->
                        ErrorHandler.handle(e, TAG, "updateMyInfo")
                    }
            }
        }
    }

    /* 로그아웃 */
    fun logout() {
        viewModelScope.launch {
            authRepository.logout().collect { result ->
                result
                    .onSuccess { message ->
                        tokenDataStore.clearTokens()    // 토큰 초기화

                        _uiEffect.emit(UiEffect.AllDeleteNavigate("login"))
                        _uiEffect.emit(UiEffect.ShowToast("로그아웃 되었습니다"))

                        Log.d(TAG, "[logout] 로그아웃 성공: ${message}")
                    }
                    .onFailure { e ->
                        ErrorHandler.handle(e, TAG, "logout")
                    }
            }
        }
    }
}