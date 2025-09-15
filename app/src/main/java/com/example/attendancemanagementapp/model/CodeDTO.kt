package com.example.attendancemanagementapp.model

object CodeDTO {
    /* 임시 공통코드 목록 데이터 */
    data class CodeListInfo(
        val code: String = "",          // 코드
        val codeName: String = "",      // 코드명
        val upperCode: String = "",     // 상위코드
        val upperCodeName: String = "", // 상위코드명
        val registerDate: String = "",  // 등록일
        val isUse: Boolean = true      // 사용여부
    )

    /* 임시 공통코드 데이터 */
    data class CodeInfo(
        val code: String = "",          // 코드
        val codeName: String = "",      // 코드명
        val upperCode: String = "",     // 상위코드
        val upperCodeName: String = "", // 상위코드명
        val codeValue: String = "",     // 코드값
        val description: String = "",   // 설명
        val isUse: Boolean = true      // 사용여부
    )
}