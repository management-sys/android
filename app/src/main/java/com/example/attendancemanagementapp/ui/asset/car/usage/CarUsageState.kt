package com.example.attendancemanagementapp.ui.asset.car.usage

import com.example.attendancemanagementapp.data.dto.CarDTO.UsageInfo
import com.example.attendancemanagementapp.retrofit.param.PaginationState

data class CarUsageState(
    val reservationState: CarUsageSearchState = CarUsageSearchState(),  // 차량 예약현황 검색 관련 상태
    val usageState: CarUsageSearchState = CarUsageSearchState(),        // 차량 사용이력 검색 관련 상태
)

/* 차량 예약현황/사용이력 검색 관련 상태 */
data class CarUsageSearchState(
    val histories: List<UsageInfo> = emptyList(),               // 차량 예약현황/사용이력 목록
    val searchText: String = "",                                // 검색어
    val type: String = "전체",                                   // 검색 타입 (차량명, 차량번호, 운행자)
    val paginationState: PaginationState = PaginationState()    // 페이지네이션 상태
)