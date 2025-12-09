package com.example.attendancemanagementapp.ui.meeting.status

import com.example.attendancemanagementapp.data.dto.MeetingDTO
import com.example.attendancemanagementapp.retrofit.param.PaginationState

data class MeetingStatusState(
    val meetings: List<MeetingDTO.MeetingsInfo> = emptyList(),  // 회의록 목록
    val startDate: String = "",                                 // 검색 시작일
    val endDate: String = "",                                   // 검색 마지막일
    val type: String = "전체",                                   // 검색 종류
    val searchText: String = "",                                // 검색어
    val paginationState: PaginationState = PaginationState()    // 페이지네이션 상태
)