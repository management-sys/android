package com.example.attendancemanagementapp.ui.home.calendar

import java.time.LocalDate
import java.time.YearMonth

data class CalendarState(
    val yearMonth: YearMonth = YearMonth.now(),                 // 출력할 월
    val selectedDate: LocalDate = LocalDate.now(),              // 선택한 날짜
    val openSheet: Boolean = false,                             // 일정 목록 바텀 시트 열림 여부
    val filteredSchedules: List<ScheduleInfo> = emptyList(),    // 선택한 날짜 일정
    val schedules: List<ScheduleInfo> = listOf(
        ScheduleInfo(
            title = "연말 결산 회의",
            startDateTime = "2025-12-02T10:00:00",
            endDateTime = "2025-12-02T12:00:00",
            type = "회의",
            employeeName = "홍길동",
            employeeGrade = "과장"
        ),
        ScheduleInfo(
            title = "국내 출장 - 부산",
            startDateTime = "2025-12-04T09:00:00",
            endDateTime = "2025-12-04T18:00:00",
            type = "국내 출장",
            employeeName = "이영희",
            employeeGrade = "주임"
        ),
        ScheduleInfo(
            title = "부서 송년회 준비 회의",
            startDateTime = "2025-12-05T14:00:00",
            endDateTime = "2025-12-05T15:30:00",
            type = "회의",
            employeeName = "김철수",
            employeeGrade = "대리"
        ),
        ScheduleInfo(
            title = "무급휴가",
            startDateTime = "2025-12-08T00:00:00",
            endDateTime = "2025-12-08T23:59:00",
            type = "무급휴가",
            employeeName = "박민수",
            employeeGrade = "사원"
        ),
        ScheduleInfo(
            title = "프로젝트 kick-off 회의",
            startDateTime = "2025-12-10T11:00:00",
            endDateTime = "2025-12-10T12:00:00",
            type = "회의",
            employeeName = "정우성",
            employeeGrade = "차장"
        ),
        ScheduleInfo(
            title = "국내 출장 - 대전",
            startDateTime = "2025-12-10T08:30:00",
            endDateTime = "2025-12-12T17:00:00",
            type = "국내 출장",
            employeeName = "최지훈",
            employeeGrade = "과장"
        ),
        ScheduleInfo(
            title = "팀 주간 회의",
            startDateTime = "2025-12-15T10:00:00",
            endDateTime = "2025-12-15T11:30:00",
            type = "회의",
            employeeName = "김하늘",
            employeeGrade = "대리"
        ),
        ScheduleInfo(
            title = "무급휴가",
            startDateTime = "2025-12-18T00:00:00",
            endDateTime = "2025-12-18T23:59:00",
            type = "무급휴가",
            employeeName = "박재현",
            employeeGrade = "사원"
        ),
        ScheduleInfo(
            title = "연말 전체 회의",
            startDateTime = "2025-12-19T15:00:00",
            endDateTime = "2025-12-22T17:00:00",
            type = "회의",
            employeeName = "송지연",
            employeeGrade = "부장"
        ),
        ScheduleInfo(
            title = "국내 출장 - 광주",
            startDateTime = "2025-12-27T09:00:00",
            endDateTime = "2025-12-27T19:00:00",
            type = "국내 출장",
            employeeName = "윤혜진",
            employeeGrade = "대리"
        ),
        ScheduleInfo(
            title = "국내 출장 - 광주2",
            startDateTime = "2025-12-27T09:10:00",
            endDateTime = "2025-12-27T19:10:00",
            type = "국내 출장",
            employeeName = "윤혜진2",
            employeeGrade = "대리3"
        ),
        ScheduleInfo(
            title = "국내 출장 - 광주3",
            startDateTime = "2025-12-27T09:20:00",
            endDateTime = "2025-12-27T19:20:00",
            type = "국내 출장",
            employeeName = "윤혜진2",
            employeeGrade = "대리3"
        ),
        ScheduleInfo(
            title = "국내 출장 - 광주3",
            startDateTime = "2025-12-27T09:20:00",
            endDateTime = "2025-12-27T19:20:00",
            type = "국내 출장",
            employeeName = "윤혜진2",
            employeeGrade = "대리3"
        ),
        ScheduleInfo(
            title = "국내 출장 - 광주3",
            startDateTime = "2025-12-27T09:20:00",
            endDateTime = "2025-12-27T19:20:00",
            type = "국내 출장",
            employeeName = "윤혜진2",
            employeeGrade = "대리3"
        )
    )
)

/* 임시 일정 데이터 클래스 */
data class ScheduleInfo(
    val title: String,          // 제목
    val startDateTime: String,  // 시작 날짜 (yyyy-MM-dd'T'HH:mm:ss)
    val endDateTime: String,    // 끝 날짜 (yyyy-MM-dd'T'HH:mm:ss)
    val type: String,           // 일정 유형 (국내 출장, 무급휴가, 회의)
    val employeeName: String,   // 직원 이름
    val employeeGrade: String,  // 직원 직급 (대표, 인턴, 사원, 주임, 대리, 과장, 차장, 부장, 소장)
)