package com.example.attendancemanagementapp.ui.project.status

enum class ProjectStatusField {
    YEAR, MONTH, DEPARTMENT, KEYWORD
}

sealed interface ProjectStatusEvent {
    // 초기화
    data object Init: ProjectStatusEvent

    // 검색어 필드 값 변경 이벤트
    data class ChangedSearchTextWith(
        val value: String
    ): ProjectStatusEvent

    // 검색어 초기화 버튼 클릭 이벤트
    data object ClickedInitSearchText: ProjectStatusEvent

    // 프로젝트 검색 필터 값 선택 이벤트
    data class SelectedSearchFilterWith(
        val field: ProjectStatusField,
        val value: String
    ): ProjectStatusEvent

    // 정보 조회할 프로젝트 선택 이벤트
    data class SelectedProjectWith(
        val projectId: String
    ): ProjectStatusEvent
}