package com.example.attendancemanagementapp.ui.asset.card.edit

import com.example.attendancemanagementapp.data.dto.CardDTO
import com.example.attendancemanagementapp.ui.project.add.EmployeeSearchState

data class CardEditState(
    val inputData: CardDTO.AddCardRequest = CardDTO.AddCardRequest(),   // 입력 데이터
    val id: String = "",                                                // 차량정보 아이디
    val managerName: String = "",                                       // 담당자 이름
    val employeeState: EmployeeSearchState = EmployeeSearchState(),     // 직원 검색 관련 상태
)