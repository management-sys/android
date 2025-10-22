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

    private val _attendanceState = MutableStateFlow(AttendanceState())
    val attendanceState = _attendanceState.asStateFlow()

    fun onEvent(e: AttendanceEvent) {
        _attendanceState.update { AttendanceReducer.reduce(it, e) }

        when (e) {
            AttendanceEvent.ClickedStartWork -> _uiEffect.tryEmit(UiEffect.ShowToast("출근했습니다."))
            AttendanceEvent.ClickedFinishWork -> _uiEffect.tryEmit(UiEffect.ShowToast("퇴근했습니다."))
        }
    }
}