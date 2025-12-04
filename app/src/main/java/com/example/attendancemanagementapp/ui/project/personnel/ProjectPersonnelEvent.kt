package com.example.attendancemanagementapp.ui.project.personnel

sealed interface ProjectPersonnelEvent {
    /* 초기화ㄱ  */
    data object Init: ProjectPersonnelEvent
}