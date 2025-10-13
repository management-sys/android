package com.example.attendancemanagementapp.ui.util

/* 부서, 직급, 직책 출력 포맷팅: 부서/직급/직책 */
fun formatDeptGradeTitle(department: String, grade: String, title: String?): String {
    if (title != "") {
        return "${department}/${grade}/${title}"
    }
    else {
        return "${department}/${grade}"
    }
}