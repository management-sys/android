package com.example.attendancemanagementapp.ui.project

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.attendancemanagementapp.data.repository.CommonCodeRepository
import com.example.attendancemanagementapp.data.repository.DepartmentRepository
import com.example.attendancemanagementapp.data.repository.EmployeeRepository
import com.example.attendancemanagementapp.data.repository.ProjectRepository
import com.example.attendancemanagementapp.retrofit.param.SearchType
import com.example.attendancemanagementapp.ui.base.UiEffect
import com.example.attendancemanagementapp.ui.project.add.ProjectAddEvent
import com.example.attendancemanagementapp.ui.project.add.ProjectAddReducer
import com.example.attendancemanagementapp.ui.project.add.ProjectAddState
import com.example.attendancemanagementapp.ui.project.detail.ProjectDetailEvent
import com.example.attendancemanagementapp.ui.project.detail.ProjectDetailReducer
import com.example.attendancemanagementapp.ui.project.detail.ProjectDetailState
import com.example.attendancemanagementapp.ui.project.add.ProjectAddSearchField
import com.example.attendancemanagementapp.ui.project.personnel.ProjectPersonnelEvent
import com.example.attendancemanagementapp.ui.project.personnel.ProjectPersonnelReducer
import com.example.attendancemanagementapp.ui.project.personnel.ProjectPersonnelState
import com.example.attendancemanagementapp.ui.project.status.ProjectStatusCnt
import com.example.attendancemanagementapp.ui.project.status.ProjectStatusEvent
import com.example.attendancemanagementapp.ui.project.status.ProjectStatusReducer
import com.example.attendancemanagementapp.ui.project.status.ProjectStatusState
import com.example.attendancemanagementapp.util.ErrorHandler
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

enum class ProjectTarget { PERSONNEL, STATUS }

@HiltViewModel
class ProjectViewModel @Inject constructor(private val projectRepository: ProjectRepository, private val employeeRepository: EmployeeRepository, private val departmentRepository: DepartmentRepository, private val commonCodeRepository: CommonCodeRepository) : ViewModel() {
    companion object {
        private const val TAG = "ProjectViewModel"
    }

    private val _uiEffect = MutableSharedFlow<UiEffect>(extraBufferCapacity = 1)
    val uiEffect = _uiEffect.asSharedFlow()

    private val _projectAddState = MutableStateFlow(ProjectAddState())
    val projectAddState = _projectAddState.asStateFlow()
    private val _projectDetailState = MutableStateFlow(ProjectDetailState())
    val projectDetailState = _projectDetailState.asStateFlow()
    private val _projectPersonnelState = MutableStateFlow(ProjectPersonnelState())
    val projectPersonnelState = _projectPersonnelState.asStateFlow()
    private val _projectStatusState = MutableStateFlow(ProjectStatusState())
    val projectStatusState = _projectStatusState.asStateFlow()

    fun onAddEvent(e: ProjectAddEvent) {
        _projectAddState.update { ProjectAddReducer.reduce(it, e) }

        when (e) {
            ProjectAddEvent.Init -> {
                getEmployees()
                getDepartments()
                getProjectType()
                getPersonnelType()
            }
            ProjectAddEvent.ClickedAdd -> addProject()
            is ProjectAddEvent.ClickedSearchWith -> {
                when (e.field) {
                    ProjectAddSearchField.DEPARTMENT -> getDepartments()
                    ProjectAddSearchField.EMPLOYEE -> getEmployees()
                }
            }
            is ProjectAddEvent.ClickedSearchInitWith -> {
                when (e.field) {
                    ProjectAddSearchField.DEPARTMENT -> getDepartments()
                    ProjectAddSearchField.EMPLOYEE -> getEmployees()
                }
            }
            is ProjectAddEvent.LoadNextPage -> {
                when (e.field) {
                    ProjectAddSearchField.DEPARTMENT -> getDepartments()
                    ProjectAddSearchField.EMPLOYEE -> getEmployees()
                }
            }
            else -> Unit
        }
    }

    fun onDetailEvent(e: ProjectDetailEvent) {
        _projectDetailState.update { ProjectDetailReducer.reduce(it, e) }

        when (e) {
            else -> Unit
        }
    }

    fun onPersonnelEvent(e: ProjectPersonnelEvent) {
        _projectPersonnelState.update { ProjectPersonnelReducer.reduce(it, e) }

        when (e) {
            ProjectPersonnelEvent.Init -> getAllDepartments(target = ProjectTarget.PERSONNEL)
        }
    }

    fun onStatusEvent(e: ProjectStatusEvent) {
        _projectStatusState.update { ProjectStatusReducer.reduce(it, e) }

        when (e) {
            ProjectStatusEvent.InitFirst -> {
                getAllDepartments(target = ProjectTarget.STATUS)
                getProjectStatus()
            }
            ProjectStatusEvent.LoadNextPage -> getProjectStatus()
            ProjectStatusEvent.ClickedSearch -> getProjectStatus()
            ProjectStatusEvent.ClickedInitSearchText -> getProjectStatus()
            is ProjectStatusEvent.ClickedProjectWith -> {
                getProject(e.id)
                _uiEffect.tryEmit(UiEffect.Navigate("projectDetail"))
            }
            ProjectStatusEvent.ClickedInitFilter -> getProjectStatus()
            is ProjectStatusEvent.ClickedUseFilter -> getProjectStatus()
            else -> Unit
        }
    }

    /* 공통코드 목록에서 프로젝트 구분 조회 */
    fun getProjectType() {
        viewModelScope.launch {
            commonCodeRepository.getCommonCodes(
                searchType = SearchType.UPPER_CODE,
                searchKeyword = "WEB_DEV",
                page = 0    // 개수가 더 늘어나면 페이지 관리도 해야함 지금은 필요 없음
            ).collect { result ->
                result
                    .onSuccess { data ->
                        val projectTypeNames: List<String> = data.content.map { it.codeName }
                        _projectAddState.update { it.copy(projectTypeOptions = projectTypeNames) }

                        Log.d(TAG, "[getProjectType] 프로젝트 구분 목록 조회 성공\n${projectTypeNames}")
                    }
                    .onFailure { e ->
                        ErrorHandler.handle(e, TAG, "getProjectType")
                    }
            }
        }
    }

    /* 공통코드 목록에서 투입인력 담당 조회 */
    fun getPersonnelType() {
        viewModelScope.launch {
            commonCodeRepository.getCommonCodes(
                searchType = SearchType.UPPER_CODE,
                searchKeyword = "TASK_TYPE_DEV",
                page = 0    // 개수가 더 늘어나면 페이지 관리도 해야함 지금은 필요 없음
            ).collect { result ->
                result
                    .onSuccess { data ->
                        val personnelTypeNames: List<String> = data.content.map { it.codeName }
                        _projectAddState.update { it.copy(personnelTypeOptions = personnelTypeNames) }

                        Log.d(TAG, "[getPersonnelType] 투입인력 담당 목록 조회 성공\n${personnelTypeNames}")
                    }
                    .onFailure { e ->
                        ErrorHandler.handle(e, TAG, "getPersonnelType")
                    }
            }
        }
    }

    /* 프로젝트 등록 */
    fun addProject() {
        val request = projectAddState.value.inputData
        Log.d(TAG, "[addProject] 프로젝트 등록 요청\n${request}")

        viewModelScope.launch {
            projectRepository.addProject(request = request).collect { result ->
                result
                    .onSuccess { data ->
                        _uiEffect.emit(UiEffect.ShowToast("등록이 완료되었습니다"))
                        _uiEffect.emit(UiEffect.NavigateBack)

                        Log.d(TAG, "[addProject] 프로젝트 등록 성공\n${data}")
                    }
                    .onFailure { e ->
                        ErrorHandler.handle(e, TAG, "addProject")
                    }
            }
        }
    }

    /* 프로젝트 상세 조회 */
    fun getProject(projectId: String) {
        viewModelScope.launch {
            projectRepository.getProject(projectId = projectId).collect { result ->
                result
                    .onSuccess { data ->
                        _projectDetailState.update { it.copy(projectInfo = data) }

                        Log.d(TAG, "[getProject] 프로젝트 상세 조회 성공\n${data}")
                    }
                    .onFailure { e ->
                        ErrorHandler.handle(e, TAG, "getProject")
                    }
            }
        }
    }

    /* 직원 목록 조회 */
    fun getEmployees() {
        val state = projectAddState.value.employeeState

        viewModelScope.launch {
            _projectAddState.update { it.copy(employeeState = it.employeeState.copy(paginationState = it.employeeState.paginationState.copy(isLoading = true))) }

            employeeRepository.getManageEmployees(
                department = "",
                grade = "",
                title = "",
                name = state.searchText,
                page = state.paginationState.currentPage
            ).collect { result ->
                result
                    .onSuccess { data ->
                        if (state.paginationState.currentPage == 0) {
                            _projectAddState.update {
                                it.copy(
                                    employeeState = it.employeeState.copy(
                                        employees = data.content,
                                        paginationState = it.employeeState.paginationState.copy(
                                            currentPage = it.employeeState.paginationState.currentPage + 1,
                                            totalPage = data.totalPages,
                                            isLoading = false
                                        )
                                    )
                                )
                            }
                        }
                        else {
                            _projectAddState.update {
                                it.copy(
                                    employeeState = it.employeeState.copy(
                                        employees = it.employeeState.employees + data.content,
                                        paginationState = it.employeeState.paginationState.copy(
                                            currentPage = it.employeeState.paginationState.currentPage + 1,
                                            isLoading = false
                                        )
                                    )
                                )
                            }
                        }

                        Log.d(TAG, "[getEmployees] 직원 목록 조회 성공: ${state.paginationState.currentPage + 1}/${data.totalPages}, 검색(${projectAddState.value.employeeState.searchText})\n${data.content}")
                    }
                    .onFailure { e ->
                        ErrorHandler.handle(e, TAG, "getEmployees")
                    }
            }
        }
    }

    /* 부서 목록 조회 */
    fun getDepartments() {
        viewModelScope.launch {
            val state = projectAddState.value.departmentState

            _projectAddState.update {
                it.copy(
                    departmentState = it.departmentState.copy(
                        paginationState = it.departmentState.paginationState.copy(isLoading = true)
                    )
                )
            }

            departmentRepository.getDepartments(
                searchName = state.searchText,
                page = state.paginationState.currentPage
            ).collect { result ->
                result
                    .onSuccess { data ->
                        if (state.paginationState.currentPage == 0) {
                            _projectAddState.update {
                                it.copy(
                                    departmentState = it.departmentState.copy(
                                        departments = data.content,
                                        paginationState = it.departmentState.paginationState.copy(
                                            currentPage = it.departmentState.paginationState.currentPage + 1,
                                            totalPage = data.totalPages,
                                            isLoading = false
                                        )
                                    )
                                )
                            }
                        } else {
                            _projectAddState.update {
                                it.copy(
                                    departmentState = it.departmentState.copy(
                                        departments = it.departmentState.departments + data.content,
                                        paginationState = it.departmentState.paginationState.copy(
                                            currentPage = it.departmentState.paginationState.currentPage + 1,
                                            isLoading = false
                                        )
                                    )
                                )
                            }
                        }

                        Log.d(TAG, "[getDepartments] 부서 검색 성공: ${state.paginationState.currentPage + 1}/${data.totalPages}, 검색(${state.searchText})\n${data.content}")
                    }
                    .onFailure { e ->
                        ErrorHandler.handle(e, TAG, "getDepartments")
                    }
            }
        }
    }

    /* 전체 부서 조회 */
    fun getAllDepartments(target: ProjectTarget) {
        viewModelScope.launch {
            departmentRepository.getAllDepartments().collect { result ->
                result
                    .onSuccess { departments ->
                        when (target) {
                            ProjectTarget.PERSONNEL -> _projectPersonnelState.update { it.copy(departments = departments) }
                            ProjectTarget.STATUS -> _projectStatusState.update { it.copy(departments = departments) }
                        }

                        Log.d(TAG, "[getAllDepartments] 전체 부서 조회 성공\n${departments}")
                    }
                    .onFailure { e ->
                        ErrorHandler.handle(e, TAG, "getAllDepartments")
                    }
            }
        }
    }

    /* 프로젝트 투입 인력 목록 조회 */
    fun getPersonnel() {
//        viewModelScope.launch {
//            projectRepository.getPersonnel(
//                projectId = TODO(), // 여기에 프로젝트 아이디 들어가는게 아닌거 같은데
//                page = projectPersonnelState.value.paginationState.currentPage
//            ).collect { result ->
//                result
//                    .onSuccess {
//
//
//                        Log.d(TAG, "[getPersonnel] 프로젝트 투입 인력 목록 조회 성공")
//                    }
//                    .onFailure { e ->
//                        ErrorHandler.handle(e, TAG, "getPersonnel")
//                    }
//            }
//        }
    }

    /* 프로젝트 현황 조회 */
    fun getProjectStatus() {
        val state = projectStatusState.value

        viewModelScope.launch {
            _projectStatusState.update { it.copy(paginationState = it.paginationState.copy(isLoading = true)) }

            projectRepository.getProjectStatus(
                query = state.filter,
                page = state.paginationState.currentPage
            ).collect { result ->
                result
                    .onSuccess { data ->
                        if (state.paginationState.currentPage == 0) {
                            _projectStatusState.update { it.copy(
                                projects = data.projects.content,
                                cntStatus = ProjectStatusCnt(
                                    total = data.totalCnt ?: 0,
                                    inProgress = data.inProgressCnt ?: 0,
                                    notStarted = data.notStartCnt ?: 0,
                                    completed = data.completeCnt ?: 0
                                ),
                                paginationState = it.paginationState.copy(
                                    currentPage = it.paginationState.currentPage + 1,
                                    totalPage = data.projects.totalpages,
                                    isLoading = false
                                ))
                            }
                        }
                        else {
                            _projectStatusState.update { it.copy(
                                projects = it.projects + data.projects.content,
                                cntStatus = ProjectStatusCnt(
                                    total = data.totalCnt ?: 0,
                                    inProgress = data.inProgressCnt ?: 0,
                                    notStarted = data.notStartCnt ?: 0,
                                    completed = data.completeCnt ?: 0
                                ),
                                paginationState = it.paginationState.copy(
                                    currentPage = it.paginationState.currentPage + 1,
                                    isLoading = false
                                )
                            ) }
                        }

                        Log.d(TAG, "[getProjectStatus] 프로젝트 현황 조회 성공: ${state.paginationState.currentPage + 1}/${data.projects.totalpages}, 검색(${state.filter.year}년/${state.filter.month}월/${state.filter.departmentId}/${state.filter.searchType.label})\n${data.projects.content}")
                    }
                    .onFailure { e ->
                        ErrorHandler.handle(e, TAG, "getProjectStatus")
                    }
            }
        }
    }
}