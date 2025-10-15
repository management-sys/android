package com.example.attendancemanagementapp.ui.util

/* 부서, 직급, 직책 출력 포맷팅: 부서/직급/직책 */
fun formatDeptGradeTitle(department: String, grade: String, title: String?): String {
    if (title.isNullOrBlank()) {
        return "${department}/${grade}"
    }
    else {
        return "${department}/${grade}/${title}"
    }
}

/* 전화번호 형식 포맷팅: 000-0000-0000 */
fun formatPhone(d: String): String {
    val s = d.filter(Char::isDigit).take(11)
    return when {
        s.length <= 3 -> s
        s.length <= 7 -> "${s.substring(0,3)}-${s.substring(3)}"
        else -> "${s.substring(0,3)}-${s.substring(3,7)}-${s.substring(7)}"
    }
}