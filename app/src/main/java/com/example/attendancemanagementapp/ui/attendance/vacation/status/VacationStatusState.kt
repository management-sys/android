package com.example.attendancemanagementapp.ui.attendance.vacation.status

import com.example.attendancemanagementapp.data.dto.VacationDTO
import com.example.attendancemanagementapp.data.param.VacationsQuery
import com.example.attendancemanagementapp.retrofit.param.PaginationState

data class VacationStatusState(
    val vacationStatusInfo: VacationDTO.GetVacationsResponse = VacationDTO.GetVacationsResponse(),  // 휴가 현황 목록 데이터
    val query: VacationsQuery = VacationsQuery(),                                                   // 휴가 현황 목록 검색 쿼리
    val paginationState: PaginationState = PaginationState()                                        // 페이지네이션 상태
)