package com.example.attendancemanagementapp.retrofit.service

import com.example.attendancemanagementapp.data.dto.DepartmentDTO
import com.example.attendancemanagementapp.data.dto.EmployeeDTO
import retrofit2.http.GET
import retrofit2.http.Path

interface DepartmentService {
    // 부서 목록 조회
    @GET("/api/depts")
    suspend fun getDepartments(): List<DepartmentDTO.DepartmentsInfo>

    // 부서 정보 상세 조회
    @GET("/api/depts/{deptId}")
    suspend fun getDepartmentDetail(
        @Path("deptId") departmentId: String
    ): DepartmentDTO.DepartmentInfo
}