package com.example.attendancemanagementapp.ui.project.status

import com.example.attendancemanagementapp.data.param.ProjectStatusQuery

sealed interface ProjectStatusEvent {
    /* 초기화 */
    data object InitFirst: ProjectStatusEvent
    data object InitLast: ProjectStatusEvent

    /* 검색어 필드 값 변경 이벤트 */
    data class ChangedSearchTextWith(
        val value: String
    ): ProjectStatusEvent

    /* 검색 버튼 클릭 이벤트 */
    data object ClickedSearch: ProjectStatusEvent

    /* 검색어 초기화 버튼 클릭 이벤트 */
    data object ClickedInitSearchText: ProjectStatusEvent

    /* 다음 페이지 조회 이벤트 */
    data object LoadNextPage: ProjectStatusEvent

    /* 정보 조회할 프로젝트 선택 이벤트 */
    data class ClickedProjectWith(
        val id: String
    ): ProjectStatusEvent

    /* 필터 초기화 버튼 클릭 이벤트 */
    data object ClickedInitFilter: ProjectStatusEvent

    /* 필터 적용 버튼 클릭 이벤트 */
    data class ClickedUseFilter(
        val filter: ProjectStatusQuery
    ): ProjectStatusEvent
}