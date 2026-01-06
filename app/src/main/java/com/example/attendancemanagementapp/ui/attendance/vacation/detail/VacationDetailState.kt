package com.example.attendancemanagementapp.ui.attendance.vacation.detail

import com.example.attendancemanagementapp.data.dto.VacationDTO

data class VacationDetailState(
    val vacationInfo: VacationDTO.GetVacationResponse = VacationDTO.GetVacationResponse()   // 휴가 정보
)