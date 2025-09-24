package com.example.attendancemanagementapp.data.dto

import com.squareup.moshi.Json

object HrDTO {
    // 직원 목록 데이터
    data class EmployeesInfo(
        @Json(name = "userId") val id: String = "",          // 사용자 아이디
        @Json(name = "userNm") val name: String = "",        // 이름
        @Json(name = "deptNm") val department: String = "",  // 부서
        @Json(name = "clsf") val grade: String = "",         // 직급
        @Json(name = "rspofc") val title: String? = ""       // 직책
    )

    // 직원 목록 상세 데이터
    data class EmployeeInfo(
        @Json(name = "loginId") val id: String = "",                        // 로그인 아이디
        @Json(name = "userNm") val name: String = "",                       // 이름
        @Json(name = "deptNm") val department: String = "",                 // 부서
        @Json(name = "clsf") val grade: String = "",                        // 직급
        @Json(name = "rspofc") val title: String? = "",                     // 직책
        @Json(name = "telno") val phone: String? = "",                      // 연락처
        @Json(name = "brthdy") val birthDate: String? = "",                 // 생년월일: yyyy-MM-dd
        @Json(name = "encpn") val hireDate: String = "",                    // 입사일: yyyy-MM-dd
        @Json(name = "authorNms") val authors: List<String> = emptyList(),  // 권한 목록
        @Json(name = "amount") val salary: Int = 0                    // 연봉
    )

    // 직원 관리 목록 데이터
    data class ManageEmployeesInfo(
        @Json(name = "userId") val userId: String = "",     // 사용자 아이디
        @Json(name = "loginId") val loginId: String = "",   // 로그인 아이디
        @Json(name = "userNm") val name: String = "",       // 이름
        @Json(name = "deptNm") val department: String = "", // 부서
        @Json(name = "clsf") val grade: String = "",        // 직급
        @Json(name = "rspofc") val title: String? = "",     // 직책
        @Json(name = "encpn") val hireDate: String = ""     // 입사일: yyyy-MM-dd
    )

    // 직원 관리 목록 조회 응답
    data class GetManageEmployeesResponse(
        val content: List<ManageEmployeesInfo>, // 직원 관리 목록
        val totalPages: Int                     // 총 페이지 개수
    )

    // 부서 목록 데이터
    data class DepartmentsInfo(
        @Json(name = "deptId") val id: String = "",             // 부서 아이디
        @Json(name = "deptNm") val name: String = "",           // 부서명
        @Json(name = "dp") val depth: Int = 0,                  // 계층 깊이 (레벨)
        @Json(name = "ordr") val order: Int = 0,                // 동일 레벨 내 순서
        @Json(name = "upperDeptId") val upperId: String? = ""   // 상위 부서 아이디
    )
}