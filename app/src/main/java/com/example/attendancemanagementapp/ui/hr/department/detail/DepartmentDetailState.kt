package com.example.attendancemanagementapp.ui.hr.department.detail

import com.example.attendancemanagementapp.data.dto.HrDTO

data class DepartmentDetailState(
    val info: HrDTO.DepartmentInfo = HrDTO.DepartmentInfo(),            // 원본 부서 정보
    val updateInfo: HrDTO.DepartmentInfo = HrDTO.DepartmentInfo(),      // 수정한 부서 정보
    val users: List<HrDTO.DepartmentUserInfo> = emptyList(),            // 부서 사용자 목록
    val selectedHead: Set<Pair<String, String>> = emptySet(),           // 선택한 부서장 목록 (아이디, 이름)
    val selectedSave: Set<String> = emptySet(),                         // 저장할 사용자 목록
    val searchText: String = "",                                        // 검색어
    val employees: List<HrDTO.DepartmentUserInfo> = emptyList(),        // 직원 목록
    val selectedEmployees: List<HrDTO.DepartmentUserInfo> = emptyList() // 추가할 직원 목록
)