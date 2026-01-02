package com.example.attendancemanagementapp.ui.project.personnelDetail

import java.time.LocalDate

sealed interface ProjectPersonnelDetailEvent {
    // 초기화
    data object Init: ProjectPersonnelDetailEvent

    // 이전 달 이동 버튼 클릭 이벤트
    data object ClickedPrev: ProjectPersonnelDetailEvent

    // 다음 달 이동 버튼 클릭 이벤트
    data object ClickedNext: ProjectPersonnelDetailEvent

    // 날짜 클릭 이벤트
    data class ClickedDateWith(
        val date: LocalDate
    ): ProjectPersonnelDetailEvent

    // 바텀 시트 닫기 이벤트
    data object ClickedCloseSheet: ProjectPersonnelDetailEvent

    // 프로젝트 선택 이벤트
    data class SelectedProject(
        val projectId: String
    ): ProjectPersonnelDetailEvent
}