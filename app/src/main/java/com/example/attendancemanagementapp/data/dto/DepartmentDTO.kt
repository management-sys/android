package com.example.attendancemanagementapp.data.dto

import com.squareup.moshi.Json

object DepartmentDTO {
    /* 부서 목록 데이터 */
    data class DepartmentsInfo(
        @Json(name = "deptId")      val id: String = "",        // 부서 아이디
        @Json(name = "deptNm")      val name: String = "",      // 부서명
        @Json(name = "dp")          val depth: Int = 0,         // 계층 깊이 (레벨)
        @Json(name = "ordr")        val order: Int = 0,         // 동일 레벨 내 순서
        @Json(name = "upperDeptId") val upperId: String? = ""   // 상위 부서 아이디
    )

    /* 부서 정보 상세 데이터 */
    data class DepartmentInfo(
        @Json(name = "deptId")      val id: String = "",                                // 부서 아이디
        @Json(name = "deptNm")      val name: String = "",                              // 부서명
        @Json(name = "dc")          val description: String? = "",                      // 부서 설명
        @Json(name = "upperDeptNm") val upperName: String? = "",                        // 상위 부서명
        @Json(name = "users")       val users: List<DepartmentUserInfo> = emptyList()   // 부서 사용자 목록
    )

    /* 부서 사용자 목록 데이터 */
    data class DepartmentUserInfo(
        @Json(name = "userId")  val id: String = "",        // 사용자 아이디
        @Json(name = "userNm")  val name: String = "",      // 이름
        @Json(name = "clsf")    val grade: String = "",     // 직급
        @Json(name = "rspofc")  val title: String = "",     // 직책
        @Json(name = "dprlrAt") val isHead: String = "N"    // 부서장 여부
    )

    /* 부서 정보 수정 요청 */
    data class UpdateDepartmentRequest(
        @Json(name = "deptNm")  val name: String,       // 부서명
        @Json(name = "dc")      val description: String // 부서 설명
    )
}