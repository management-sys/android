package com.example.attendancemanagementapp.data.param

data class ApproverQuery(
    val year: Int? = null,
    val month: Int? = null,
    val approvalType: ApprovalType = ApprovalType.ALL,
    val applicationType: ApplicationType = ApplicationType.ALL
) {
    fun hasFilter(): Boolean {
        return year != null || month != null || approvalType != ApprovalType.ALL || applicationType != ApplicationType.ALL
    }
}

enum class ApprovalType(private val wire: String, val label: String) {
    ALL("", "상태 전체"),
    WAITING("W", "결재대기"),
    APPROVE("A", "승인"),
    REJECT("R", "반려"),
    CANCEL("C", "취소");

    fun toKey(): String {
        return wire
    }

    companion object {
        fun fromLabel(label: String): ApprovalType {
            return ApprovalType.values()
                .find { it.label == label } ?: ApprovalType.ALL
        }
    }
}

enum class ApplicationType(private val wire: String, val label: String) {
    ALL("", "구분 전체"),
    VACATION("VCAT", "휴가"),
    TRIP("BSRP", "출장");

    fun toKey(): String {
        return wire
    }

    companion object {
        fun fromLabel(label: String): ApplicationType {
            return ApplicationType.values()
                .find { it.label == label } ?: ApplicationType.ALL
        }
    }
}