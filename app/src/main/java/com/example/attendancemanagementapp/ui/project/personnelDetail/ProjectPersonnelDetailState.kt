package com.example.attendancemanagementapp.ui.project.personnelDetail

import com.example.attendancemanagementapp.data.dto.ProjectDTO
import java.time.LocalDate
import java.time.YearMonth

data class ProjectPersonnelDetailState(
    val personnelInfo: ProjectDTO.GetPersonnelDetailResponse = ProjectDTO.GetPersonnelDetailResponse(), // 투입 현황 상세 데이터
    val yearMonth: YearMonth = YearMonth.now(),                                                         // 출력할 월
    val selectedDate: LocalDate = LocalDate.now(),                                                      // 선택한 날짜
    val filteredProjects: List<ProjectDTO.ProjectStatusInfo> = emptyList(),                             // 선택한 날짜 일정
    val openSheet: Boolean = false,                                                                     // 일정 목록 바텀 시트 열림 여부
)