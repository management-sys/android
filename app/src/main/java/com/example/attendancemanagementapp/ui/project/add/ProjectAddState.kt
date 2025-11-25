package com.example.attendancemanagementapp.ui.project.add

import com.example.attendancemanagementapp.data.dto.DepartmentDTO
import com.example.attendancemanagementapp.data.dto.EmployeeDTO
import com.example.attendancemanagementapp.data.dto.ProjectDTO

data class ProjectAddState(
    val inputData: ProjectDTO.AddProjectRequest = ProjectDTO.AddProjectRequest(),   // 입력 데이터
    val departmentName: String = "",                                                // 담당부서명
    val managerName: String = "",                                                   // 프로젝트 책임자 이름
    val checkedAssignedPersonnel: List<EmployeeDTO.EmployeesInfo> = emptyList(),    // 투입 인력 리스트
    val employees: List<EmployeeDTO.EmployeesInfo> = emptyList(),                   // 직원 목록
    val departments: List<DepartmentDTO.DepartmentsInfo> = emptyList()              // 부서 목록
)