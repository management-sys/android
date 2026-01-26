package com.example.attendancemanagementapp.ui.attendance.trip.status

import com.example.attendancemanagementapp.data.dto.TripDTO
import com.example.attendancemanagementapp.data.param.TripsQuery
import com.example.attendancemanagementapp.retrofit.param.PaginationState

data class TripStatusState(
    val tripStatusInfo: TripDTO.GetTripsResponse = TripDTO.GetTripsResponse(),  // 출장 현황 목록 데이터
    val query: TripsQuery = TripsQuery(),                                       // 출장 현황 목록 검색 쿼리
    val paginationState: PaginationState = PaginationState(),                   // 페이지네이션 상태
    val types: List<String> = listOf("전체", "국내", "국외")                      // 출장 구분 옵션
)