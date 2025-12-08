package com.example.attendancemanagementapp.ui.meeting.edit

import com.example.attendancemanagementapp.data.dto.MeetingDTO
import com.example.attendancemanagementapp.data.dto.ProjectDTO.AssignedPersonnelInfo
import com.example.attendancemanagementapp.ui.project.add.EmployeeSearchState

data class MeetingEditState(
    val inputData: MeetingDTO.UpdateMeetingRequest = MeetingDTO.UpdateMeetingRequest(), // 입력 데이터
    val projectName: String = "",                                                       // 프로젝트명
    val meetingId: Long = 0,                                                            // 회의록 아이디
    val employeeState: EmployeeSearchState = EmployeeSearchState(),                     // 직원 검색 관련 상태
)