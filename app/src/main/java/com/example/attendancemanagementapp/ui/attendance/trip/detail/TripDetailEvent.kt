package com.example.attendancemanagementapp.ui.attendance.trip.detail

import android.content.Context

sealed interface TripDetailEvent {
    // 출장 신청 삭제 버튼 클릭 이벤트
    data object ClickedDelete: TripDetailEvent

    // 출장 신청 취소 버튼 클릭 이벤트
    data object ClickedCancel: TripDetailEvent

    // 출장 신청 수정 버튼 클릭 이벤트
    data object ClickedUpdate: TripDetailEvent

    // 품의서 다운로드 버튼 클릭 이벤트
    data class ClickedDownloadWith(
        val context: Context
    ): TripDetailEvent
}