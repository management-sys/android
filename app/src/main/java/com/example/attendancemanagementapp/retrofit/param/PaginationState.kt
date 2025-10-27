package com.example.attendancemanagementapp.retrofit.param

data class PaginationState(
    val currentPage: Int = 0,                                       // 현재 페이지 번호
    val totalPage: Int = Int.MAX_VALUE,                             // 총 페이지 개수
    val isLoading: Boolean = false                                  // 로딩 중 여부
)