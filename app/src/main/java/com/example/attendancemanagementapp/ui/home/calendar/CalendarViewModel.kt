package com.example.attendancemanagementapp.ui.home.calendar

import androidx.lifecycle.ViewModel
import com.example.attendancemanagementapp.data.repository.EmployeeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class CalendarViewModel @Inject constructor(private val repository: EmployeeRepository) : ViewModel() {
    companion object {
        private const val TAG = "CalendarViewModel"
    }

    private val _calendarUiState = MutableStateFlow(CalendarUiState())
    val calendarUiState = _calendarUiState.asStateFlow()

    /* 이전 달로 이동 */
    fun onClickPrev() {
        _calendarUiState.update { it.copy(yearMonth = _calendarUiState.value.yearMonth.minusMonths(1)) }
    }

    /* 다음 달로 이동 */
    fun onClickNext() {
        _calendarUiState.update { it.copy(yearMonth = _calendarUiState.value.yearMonth.plusMonths(1)) }
    }
}