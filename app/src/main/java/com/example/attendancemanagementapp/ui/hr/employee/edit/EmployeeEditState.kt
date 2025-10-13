package com.example.attendancemanagementapp.ui.hr.employee.edit

import com.example.attendancemanagementapp.data.dto.AuthorDTO
import com.example.attendancemanagementapp.data.dto.EmployeeDTO
import com.example.attendancemanagementapp.ui.hr.employee.manage.DropDownMenu

data class EmployeeEditState(
    val inputData: EmployeeDTO.EmployeeInfo = EmployeeDTO.EmployeeInfo(),           // 입력한 직원 정보
    val dropDownMenu: DropDownMenu = DropDownMenu(),                    // 드롭다운 메뉴
    val searchText: String = "",                                        // 검색어
    val authors: List<AuthorDTO.GetAuthorsResponse> = emptyList(),      // 전체 권한 목록
    val selectAuthor: List<AuthorDTO.GetAuthorsResponse> = emptyList(), // 선택한 권한 목록
    val selectDepartmentId: String = ""                                 // 선택한 부서 아이디
)
