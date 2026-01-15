package com.example.attendancemanagementapp.ui.asset.card.manage

import com.example.attendancemanagementapp.data.dto.CardDTO

data class CardManageState(
    val cards: List<CardDTO.CardsInfo> = emptyList(),   // 카드 목록
    val searchText: String = "",                        // 카드 검색어
    val type: String = "전체",                           // 검색 키워드
)