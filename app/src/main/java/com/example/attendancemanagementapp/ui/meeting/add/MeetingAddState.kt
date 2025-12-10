package com.example.attendancemanagementapp.ui.meeting.add

import com.example.attendancemanagementapp.data.dto.MeetingDTO
import com.example.attendancemanagementapp.data.dto.ProjectDTO.AssignedPersonnelInfo
import com.example.attendancemanagementapp.data.dto.ProjectDTO.ProjectStatusInfo
import com.example.attendancemanagementapp.retrofit.param.PaginationState
import com.example.attendancemanagementapp.ui.project.add.EmployeeSearchState

data class MeetingAddState(
    val inputData: MeetingDTO.AddMeetingRequest = MeetingDTO.AddMeetingRequest(),   // 입력 데이터
    val projectName: String = "",                                                   // 프로젝트명
    val employeeState: EmployeeSearchState = EmployeeSearchState(),                 // 직원 검색 관련 상태
    val fixedProject: Boolean = false,                                              // 프로젝트 고정 여부 (프로젝트 상세 화면의 회의록 등록 버튼 클릭: True, 드로어의 회의록 등록 버튼 클릭: False)
    val projectState: ProjectSearchState = ProjectSearchState()                     // 프로젝트 검색 관련 상태
)

/* 프로젝트 검색 관련 데이터 */
data class ProjectSearchState(
    val projects: List<ProjectStatusInfo> = emptyList(),        // 프로젝트 목록
    val searchText: String = "",                                // 검색어
    val paginationState: PaginationState = PaginationState()    // 페이지네이션 상태
)