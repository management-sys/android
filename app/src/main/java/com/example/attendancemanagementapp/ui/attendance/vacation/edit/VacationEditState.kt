package com.example.attendancemanagementapp.ui.attendance.vacation.edit

import com.example.attendancemanagementapp.data.dto.VacationDTO
import com.example.attendancemanagementapp.ui.project.add.EmployeeSearchState

data class VacationEditState(
    val inputData: VacationDTO.UpdateVacationRequest = VacationDTO.UpdateVacationRequest(), // 입력 데이터
    val vacationId: String = "",                                                            // 휴가 아이디
    val employeeState: EmployeeSearchState = EmployeeSearchState(),                         // 직원 검색 관련 상태
    val vacationTypeOptions: List<String> = emptyList(),                                    // 휴가 종류 옵션
)