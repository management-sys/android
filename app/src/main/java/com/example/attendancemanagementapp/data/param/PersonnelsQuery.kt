package com.example.attendancemanagementapp.data.param

/* 투입 현황 조회에서 사용하는 쿼리 데이터 */
data class PersonnelsQuery(
    val departmentId: String = "",  // 부서 아이디
    val userName: String = "",      // 사용자 이름
    val year: Int = 0,              // 검색 연도
)