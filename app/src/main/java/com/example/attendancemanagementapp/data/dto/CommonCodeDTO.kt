package com.example.attendancemanagementapp.data.dto

import com.squareup.moshi.Json

object CommonCodeDTO {
    /* 공통코드 목록 데이터 */
    data class CommonCodesInfo(
        @Json(name = "code")        val code: String = "",              // 코드
        @Json(name = "codeNm")      val codeName: String = "",          // 코드명
        @Json(name = "upperCode")   val upperCode: String? = "",        // 상위코드
        @Json(name = "upperCodeNm") val upperCodeName: String? = "",    // 상위코드명
        @Json(name = "rgsde")       val registerDate: String = "",      // 등록일: yyyy-MM-dd
        @Json(name = "useAtNm")     val isUse: String = ""              // 사용여부: 사용중, 미사용
    )

    /* 공통코드 상세 데이터 */
    data class CommonCodeInfo(
        @Json(name = "code")        val code: String = "",              // 코드
        @Json(name = "codeNm")      val codeName: String = "",          // 코드명
        @Json(name = "upperCode")   val upperCode: String? = "",        // 상위코드
        @Json(name = "upperCodeNm") val upperCodeName: String? = "",    // 상위코드명
        @Json(name = "codeValue")   val codeValue: String? = "",        // 코드값
        @Json(name = "dc")          val description: String? = "",      // 설명
        @Json(name = "useAtNm")     val isUse: String = ""              // 사용여부
    )

    /* 공통코드 목록 조회 응답 */
    data class GetCommonCodesResponse(
        @Json(name = "content")     val content: List<CommonCodesInfo>, // 공통코드 목록
        @Json(name = "totalPages")  val totalPages: Int                 // 총 페이지 개수
    )

    /* 공통코드 등록, 수정 요청 */
    data class AddUpdateCommonCodeRequest(
        @Json(name = "code")        val code: String,           // 코드
        @Json(name = "codeNm")      val codeName: String,       // 코드명
        @Json(name = "upperCode")   val upperCode: String?,     // 상위코드
        @Json(name = "codeValue")   val codeValue: String?,     // 코드값
        @Json(name = "dc")          val description: String?,   // 설명
        @Json(name = "useAt")       val isUse: String = "Y"     // 사용여부
    )

    /* 공통코드 등록, 수정 응답 */
    data class AddUpdateCommonCodeResponse(
        @Json(name = "code")    val code: String    // 수정된 코드
    )

    /* 공통코드 삭제 응답 */
    data class DeleteCommonCodeResponse(
        @Json(name = "count")   val count: Int  // 삭제된 코드 개수
    )
}