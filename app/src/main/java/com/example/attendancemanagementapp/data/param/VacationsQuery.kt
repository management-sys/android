package com.example.attendancemanagementapp.data.param

import com.example.attendancemanagementapp.data.param.ProjectStatusSearchType.ALL

/* 휴가 현황 목록 조회에서 사용하는 쿼리 데이터 */
data class VacationsQuery(
    val year: Int = 0,
    val filter: VacationsSearchType = VacationsSearchType.TOTAL
)

/* 휴가 현황 필터 종류 */
enum class VacationsSearchType(private val wire: String, val label: String) {
    TOTAL("total", "전체"),
    MINUS("minus", "마이너스 이월"),
    USED("used", "사용"),
    UNUSED("unused", "미사용"),
    OFFICIAL("official", "공가"),
    SICK("sick", "병가"),
    SPECIAL("special", "특별휴가");

    fun toKey(): String {
        return wire
    }

    companion object {
        fun fromLabel(label: String): ProjectStatusSearchType {
            return ProjectStatusSearchType.values().find { it.label == label } ?: ALL
        }
    }
}