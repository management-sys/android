package com.example.attendancemanagementapp.ui.approver.request

import com.example.attendancemanagementapp.data.dto.ApproverDTO
import com.example.attendancemanagementapp.data.param.ApproverQuery
import com.example.attendancemanagementapp.retrofit.param.PaginationState

data class ApproverRequestState(
    val approverRequest: ApproverDTO.GetApproversResponse = ApproverDTO.GetApproversResponse(),   // 결재 요청 목록
    val filter: ApproverQuery = ApproverQuery(),
    val paginationState: PaginationState = PaginationState()            // 페이지네이션 상태
)