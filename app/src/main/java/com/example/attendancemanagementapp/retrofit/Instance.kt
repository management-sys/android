package com.example.attendancemanagementapp.retrofit

import com.example.attendancemanagementapp.retrofit.service.AuthorService
import com.example.attendancemanagementapp.retrofit.service.CommonCodeService
import com.example.attendancemanagementapp.retrofit.service.DepartmentService
import com.example.attendancemanagementapp.retrofit.service.EmployeeService
import com.squareup.moshi.KotlinJsonAdapterFactory
import com.squareup.moshi.Moshi
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory

object RetrofitInstance {
    private const val BASE_URL = "http://10.0.2.2:8080/"   // BASE URL 설정 (localhost)

    val moshi: Moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(ScalarsConverterFactory.create())
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .build()

    val authorService: AuthorService by lazy { retrofit.create(AuthorService::class.java) }
    val commonCodeService: CommonCodeService by lazy { retrofit.create(CommonCodeService::class.java) }
    val departmentService: DepartmentService by lazy { retrofit.create(DepartmentService::class.java) }
    val employeeService: EmployeeService by lazy { retrofit.create(EmployeeService::class.java) }
}