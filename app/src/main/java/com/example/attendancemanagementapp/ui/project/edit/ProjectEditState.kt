package com.example.attendancemanagementapp.ui.project.edit

import com.example.attendancemanagementapp.data.dto.ProjectDTO
import com.example.attendancemanagementapp.ui.project.add.DepartmentSearchState
import com.example.attendancemanagementapp.ui.project.add.EmployeeSearchState

data class ProjectEditState(
    val inputData: ProjectDTO.UpdateProjectRequest = ProjectDTO.UpdateProjectRequest(), // 입력 데이터
    val projectId: String = "",                                                         // 프로젝트 아이디
    val departmentName: String = "",                                                    // 담당부서명
    val managerName: String = "",                                                       // 프로젝트 책임자 이름
    val departmentState: DepartmentSearchState = DepartmentSearchState(),               // 부서 검색 관련 상태
    val employeeState: EmployeeSearchState = EmployeeSearchState(),                     // 직원 검색 관련 상태
    val projectTypeOptions: List<String> = emptyList(),                                 // 프로젝트 구분 옵션
    val personnelTypeOptions: List<String> = emptyList(),                               // 투입인력 담당 옵션
)