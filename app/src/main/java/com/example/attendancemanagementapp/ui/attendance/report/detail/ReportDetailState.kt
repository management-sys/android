package com.example.attendancemanagementapp.ui.attendance.report.detail

import com.example.attendancemanagementapp.data.dto.TripDTO

data class ReportDetailState(
    val reportInfo: TripDTO.GetTripReportResponse = TripDTO.GetTripReportResponse()
)