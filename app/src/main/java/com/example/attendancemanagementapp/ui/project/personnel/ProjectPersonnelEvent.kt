package com.example.attendancemanagementapp.ui.project.personnel

import com.example.attendancemanagementapp.data.dto.ProjectDTO
import com.example.attendancemanagementapp.data.param.PersonnelsQuery

sealed interface ProjectPersonnelEvent {
    /* 초기화 */
    data object Init: ProjectPersonnelEvent

    /* 필터 초기화 버튼 클릭 이벤트 */
    data object ClickedInitFilter: ProjectPersonnelEvent

    /* 필터 적용 버튼 클릭 이벤트 */
    data class ClickedUseFilter(
        val filter: PersonnelsQuery
    ): ProjectPersonnelEvent

    /* 다음 페이지 조회 이벤트 */
    data object LoadNextPage: ProjectPersonnelEvent

    /* 검색어 필드 값 변경 이벤트 */
    data class ChangedSearchTextWith(
        val value: String
    ): ProjectPersonnelEvent

    /* 검색 버튼 클릭 이벤트 */
    data object ClickedSearch: ProjectPersonnelEvent

    /* 검색어 초기화 버튼 클릭 이벤트 */
    data object ClickedInitSearchText: ProjectPersonnelEvent

    /* 정보 조회할 투입 인력 선택 이벤트 */
    data class ClickedPersonnelWith(
        val personnelInfo: ProjectDTO.PersonnelInfo
    ): ProjectPersonnelEvent
}