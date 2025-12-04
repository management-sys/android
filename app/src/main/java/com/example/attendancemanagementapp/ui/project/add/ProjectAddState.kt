package com.example.attendancemanagementapp.ui.project.add

import com.example.attendancemanagementapp.data.dto.DepartmentDTO
import com.example.attendancemanagementapp.data.dto.EmployeeDTO
import com.example.attendancemanagementapp.data.dto.ProjectDTO
import com.example.attendancemanagementapp.retrofit.param.PaginationState

data class ProjectAddState(
    val inputData: ProjectDTO.AddProjectRequest = ProjectDTO.AddProjectRequest(),   // 입력 데이터
    val departmentName: String = "",                                                // 담당부서명
    val managerName: String = "",                                                   // 프로젝트 책임자 이름
    val departmentState: DepartmentSearchState = DepartmentSearchState(),           // 부서 검색 관련 상태
    val employeeState: EmployeeSearchState = EmployeeSearchState(),                 // 직원 검색 관련 상태
    val projectTypeOptions: List<String> = emptyList(),                             // 프로젝트 구분 옵션
    val personnelTypeOptions: List<String> = emptyList()                            // 투입인력 담당 옵션
)

/* 부서 검색 관련 상태 */
data class DepartmentSearchState(
    val departments: List<DepartmentDTO.DepartmentsInfo> = emptyList(), // 부서 목록
    val searchText: String = "",                                        // 부서 검색어
    val paginationState: PaginationState = PaginationState()            // 페이지네이션 상태
)

/* 직원 검색 관련 상태 */
data class EmployeeSearchState(
    val employees: List<EmployeeDTO.ManageEmployeesInfo> = emptyList(),   // 직원 목록
    val searchText: String = "",                                    // 직원 검색어
    val paginationState: PaginationState = PaginationState()        // 페이지네이션 상태
)