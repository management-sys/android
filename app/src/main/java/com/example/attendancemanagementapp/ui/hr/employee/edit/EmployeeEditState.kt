package com.example.attendancemanagementapp.ui.hr.employee.edit

import com.example.attendancemanagementapp.data.dto.AuthorDTO
import com.example.attendancemanagementapp.data.dto.DepartmentDTO
import com.example.attendancemanagementapp.data.dto.EmployeeDTO
import com.example.attendancemanagementapp.retrofit.param.PaginationState
import com.example.attendancemanagementapp.ui.hr.employee.manage.DropDownMenu

data class EmployeeEditState(
    val inputData: EmployeeDTO.EmployeeInfo = EmployeeDTO.EmployeeInfo(),   // 입력한 직원 정보
    val dropDownMenu: DropDownMenu = DropDownMenu(),                        // 드롭다운 메뉴 (부서, 직급, 직책)
    val searchText: String = "",                                            // 검색어
    val paginationState: PaginationState = PaginationState(),               // 페이지네이션 상태
    val authors: List<AuthorDTO.GetAuthorsResponse> = emptyList(),          // 전체 권한 목록
    val selectAuthor: List<AuthorDTO.GetAuthorsResponse> = emptyList(),     // 선택한 권한 목록
    val selectDepartmentId: String = "",                                    // 선택한 부서 아이디
    //    val annualLeaveInfo: List<String> = listOf(),    // 연차 정보
    val annualLeaveInfo: List<String> = listOf( // 연차 정보 테스트 (연차, 시작일, 종료일, 연차 개수, 이월 연차 개수, 사용 연차 개수
        "0", "2025-07-21", "2026-07-20", "2", "0", "0"
    ),
//    val careerInfo: List<String> = listOf(), // 경력 정보
    val careerInfo: List<EmployeeDTO.CareerInfo> = listOf(  // 경력 정보 테스트 (회사명, 입사일, 퇴사일, 기간)
        EmployeeDTO.CareerInfo(id = 0, name = "테이큰소프트", hireDate = "2025-07-21", resignDate = ""),
        EmployeeDTO.CareerInfo(id = 1, name = "영남대", hireDate = "2024-01-01", resignDate = "2025-06-30"),
//        "테이큰소프트", "2025-07-21", "", "3개월 (재직중)"
    )
)
