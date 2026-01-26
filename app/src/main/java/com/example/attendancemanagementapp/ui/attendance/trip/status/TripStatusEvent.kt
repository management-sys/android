package com.example.attendancemanagementapp.ui.attendance.trip.status

import com.example.attendancemanagementapp.data.dto.TripDTO
import com.example.attendancemanagementapp.data.param.TripsQuery
import com.example.attendancemanagementapp.retrofit.param.PaginationState
import com.example.attendancemanagementapp.ui.attendance.vacation.status.VacationStatusEvent
import com.example.attendancemanagementapp.ui.project.status.ProjectStatusEvent

sealed interface TripStatusEvent {
    // 초기화
    data object Init: TripStatusEvent

    // 연차 선택 이벤트
    data class SelectedYearWith(
        val year: Int?
    ): TripStatusEvent

    // 출장 종류 선택 이벤트
    data class SelectedFilterWith(
        val filter: String
    ): TripStatusEvent

    // 출장 아이템 클릭 이벤트
    data class ClickedTripWith(
        val id: String
    ): TripStatusEvent

    /* 다음 페이지 조회 이벤트 */
    data object LoadNextPage: TripStatusEvent
}