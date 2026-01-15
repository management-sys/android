package com.example.attendancemanagementapp.ui.asset.card.add

import com.example.attendancemanagementapp.data.dto.CardDTO
import com.example.attendancemanagementapp.ui.project.add.EmployeeSearchState

data class CardAddState(
    val inputData: CardDTO.AddCardRequest = CardDTO.AddCardRequest(),
    val managerName: String = "",
    val employeeState: EmployeeSearchState = EmployeeSearchState(), // 직원 검색 관련 상태
)