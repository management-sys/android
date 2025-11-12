package com.example.attendancemanagementapp.util

import android.util.Log
import retrofit2.HttpException

object ErrorHandler {
    fun handle(e: Throwable, tag: String, method: String) {
        when (e) {
            is HttpException -> {
                val errorBody = e.response()?.errorBody()?.string()
                Log.e(tag, "[${method}] 서버 오류: ${e.code()} ${e.message()}\n서버 응답 내용: $errorBody")
            }
            else -> {
                Log.e(tag, "[${method}] 네트워크/기타 오류: ${e.message}", e)
            }
        }
    }
}