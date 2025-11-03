package com.example.attendancemanagementapp.data.dto

import com.squareup.moshi.Json

object EmployeeDTO {
    /* 직원 목록 데이터 */
    data class EmployeesInfo(
        @Json(name = "userId")  val id: String = "",            // 사용자 아이디
        @Json(name = "userNm")  val name: String = "",          // 이름
        @Json(name = "deptNm")  val department: String = "",    // 부서
        @Json(name = "clsf")    val grade: String = "",         // 직급
        @Json(name = "rspofc")  val title: String? = ""         // 직책
    )

    /* 직원 목록 상세 데이터 */
    data class EmployeeInfo(
        @Json(name = "loginId")     val loginId: String = "",                               // 로그인 아이디
        @Json(name = "userId")      val userId: String = "",                                // 사용자 아이디 (고유 식별자)
        @Json(name = "userNm")      val name: String = "",                                  // 이름
        @Json(name = "deptNm")      val department: String = "",                            // 부서
        @Json(name = "clsf")        val grade: String = "",                                 // 직급
        @Json(name = "rspofc")      val title: String? = "",                                // 직책
        @Json(name = "telno")       val phone: String? = "",                                // 연락처
        @Json(name = "brthdy")      val birthDate: String? = "",                            // 생년월일: yyyy-MM-dd
        @Json(name = "encpn")       val hireDate: String = "",                              // 입사일: yyyy-MM-dd
        @Json(name = "authorNms")   val authors: List<String> = emptyList(),                // 권한 이름 목록
        @Json(name = "useAt")       val isUse: String = "Y",                                // 사용 여부 (Y/N)
        @Json(name = "yrycs")       val annualLeaves: List<AnnualLeaveInfo> = emptyList(),  // 연차 정보 목록
        @Json(name = "crrs")        val careers: List<CareerInfo> = emptyList(),            // 경력 정보 목록
        @Json(name = "anslries")    val salaries: List<SalaryInfo> = emptyList(),           // 연봉 이력 목록
    )

    /* 연차 정보 목록 데이터 */
    data class AnnualLeaveInfo(
        @Json(name = "yrycId")      val id: Int = 0,                // 연차 아이디
        @Json(name = "yryc")        val year: Int = 0,              // 연차 (근무 기간)
        @Json(name = "bgnde")       val startDate: String = "",     // 시작일
        @Json(name = "endde")       val endDate: String = "",       // 종료일
        @Json(name = "yrycCo")      val totalCnt: String = "",      // 연차 개수
        @Json(name = "yrycCyfdCo")  val carryoverCnt: String = "",  // 이월 연차 개수
        @Json(name = "yrycConfmCo") val usedCnt: String = "",       // 사용 연차 개수
    )

    /* 경력 정보 목록 데이터 */
    data class CareerInfo(
        @Json(name = "crrId")   val id: Int? = 0,               // 경력 아이디 (TODO: 직원 정보 수정 API 바뀐거 보고 null로 주면 되는지 확인 필요)
        @Json(name = "cpnNm")   val name: String = "",          // 회사명
        @Json(name = "encpn")   val hireDate: String = "",      // 입사일
        @Json(name = "excpn")   val resignDate: String? = null  // 퇴사일 (현재 재직중이면 null)
    )

    /* 연봉 이력 목록 데이터 */
    data class SalaryInfo(
        @Json(name = "anslryId")    val id: Long?,      // 연봉 아이디
        @Json(name = "year")        val year: String,   // 년도
        @Json(name = "amount")      val amount: Int     // 금액
    )

    /* 직원 관리 목록 데이터 */
    data class ManageEmployeesInfo(
        @Json(name = "userId")  val userId: String = "",        // 사용자 아이디
        @Json(name = "loginId") val loginId: String = "",       // 로그인 아이디
        @Json(name = "userNm")  val name: String = "",          // 이름
        @Json(name = "deptNm")  val department: String = "",    // 부서
        @Json(name = "clsf")    val grade: String = "",         // 직급
        @Json(name = "rspofc")  val title: String? = "",        // 직책
        @Json(name = "encpn")   val hireDate: String = "",      // 입사일: yyyy-MM-dd
        @Json(name = "useAt")   val isUse: String = "Y"         // 사용 여부 (Y/N)
    )

    /* 직원 관리 목록 조회 응답 */
    data class GetManageEmployeesResponse(
        @Json(name = "content")     val content: List<ManageEmployeesInfo>, // 직원 관리 목록
        @Json(name = "totalPages")  val totalPages: Int                     // 총 페이지 개수
    )

    /* 직원 정보 수정 요청 */
    data class UpdateEmployeeRequest(
        @Json(name = "userId")      val userId: String = "",                                        // 사용자 아이디
        @Json(name = "userNm")      val name: String = "",                                          // 이름
        @Json(name = "deptId")      val departmentId: String = "",                                  // 부서 아이디
        @Json(name = "clsf")        val grade: String = "",                                         // 직급
        @Json(name = "rspofc")      val title: String = "",                                         // 직책
        @Json(name = "telno")       val phone: String = "",                                         // 연락처: 000-0000-0000
        @Json(name = "brthdy")      val birthDate: String = "",                                     // 생년월일: yyyy-MM-ddT00:00:00
        @Json(name = "encpn")       val hireDate: String = "",                                      // 입사일: yyyy-MM-ddT00:00:00
        @Json(name = "authorCodes") val authors: List<String> = emptyList(),                        // 권한 코드 목록
        @Json(name = "yrycs")       val annualLeaves: List<UpdateAnnualLeavesInfo> = emptyList(),   // 연차 수정 목록
        @Json(name = "crrs")        val careers: List<CareerInfo> = emptyList(),                    // 경력 정보 목록
        @Json(name = "anslries")    val salaries: List<SalaryInfo> = emptyList(),                   // 연봉
    )

    /* 연차 수정 목록 데이터 */
    data class UpdateAnnualLeavesInfo(
        @Json(name = "yrycId")  val id: Int,
        @Json(name = "yrycCo")  val totalCnt: Double
    )

    /* 직원 등록 요청 */
    data class AddEmployeeRequest(
        @Json(name = "loginId")     val id: String = "",                            // 사용자 아이디
        @Json(name = "deptId")      val departmentId: String = "",                  // 부서 아이디
        @Json(name = "authorCodes") val authors: List<String> = emptyList(),        // 권한 코드 목록
        @Json(name = "userNm")      val name: String = "",                          // 이름
        @Json(name = "encpn")       val hireDate: String = "",                      // 입사일: yyyy-MM-ddT00:00:00
        @Json(name = "brthdy")      val birthDate: String = "",                     // 생년월일: yyyy-MM-ddT00:00:00
        @Json(name = "clsf")        val grade: String = "",                         // 직급
        @Json(name = "rspofc")      val title: String = "",                         // 직책
        @Json(name = "telno")       val phone: String = "",                         // 연락처: 000-0000-0000
        @Json(name = "anslries")    val salaries: List<SalaryInfo> = emptyList(),   // 연봉
    )

    /* 비밀번호 초기화 요청 */
    data class ResetPasswordRequest(
        @Json(name = "userId")  val id: String = "" // 사용자 아이디
    )
}