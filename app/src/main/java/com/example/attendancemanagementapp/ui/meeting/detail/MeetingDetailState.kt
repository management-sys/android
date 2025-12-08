package com.example.attendancemanagementapp.ui.meeting.detail

import com.example.attendancemanagementapp.data.dto.MeetingDTO

data class MeetingDetailState(
    val info: MeetingDTO.GetMeetingResponse = MeetingDTO.GetMeetingResponse(),   // 회의록 정보
    val projectId: String = ""                                                          // 프로젝트 아이디
)