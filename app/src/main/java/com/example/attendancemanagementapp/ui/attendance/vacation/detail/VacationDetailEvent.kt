package com.example.attendancemanagementapp.ui.attendance.vacation.detail

import android.content.Context

sealed interface VacationDetailEvent {
    // 휴가 신청 삭제 버튼 클릭 이벤트
    data object ClickedDelete: VacationDetailEvent

    // 휴가 신청 취소 버튼 클릭 이벤트
    data object ClickedCancel: VacationDetailEvent

    // 휴가 신청서 다운로드 버튼 클릭 이벤트
    data class ClickedDownloadWith(
        val context: Context
    ): VacationDetailEvent

    // 휴가 신청 수정 버튼 클릭 이벤트
    data object ClickedUpdate: VacationDetailEvent
}