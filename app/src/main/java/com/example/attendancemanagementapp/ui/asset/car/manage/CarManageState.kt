package com.example.attendancemanagementapp.ui.asset.car.manage

import com.example.attendancemanagementapp.data.dto.CarDTO
import com.example.attendancemanagementapp.data.dto.EmployeeDTO
import com.example.attendancemanagementapp.retrofit.param.PaginationState

data class CarManageState(
    val cars: List<CarDTO.CarsInfo> = emptyList(),              // 차량 목록
    val searchText: String = "",                                // 차량 검색어
    val type: String = "전체",                                   // 검색 키워드
)
