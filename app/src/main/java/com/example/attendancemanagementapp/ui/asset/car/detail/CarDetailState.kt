package com.example.attendancemanagementapp.ui.asset.car.detail

import com.example.attendancemanagementapp.data.dto.CarDTO

data class CarDetailState(
    val carInfo: CarDTO.GetCarResponse = CarDTO.GetCarResponse()
)