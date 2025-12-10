package com.example.attendancemanagementapp.data.param

/* 프로젝트 현황 조회에서 사용하는 쿼리 데이터 */
data class ProjectStatusQuery(
    val year: Int = 0,                                                      // 년
    val month: Int = 0,                                                     // 월
    val departmentId: String = "",                                          // 부서 아이디
    val searchType: ProjectStatusSearchType = ProjectStatusSearchType.ALL,  // 검색 종류
    val searchText: String = ""                                             // 검색어
)

/* 프로젝트 현황 검색 종류 (wire: 서버 전달할 키, label: 출력용 라벨) */
enum class ProjectStatusSearchType(private val wire: String, val label: String) {
    ALL("all", "전체"),
    PROJECT_NM("prjctNm", "프로젝트명"),
    PM("PM", "책임자명");

    fun toKey(): String {
        return wire
    }

    companion object {
        fun fromLabel(label: String): ProjectStatusSearchType {
            return values().find { it.label == label } ?: ALL
        }
    }
}