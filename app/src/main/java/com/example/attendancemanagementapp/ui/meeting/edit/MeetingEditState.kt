package com.example.attendancemanagementapp.ui.meeting.edit

import com.example.attendancemanagementapp.data.dto.MeetingDTO
import com.example.attendancemanagementapp.ui.meeting.add.ProjectEmployeeSearchState

data class MeetingEditState(
    val inputData: MeetingDTO.UpdateMeetingRequest = MeetingDTO.UpdateMeetingRequest(), // 입력 데이터
    val projectId: String = "",                                                         // 프로젝트 아이디
    val projectName: String = "",                                                       // 프로젝트명
    val meetingId: Long = 0,                                                            // 회의록 아이디
    val employeeState: ProjectEmployeeSearchState = ProjectEmployeeSearchState(),       // 직원 검색 관련 상태
)