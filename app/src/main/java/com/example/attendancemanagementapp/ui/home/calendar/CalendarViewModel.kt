package com.example.attendancemanagementapp.ui.home.calendar

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
class CalendarViewModel @Inject constructor(private val repository: EmployeeRepository) : ViewModel() {
    companion object {
        private const val TAG = "CalendarViewModel"
    }

    private val _uiEffect = MutableSharedFlow<UiEffect>(extraBufferCapacity = 1)
    val uiEffect = _uiEffect.asSharedFlow()

    private val _calendarState = MutableStateFlow(CalendarState())
    val calendarState = _calendarState.asStateFlow()

    /* 이전 달로 이동 */
    fun onClickPrev() {
        _calendarState.update { it.copy(yearMonth = _calendarState.value.yearMonth.minusMonths(1)) }
    }

    /* 다음 달로 이동 */
    fun onClickNext() {
        _calendarState.update { it.copy(yearMonth = _calendarState.value.yearMonth.plusMonths(1)) }
    }
}