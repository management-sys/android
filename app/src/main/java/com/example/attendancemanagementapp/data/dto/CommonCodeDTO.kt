package com.example.attendancemanagementapp.data.dto

import com.squareup.moshi.Json

object CommonCodeDTO {
    // 공통코드 목록 데이터
    data class CommonCodesInfo(
        val code: String = "",                                          // 코드
        @Json(name = "codeNm") val codeName: String = "",               // 코드명
        val upperCode: String? = "",                                    // 상위코드
        @Json(name = "upperCodeNm") val upperCodeName: String? = "",    // 상위코드명
        @Json(name = "rgsde") val registerDate: String = "",            // 등록일: yyyy-MM-dd
        @Json(name = "useAtNm") val isUse: String = ""                  // 사용여부: 사용중, 미사용
    )

    // 전체 공통코드 조회 응답
    data class GetCommonCodesResponse(
        val content: List<CommonCodesInfo>  // 공통코드 목록
    )

    // 임시 공통코드 데이터
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