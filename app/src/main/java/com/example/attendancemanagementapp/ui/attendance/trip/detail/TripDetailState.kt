package com.example.attendancemanagementapp.ui.attendance.trip.detail

import com.example.attendancemanagementapp.data.dto.TripDTO

data class TripDetailState(
    val tripInfo: TripDTO.GetTripResponse = TripDTO.GetTripResponse()   // 출장 정보
)