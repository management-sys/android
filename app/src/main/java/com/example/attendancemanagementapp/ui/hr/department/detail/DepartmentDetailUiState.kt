package com.example.attendancemanagementapp.ui.hr.department.detail

import com.example.attendancemanagementapp.data.dto.HrDTO

data class DepartmentDetailUiState(
    val info: DepartmentInfo = DepartmentInfo(upper = "테이큰소프트", name = "개발팀", description = "개발팀입니다."),
    val updateInfo: DepartmentInfo = DepartmentInfo(upper = "테이큰소프트", name = "개발팀", description = "개발팀입니다."),
    val users: List<DepartmentUserInfo> = listOf(
        DepartmentUserInfo(id = "1", name = "팀장2", department = "개발 1팀", grade = "차장", title = "팀장"),
        DepartmentUserInfo(id = "2", name = "사용자1", department = "개발 1팀", grade = "대리", title = "미지정"),
    ),
    val selectedHead: Set<Pair<String, String>> = setOf(
        Pair("1", "팀장2"),
    ),
    val selectedSave: Set<String> = emptySet(), // 저장할 사용자 목록 (아이디)
    val searchText: String = "",                // 검색어
    val employees: List<DepartmentUserInfo> = listOf(   // 직원 목록
        DepartmentUserInfo(id = "1", name = "팀장2", department = "개발 1팀", grade = "차장", title = "팀장"),
        DepartmentUserInfo(id = "2", name = "사용자1", department = "개발 1팀", grade = "대리", title = "미지정"),
        DepartmentUserInfo(id = "3", name = "사용자2", department = "개발 1팀", grade = "주임", title = "미지정"),
        DepartmentUserInfo(id = "4", name = "사용자6", department = "개발 1팀", grade = "과장", title = "미지정")
    ),
    val selectedEmployees: List<DepartmentUserInfo> = emptyList() // 추가할 직원 목록: 기본값 = users

//    val info: DepartmentInfo = DepartmentInfo(),              // 원본 부서 정보
//    val updateInfo: DepartmentInfo = DepartmentInfo(),        // 수정한 부서 정보
//    val users: List<DepartmentUserInfo> = emptyList(),        // 부서 사용자 목록
//    val selectedHead: Set<Pair<String, String>> = emptySet(), // 선택한 부서장 목록 (아이디, 이름)
//    val selectedSave: Set<String> = emptySet(),               // 저장할 사용자 목록
//    val searchText: String,                                   // 검색어
//    val employees: List<DepartmentUserInfo> = emptyList(),    // 직원 목록
//    val selectedEmployees: List<DepartmentUserInfo> = emptyList(),          // 추가할 직원 목록
)

/* 부서 정보 데이터 */
data class DepartmentInfo(
    val upper: String = "",      // 상위부서
    val name: String = "",       // 부서명
    val description: String = "" // 부서설명
)

/* 부서 사용자 데이터 */
data class DepartmentUserInfo(
    val id: String = "",            // 아이디
    val name: String = "",          // 이름
    val department: String = "",    // 부서
    val grade: String = "",         // 직급
    val title: String               // 직책
)

/* 부서, 직급, 직책 출력 포맷팅 */
fun DepartmentUserInfo.formatDeptGradeTitle(): String {
    if (title != null) {
        return "${department}/${grade}/${title}"
    }
    else {
        return "${department}/${grade}"
    }
}