package com.example.attendancemanagementapp.ui.asset.card.usage

import com.example.attendancemanagementapp.data.dto.CardDTO
import com.example.attendancemanagementapp.retrofit.param.PaginationState

data class CardUsageState(
    val reservationState: CardUsageSearchState = CardUsageSearchState(),  // 카드 예약현황 검색 관련 상태
    val usageState: CardUsageSearchState = CardUsageSearchState(),        // 카드 사용이력 검색 관련 상태
)

/* 카드 예약현황/사용이력 검색 관련 상태 */
data class CardUsageSearchState(
    val histories: List<CardDTO.CardUsageInfo> = emptyList(),   // 카드 예약현황/사용이력 목록
    val searchText: String = "",                                // 검색어
    val type: String = "전체",                                   // 검색 타입 (카드명, 신청자)
    val paginationState: PaginationState = PaginationState()    // 페이지네이션 상태
)