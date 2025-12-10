package com.example.attendancemanagementapp.retrofit.param

enum class SearchType(private val wire: String, val label: String) {
    ALL("ALL", "전체"),
    CODE("CODE", "코드"),
    CODE_NM("CODE_NM", "코드 이름"),
    UPPER_CODE("UPPER_CODE", "상위코드"),
    UPPER_CODE_NM("UPPER_CODE_NM", "상위코드 이름");

    override fun toString(): String {
        return wire
    }
}