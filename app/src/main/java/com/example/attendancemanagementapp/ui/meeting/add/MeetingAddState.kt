package com.example.attendancemanagementapp.ui.meeting.add

import com.example.attendancemanagementapp.data.dto.MeetingDTO
import com.example.attendancemanagementapp.data.dto.ProjectDTO.AssignedPersonnelInfo
import com.example.attendancemanagementapp.ui.project.add.EmployeeSearchState

data class MeetingAddState(
    val inputData: MeetingDTO.AddMeetingRequest = MeetingDTO.AddMeetingRequest(),   // 입력 데이터
    val projectName: String = "",                                                   // 프로젝트명
    val employeeState: EmployeeSearchState = EmployeeSearchState(),                 // 직원 검색 관련 상태
)