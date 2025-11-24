package com.example.attendancemanagementapp.data.dto

import com.squareup.moshi.Json

object DepartmentDTO {
    /* 부서 목록 데이터 */
    data class DepartmentsInfo(
        @Json(name = "deptId")      val id: String = "",        // 부서 아이디
        @Json(name = "deptLdNm")    val headName: String? = "", // 부서장 이름
        @Json(name = "deptNm")      val name: String = "",      // 부서명
        @Json(name = "dp")          val depth: Int = 0,         // 계층 깊이 (레벨)
        @Json(name = "ordr")        val order: Int = 0,         // 동일 레벨 내 순서
        @Json(name = "upperDeptId") val upperId: String? = ""   // 상위 부서 아이디
    )

    /* 부서 목록 조회 응답 */
    data class GetDepartmentsResponse(
        @Json(name = "content")     val content: List<DepartmentsInfo>, // 부서 목록
        @Json(name = "totalPages")  val totalPages: Int                 // 총 페이지 개수
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

    /* 부서 위치 변경 요청 */
    data class UpdatePositionRequest(
        @Json(name = "newOrder")        val newOrder: Int,      // 새로운 순서
        @Json(name = "newUprDeptId")    val newUpperId: String? // 새로운 상위 부서 아이디
    )

    /* 새로운 부서 등록 요청 */
    data class AddDepartmentRequest(
        @Json(name = "dc")          val description: String? = "",  // 부서 설명
        @Json(name = "deptNm")      val name: String = "",          // 부서명
        @Json(name = "upperDeptId") val upperId: String? = null     // 상위 부서 아이디 (최상위 부서인 경우 null)
    )

    /* 부서 사용자 정보 저장 요청 */
    data class UpdateDepartmentUserRequest(
        @Json(name = "deptId")  val id: String,                 // 부서 아이디
        @Json(name = "users")   val users: List<AddUserInfo>    // 추가할 사용자 목록
    )

    /* 추가할 사용자 목록 데이터 */
    data class AddUserInfo(
        @Json(name = "dprlrAt") val isHead: String = "N",   // 부서장 여부
        @Json(name = "userId")  val userId: String          // 사용자 아이디
    )
}