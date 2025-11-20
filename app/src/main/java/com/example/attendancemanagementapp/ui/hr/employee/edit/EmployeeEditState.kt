package com.example.attendancemanagementapp.ui.hr.employee.edit

import com.example.attendancemanagementapp.data.dto.AuthorDTO
import com.example.attendancemanagementapp.data.dto.EmployeeDTO
import com.example.attendancemanagementapp.retrofit.param.PaginationState
import com.example.attendancemanagementapp.ui.hr.employee.manage.DropDownMenu

data class EmployeeEditState(
    val inputData: EmployeeDTO.EmployeeInfo = EmployeeDTO.EmployeeInfo(),   // 입력한 직원 정보(기본 정보, 연봉 정보)
    val dropDownMenu: DropDownMenu = DropDownMenu(),                        // 드롭다운 메뉴 (부서, 직급, 직책)
    val searchText: String = "",                                            // 검색어
    val paginationState: PaginationState = PaginationState(),               // 페이지네이션 상태
    val authors: List<AuthorDTO.GetAuthorsResponse> = emptyList(),          // 전체 권한 목록
    val selectAuthor: List<AuthorDTO.GetAuthorsResponse> = emptyList(),     // 선택한 권한 목록
    val selectDepartmentId: String = "",                                    // 선택한 부서 아이디
    val annualLeaveInfo: List<EmployeeDTO.AnnualLeaveInfo> = listOf(),      // 연차 정보
    val careerInfo: List<EmployeeDTO.CareerInfo> = listOf(),                // 경력 정보
)
