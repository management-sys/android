package com.example.attendancemanagementapp.util

import com.example.attendancemanagementapp.data.dto.EmployeeDTO
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.Period
import java.time.format.DateTimeFormatter

/* 부서, 직급, 직책 출력 포맷: 부서/직급/직책 */
fun formatDeptGradeTitle(department: String, grade: String, title: String?): String {
    if (title.isNullOrBlank()) {
        return "${department}/${grade}"
    }
    else {
        return "${department}/${grade}/${title}"
    }
}

/* 전화번호 형식 포맷: 000-0000-0000 */
fun formatPhone(phone: String): String {
    val s = phone.filter(Char::isDigit).take(11)
    return when {
        s.length <= 3 -> s
        s.length <= 7 -> "${s.substring(0,3)}-${s.substring(3)}"
        else -> "${s.substring(0,3)}-${s.substring(3,7)}-${s.substring(7)}"
    }
}

/* 날짜 형식 포맷: yyyy-MM-ddT00:00:00 */
fun formatDateTime(date: String?): String {
    return if (date.isNullOrBlank()) "" else "${date}T00:00:00"
}

/* 날짜 형식 포맷: yy-MM-dd */
fun formatDateYY(dateTime: String): String {
    val dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")
    val dateFormatter = DateTimeFormatter.ofPattern("yy-MM-dd")

    if (dateTime.isBlank()) return ""
    return runCatching {
        LocalDateTime.parse(dateTime, dateTimeFormatter).format(dateFormatter)
    }.getOrDefault("")
}

/* 날짜 형식 포맷: MM월 dd일 HH:mm */
fun formatShowDateTime(dateTime: String): String {
    val dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")
    val dateFormatter = DateTimeFormatter.ofPattern("MM월 dd일 HH:mm")

    if (dateTime.isBlank()) return ""
    return runCatching {
        LocalDateTime.parse(dateTime, dateTimeFormatter).format(dateFormatter)
    }.getOrDefault("")
}

/* 시간 형식 포맷: HH:mm */
fun formatTime(dateTime: String): String {
    val dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")
    val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")

    if (dateTime.isBlank()) return ""
    return runCatching {
        LocalDateTime.parse(dateTime, dateTimeFormatter).format(timeFormatter)
    }.getOrDefault("")
}

/* 재직 기간(경력) 계산 */
fun calculateCareerPeriod(hireDate: String, resignDate: String?): String {
    if (hireDate == "") {   // 경력 아이템 추가 시 입사일이 공백임
        return "-"
    }

    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    val hireDate = LocalDate.parse(hireDate, formatter)
    val resignDate = if (resignDate.isNullOrBlank()) LocalDate.now() else LocalDate.parse(resignDate, formatter)    // 퇴사일이 null이면 오늘 날짜를 기준으로 재직 기간 계산

    val period = Period.between(hireDate, resignDate)
    val years = period.years
    val months = period.months

    return if (years == 0) "${months}개월" else "${years}년 ${months}개월"
}

/* 총 경력 계산 */
fun calculateTotalCareerPeriod(careers: List<EmployeeDTO.CareerInfo>): String {
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

    var totalMonths = 0

    for (career in careers) {
        val hireDate = LocalDate.parse(career.hireDate, formatter)
        val resignDate = if (career.resignDate.isNullOrBlank()) LocalDate.now() else LocalDate.parse(career.resignDate, formatter)

        val period = Period.between(hireDate, resignDate)
        totalMonths += period.years * 12 + period.months
    }

    val totalYears = totalMonths / 12
    val remainingMonths = totalMonths % 12

    return "${totalYears}년 ${remainingMonths}개월"
}

/* 일시 출력 포맷 함수 */
fun formatDateRangeWithTime(startDate: String, endDate: String): String {
    if (startDate.isNullOrBlank() || endDate.isNullOrBlank()) {
        return ""
    }

    val inputFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")
    val outputDateTime = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
    val outputTime = DateTimeFormatter.ofPattern("HH:mm")

    val start = LocalDateTime.parse(startDate, inputFormat)
    val end = LocalDateTime.parse(endDate, inputFormat)

    // 날짜가 같으면 yyyy-MM-dd HH:mm:ss ~ HH:mm:ss 리턴, 아니면 yyyy-MM-dd HH:mm ~ yyyy-MM-dd HH:mm 리턴
    return if (start.toLocalDate() == end.toLocalDate()) {
        "${start.format(outputDateTime)} ~ ${end.format(outputTime)}"
    } else {
        "${start.format(outputDateTime)} ~ ${end.format(outputDateTime)}"
    }
}