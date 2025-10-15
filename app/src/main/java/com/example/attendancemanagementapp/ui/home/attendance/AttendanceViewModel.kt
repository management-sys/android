package com.example.attendancemanagementapp.ui.home.attendance

import androidx.lifecycle.ViewModel
import com.example.attendancemanagementapp.data.repository.EmployeeRepository
import com.example.attendancemanagementapp.ui.base.UiEffect
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class AttendanceViewModel @Inject constructor(private val repository: EmployeeRepository) : ViewModel() {
    companion object {
        private const val TAG = "AttendanceViewModel"
    }

    private val _uiEffect = MutableSharedFlow<UiEffect>(extraBufferCapacity = 1)
    val uiEffect = _uiEffect.asSharedFlow()

    private val _attendanceUiState = MutableStateFlow(AttendanceUiState())
    val attendanceUiState = _attendanceUiState.asStateFlow()

    /* 출근 퇴근 처리 */
    fun reverseWorking() {
        _attendanceUiState.update { it.copy(isWorking = !it.isWorking) }
        _uiEffect.tryEmit(UiEffect.ShowToast(if (_attendanceUiState.value.isWorking) "출근했습니다." else "퇴근했습니다."))
    }
}