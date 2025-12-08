package com.example.attendancemanagementapp.ui.meeting.add

import com.example.attendancemanagementapp.data.dto.MeetingDTO
import com.example.attendancemanagementapp.data.dto.ProjectDTO.AssignedPersonnelInfo
import com.example.attendancemanagementapp.ui.project.add.EmployeeSearchState

data class MeetingAddState(
    val inputData: MeetingDTO.AddMeetingRequest = MeetingDTO.AddMeetingRequest(),   // 입력 데이터
    val projectName: String = "",                                                   // 프로젝트명
    val employeeState: EmployeeSearchState = EmployeeSearchState(),                 // 직원 검색 관련 상태
)

// 회의록 상세 화면 내용 없는 것들 '없음'으로 표시
// 회의록 등록, 수정 화면 직원 검색 안됨