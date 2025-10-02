package com.example.attendancemanagementapp.data.dto

import com.squareup.moshi.Json

object HrDTO {
    /* 직원 목록 데이터 */
    data class EmployeesInfo(
        @Json(name = "userId") val id: String = "",          // 사용자 아이디
        @Json(name = "userNm") val name: String = "",        // 이름
        @Json(name = "deptNm") val department: String = "",  // 부서
        @Json(name = "clsf") val grade: String = "",         // 직급
        @Json(name = "rspofc") val title: String? = ""       // 직책
    )

    /* 직원 목록 상세 데이터 */
    data class EmployeeInfo(
        @Json(name = "loginId") val id: String = "",                            // 로그인 아이디
        @Json(name = "userId") val userId: String = "",                         // 사용자 아이디
        @Json(name = "userNm") val name: String = "",                           // 이름
        @Json(name = "deptNm") val department: String = "",                     // 부서
        @Json(name = "clsf") val grade: String = "",                            // 직급
        @Json(name = "rspofc") val title: String? = "",                         // 직책
        @Json(name = "telno") val phone: String? = "",                          // 연락처
        @Json(name = "brthdy") val birthDate: String? = "",                     // 생년월일: yyyy-MM-dd
        @Json(name = "encpn") val hireDate: String = "",                        // 입사일: yyyy-MM-dd
        @Json(name = "authorNms") val authors: List<String> = emptyList(),      // 권한 이름 목록
        @Json(name = "anslries") val salaries: List<SalaryInfo> = emptyList(),  // 연봉
        @Json(name = "useAt") val isUse: String = "Y"                           // 사용 여부 (Y/N)
    )

    /* 연봉 이력 목록 데이터 */
    data class SalaryInfo(
        @Json(name = "anslryId") val id: Long?, // 연봉 아이디
        val year: String,                       // 년도
        val amount: Int                         // 금액
    )

    /* 직원 관리 목록 데이터 */
    data class ManageEmployeesInfo(
        @Json(name = "userId") val userId: String = "",     // 사용자 아이디
        @Json(name = "loginId") val loginId: String = "",   // 로그인 아이디
        @Json(name = "userNm") val name: String = "",       // 이름
        @Json(name = "deptNm") val department: String = "", // 부서
        @Json(name = "clsf") val grade: String = "",        // 직급
        @Json(name = "rspofc") val title: String? = "",     // 직책
        @Json(name = "encpn") val hireDate: String = ""     // 입사일: yyyy-MM-dd
    )

    /* 직원 관리 목록 조회 응답 */
    data class GetManageEmployeesResponse(
        val content: List<ManageEmployeesInfo>, // 직원 관리 목록
        val totalPages: Int                     // 총 페이지 개수
    )

    /* 직원 정보 수정 요청 */
    data class UpdateEmployeeRequest(
        @Json(name = "userId") val userId: String = "",                         // 사용자 아이디
        @Json(name = "userNm") val name: String = "",                           // 이름
        @Json(name = "deptId") val departmentId: String = "",                     // 부서 아이디
        @Json(name = "clsf") val grade: String = "",                            // 직급
        @Json(name = "rspofc") val title: String = "",                         // 직책
        @Json(name = "telno") val phone: String = "",                          // 연락처: 000-0000-0000
        @Json(name = "brthdy") val birthDate: String = "",                     // 생년월일: yyyy-MM-ddT00:00:00
        @Json(name = "encpn") val hireDate: String = "",                        // 입사일: yyyy-MM-ddT00:00:00
        @Json(name = "authorCodes") val authors: List<String> = emptyList(),    // 권한 코드 목록
        @Json(name = "anslries") val salaries: List<SalaryInfo> = emptyList(),  // 연봉
    )

    /* 부서 목록 데이터 */
    data class DepartmentsInfo(
        @Json(name = "deptId") val id: String = "",             // 부서 아이디
        @Json(name = "deptNm") val name: String = "",           // 부서명
        @Json(name = "dp") val depth: Int = 0,                  // 계층 깊이 (레벨)
        @Json(name = "ordr") val order: Int = 0,                // 동일 레벨 내 순서
        @Json(name = "upperDeptId") val upperId: String? = ""   // 상위 부서 아이디
    )
}