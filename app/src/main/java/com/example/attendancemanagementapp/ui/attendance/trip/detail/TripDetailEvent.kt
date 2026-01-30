package com.example.attendancemanagementapp.ui.attendance.trip.detail

sealed interface TripDetailEvent {
    // 출장 신청 삭제 버튼 클릭 이벤트
    data object ClickedDelete: TripDetailEvent

    // 출장 신청 취소 버튼 클릭 이벤트
    data object ClickedCancel: TripDetailEvent

    // 출장 신청 수정 버튼 클릭 이벤트
    data object ClickedUpdate: TripDetailEvent

    // 품의서 다운로드 버튼 클릭 이벤트
    data object ClickedDownload: TripDetailEvent

    // 복명서 작성 버튼 클릭 이벤트
    data object ClickedAddReport: TripDetailEvent

    // 복명서 신청 수정 버튼 클릭 이벤트
    data object ClickedUpdateReport: TripDetailEvent

    // 복명서 다운로드 버튼 클릭 이벤트
    data object ClickedDownloadReport: TripDetailEvent
}