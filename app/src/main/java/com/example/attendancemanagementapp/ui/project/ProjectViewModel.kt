package com.example.attendancemanagementapp.ui.project

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.attendancemanagementapp.data.dto.ProjectDTO
import com.example.attendancemanagementapp.data.repository.CommonCodeRepository
import com.example.attendancemanagementapp.data.repository.DepartmentRepository
import com.example.attendancemanagementapp.data.repository.EmployeeRepository
import com.example.attendancemanagementapp.data.repository.ProjectRepository
import com.example.attendancemanagementapp.retrofit.param.SearchType
import com.example.attendancemanagementapp.ui.base.UiEffect
import com.example.attendancemanagementapp.ui.project.add.DepartmentSearchState
import com.example.attendancemanagementapp.ui.project.add.EmployeeSearchState
import com.example.attendancemanagementapp.ui.project.add.ProjectAddEvent
import com.example.attendancemanagementapp.ui.project.add.ProjectAddReducer
import com.example.attendancemanagementapp.ui.project.add.ProjectAddState
import com.example.attendancemanagementapp.ui.project.detail.ProjectDetailEvent
import com.example.attendancemanagementapp.ui.project.detail.ProjectDetailReducer
import com.example.attendancemanagementapp.ui.project.detail.ProjectDetailState
import com.example.attendancemanagementapp.ui.project.add.ProjectAddSearchField
import com.example.attendancemanagementapp.ui.project.edit.ProjectEditEvent
import com.example.attendancemanagementapp.ui.project.edit.ProjectEditReducer
import com.example.attendancemanagementapp.ui.project.edit.ProjectEditState
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

enum class ProjectTarget { ADD, EDIT, PERSONNEL, STATUS }

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
    private val _projectEditState = MutableStateFlow(ProjectEditState())
    val projectEditState = _projectEditState.asStateFlow()
    private val _projectPersonnelState = MutableStateFlow(ProjectPersonnelState())
    val projectPersonnelState = _projectPersonnelState.asStateFlow()
    private val _projectStatusState = MutableStateFlow(ProjectStatusState())
    val projectStatusState = _projectStatusState.asStateFlow()

    fun onAddEvent(e: ProjectAddEvent) {
        _projectAddState.update { ProjectAddReducer.reduce(it, e) }

        when (e) {
            ProjectAddEvent.Init -> {
                getEmployees(ProjectTarget.ADD)
                getDepartments(ProjectTarget.ADD)
                getProjectType(ProjectTarget.ADD)
                getPersonnelType(ProjectTarget.ADD)
            }
            ProjectAddEvent.ClickedAdd -> addProject()
            is ProjectAddEvent.ClickedSearchWith -> {
                when (e.field) {
                    ProjectAddSearchField.DEPARTMENT -> getDepartments(ProjectTarget.ADD)
                    ProjectAddSearchField.EMPLOYEE -> getEmployees(ProjectTarget.ADD)
                }
            }
            is ProjectAddEvent.ClickedSearchInitWith -> {
                when (e.field) {
                    ProjectAddSearchField.DEPARTMENT -> getDepartments(ProjectTarget.ADD)
                    ProjectAddSearchField.EMPLOYEE -> getEmployees(ProjectTarget.ADD)
                }
            }
            is ProjectAddEvent.LoadNextPage -> {
                when (e.field) {
                    ProjectAddSearchField.DEPARTMENT -> getDepartments(ProjectTarget.ADD)
                    ProjectAddSearchField.EMPLOYEE -> getEmployees(ProjectTarget.ADD)
                }
            }
            else -> Unit
        }
    }

    fun onDetailEvent(e: ProjectDetailEvent){
        _projectDetailState.update { ProjectDetailReducer.reduce(it, e) }

        when (e) {
            ProjectDetailEvent.ClickedDelete -> deleteProject()
            ProjectDetailEvent.ClickedStop -> stopProject()
            else -> Unit
        }
    }

    fun onEditEvent(e: ProjectEditEvent) {
        _projectEditState.update { ProjectEditReducer.reduce(it, e) }

        when (e) {
            is ProjectEditEvent.Init -> {
                getEmployees(ProjectTarget.EDIT)
                getDepartments(ProjectTarget.EDIT)
                getProjectType(ProjectTarget.EDIT)
                getPersonnelType(ProjectTarget.EDIT)
                onEditEvent(ProjectEditEvent.InitWith(projectDetailState.value.projectInfo))
            }
            ProjectEditEvent.ClickedUpdate -> updateProject()
            is ProjectEditEvent.ClickedSearchWith -> {
                when (e.field) {
                    ProjectAddSearchField.DEPARTMENT -> getDepartments(ProjectTarget.EDIT)
                    ProjectAddSearchField.EMPLOYEE -> getEmployees(ProjectTarget.EDIT)
                }
            }
            is ProjectEditEvent.ClickedSearchInitWith -> {
                when (e.field) {
                    ProjectAddSearchField.DEPARTMENT -> getDepartments(ProjectTarget.EDIT)
                    ProjectAddSearchField.EMPLOYEE -> getEmployees(ProjectTarget.EDIT)
                }
            }
            is ProjectEditEvent.LoadNextPage -> {
                when (e.field) {
                    ProjectAddSearchField.DEPARTMENT -> getDepartments(ProjectTarget.EDIT)
                    ProjectAddSearchField.EMPLOYEE -> getEmployees(ProjectTarget.EDIT)
                }
            }
            else -> Unit
        }
    }

    fun onPersonnelEvent(e: ProjectPersonnelEvent) {
        _projectPersonnelState.update { ProjectPersonnelReducer.reduce(it, e) }

        when (e) {
            ProjectPersonnelEvent.Init -> {
                getAllDepartments(target = ProjectTarget.PERSONNEL)
                getPersonnels()
            }
            ProjectPersonnelEvent.LoadNextPage -> getPersonnels()
            ProjectPersonnelEvent.ClickedSearch -> getPersonnels()
            ProjectPersonnelEvent.ClickedInitSearchText -> getPersonnels()
            is ProjectPersonnelEvent.ClickedPersonnelWith -> {
                _uiEffect.tryEmit(UiEffect.Navigate(""))
            }
            ProjectPersonnelEvent.ClickedInitFilter -> getPersonnels()
            is ProjectPersonnelEvent.ClickedUseFilter -> getPersonnels()
            else -> Unit
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
    fun getProjectType(target: ProjectTarget) {
        viewModelScope.launch {
            commonCodeRepository.getCommonCodes(
                searchType = SearchType.UPPER_CODE,
                searchKeyword = "WEB_DEV",
                page = 0    // 개수가 더 늘어나면 페이지 관리도 해야함 지금은 필요 없음
            ).collect { result ->
                result
                    .onSuccess { data ->
                        val projectTypeNames: List<String> = data.content.map { it.codeName }

                        when (target) {
                            ProjectTarget.ADD -> _projectAddState.update { it.copy(projectTypeOptions = projectTypeNames) }
                            ProjectTarget.EDIT -> _projectEditState.update { it.copy(projectTypeOptions = projectTypeNames) }
                            else -> Unit
                        }

                        Log.d(TAG, "[getProjectType-${target}] 프로젝트 구분 목록 조회 성공\n${projectTypeNames}")
                    }
                    .onFailure { e ->
                        ErrorHandler.handle(e, TAG, "getProjectType-${target}")
                    }
            }
        }
    }

    /* 공통코드 목록에서 투입인력 담당 조회 */
    fun getPersonnelType(target: ProjectTarget) {
        viewModelScope.launch {
            commonCodeRepository.getCommonCodes(
                searchType = SearchType.UPPER_CODE,
                searchKeyword = "TASK_TYPE_DEV",
                page = 0    // 개수가 더 늘어나면 페이지 관리도 해야함 지금은 필요 없음
            ).collect { result ->
                result
                    .onSuccess { data ->
                        val personnelTypeNames: List<String> = data.content.map { it.codeName }

                        when (target) {
                            ProjectTarget.ADD -> _projectAddState.update { it.copy(personnelTypeOptions = personnelTypeNames) }
                            ProjectTarget.EDIT -> _projectEditState.update { it.copy(personnelTypeOptions = personnelTypeNames) }
                            else -> Unit
                        }

                        Log.d(TAG, "[getPersonnelType-${target}] 투입인력 담당 목록 조회 성공\n${personnelTypeNames}")
                    }
                    .onFailure { e ->
                        ErrorHandler.handle(e, TAG, "getPersonnelType-${target}")
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
    fun getEmployees(target: ProjectTarget) {
        val state = when (target) {
            ProjectTarget.ADD -> projectAddState.value.employeeState
            ProjectTarget.EDIT -> projectEditState.value.employeeState
            else -> EmployeeSearchState()
        }

        val updateState: (EmployeeSearchState) -> Unit = { newState ->
            when (target) {
                ProjectTarget.ADD -> _projectAddState.update { it.copy(employeeState = newState) }
                ProjectTarget.EDIT -> _projectEditState.update { it.copy(employeeState = newState) }
                else -> Unit
            }
        }

        updateState(state.copy(paginationState = state.paginationState.copy(isLoading = true)))

        viewModelScope.launch {
            employeeRepository.getManageEmployees(
                department = "",
                grade = "",
                title = "",
                name = state.searchText,
                page = state.paginationState.currentPage
            ).collect { result ->
                result
                    .onSuccess { data ->
                        val isFirstPage = state.paginationState.currentPage == 0

                        val updatedEmployees = if (isFirstPage) data.content else state.employees + data.content

                        updateState(state.copy(
                            employees = updatedEmployees,
                            paginationState = state.paginationState.copy(
                                currentPage = state.paginationState.currentPage + 1,
                                totalPage = data.totalPages,
                                isLoading = false
                            )
                        ))

                        Log.d(TAG, "[getEmployees-${target}] 직원 목록 조회 성공: ${state.paginationState.currentPage + 1}/${data.totalPages}, 검색(${projectAddState.value.employeeState.searchText})\n${data.content}")
                    }
                    .onFailure { e ->
                        updateState(state.copy(paginationState = state.paginationState.copy(isLoading = false)))

                        ErrorHandler.handle(e, TAG, "getEmployees-${target}")
                    }
            }
        }
    }

    /* 부서 목록 조회 */
    fun getDepartments(target: ProjectTarget) {
        val state = when (target) {
            ProjectTarget.ADD -> projectAddState.value.departmentState
            ProjectTarget.EDIT -> projectEditState.value.departmentState
            else -> DepartmentSearchState()
        }

        val updateState: (DepartmentSearchState) -> Unit = { newState ->
            when (target) {
                ProjectTarget.ADD -> _projectAddState.update { it.copy(departmentState = newState) }
                ProjectTarget.EDIT -> _projectEditState.update { it.copy(departmentState = newState) }
                else -> Unit
            }
        }

        updateState(state.copy(paginationState = state.paginationState.copy(isLoading = true)))

        viewModelScope.launch {
            departmentRepository.getDepartments(
                searchName = state.searchText,
                page = state.paginationState.currentPage
            ).collect { result ->
                result
                    .onSuccess { data ->
                        val isFirstPage = state.paginationState.currentPage == 0

                        val updatedDepartments = if (isFirstPage) data.content else state.departments + data.content

                        updateState(state.copy(
                            departments = updatedDepartments,
                            paginationState = state.paginationState.copy(
                                currentPage = state.paginationState.currentPage + 1,
                                totalPage = data.totalPages,
                                isLoading = false
                            )
                        ))

                        Log.d(TAG, "[getDepartments-${target}] 부서 검색 성공: ${state.paginationState.currentPage + 1}/${data.totalPages}, 검색(${state.searchText})\n${data.content}")
                    }
                    .onFailure { e ->
                        updateState(state.copy(paginationState = state.paginationState.copy(isLoading = false)))

                        ErrorHandler.handle(e, TAG, "getDepartments-${target}")
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
                            else -> Unit
                        }

                        Log.d(TAG, "[getAllDepartments-${target}] 전체 부서 조회 성공\n${departments}")
                    }
                    .onFailure { e ->
                        ErrorHandler.handle(e, TAG, "getAllDepartments-${target}")
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
                        val isFirstPage = state.paginationState.currentPage == 0

                        val updatedProjects = if (isFirstPage) data.projects.content else state.projects + data.projects.content

                        _projectStatusState.update { it.copy(
                            projects = updatedProjects,
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

                        Log.d(TAG, "[getProjectStatus] 프로젝트 현황 조회 성공: ${state.paginationState.currentPage + 1}/${data.projects.totalpages}, 검색(${state.filter.year}년/${state.filter.month}월/${state.filter.departmentId}/${state.filter.searchType.label})\n${data.projects.content}")
                    }
                    .onFailure { e ->
                        _projectStatusState.update { it.copy(paginationState = it.paginationState.copy(isLoading = false)) }

                        ErrorHandler.handle(e, TAG, "getProjectStatus")
                    }
            }
        }
    }

    /* 프로젝트 수정 */
    fun updateProject() {
        val state = projectEditState.value

        viewModelScope.launch {
            projectRepository.updateProject(
                projectId = state.projectId,
                request = state.inputData
            ).collect { result ->
                result
                    .onSuccess { projectInfo ->
                        _projectDetailState.update { it.copy(projectInfo = projectInfo) }

                        _uiEffect.emit(UiEffect.ShowToast("수정이 완료되었습니다"))
                        _uiEffect.emit(UiEffect.NavigateBack)

                        Log.d(TAG, "프로젝트 수정 성공\n${projectInfo}")
                    }
                    .onFailure { e ->
                        ErrorHandler.handle(e, TAG, "updateProject")
                    }
            }
        }
    }

    /* 프로젝트 삭제 */
    fun deleteProject() {
        viewModelScope.launch {
            projectRepository.deleteProject(
                projectId = projectDetailState.value.projectInfo.projectId
            ).collect { result ->
                result
                    .onSuccess {
                        _uiEffect.emit(UiEffect.ShowToast("삭제가 완료되었습니다"))
                        _uiEffect.emit(UiEffect.NavigateBack)

                        Log.d(TAG, "프로젝트 삭제 성공")
                    }
                    .onFailure { e ->
                        ErrorHandler.handle(e, TAG, "deleteProject")
                    }
            }
        }
    }

    /* 프로젝트 중단 */
    fun stopProject() {
        viewModelScope.launch {
            projectRepository.stopProject(
                projectId = projectDetailState.value.projectInfo.projectId
            ).collect { result ->
                result
                    .onSuccess {
                        getProject(projectDetailState.value.projectInfo.projectId)

                        _uiEffect.emit(UiEffect.ShowToast("중단 처리가 완료되었습니다"))

                        Log.d(TAG, "프로젝트 중단 성공")
                    }
                    .onFailure { e ->
                        ErrorHandler.handle(e, TAG, "stopProject")
                    }
            }
        }
    }

    /* 투입 현황 조회 */
    fun getPersonnels() {
        val state = projectPersonnelState.value

        viewModelScope.launch {
            _projectPersonnelState.update { it.copy(paginationState = it.paginationState.copy(isLoading = true)) }

            projectRepository.getPersonnels(
                query = state.filter,
                page = state.paginationState.currentPage
            ).collect { result ->
                result
                    .onSuccess { data ->
                        val isFirstPage = state.paginationState.currentPage == 0

                        val updatedPersonnels = if (isFirstPage) data.personnels else state.personnels + data.personnels

                        _projectPersonnelState.update { it.copy(
                            personnels = updatedPersonnels,
                            paginationState = it.paginationState.copy(
                                currentPage = it.paginationState.currentPage + 1,
                                totalPage = data.totalpages,
                                isLoading = false
                            )
                        ) }

                        Log.d(TAG, "[getPersonnels] 투입 현황 조회 성공: ${state.paginationState.currentPage + 1}/${data.totalpages}, 검색(부서 아이디: ${state.filter.departmentId}/사용자 이름: ${state.filter.userName}/${state.filter.year}년\n${data.personnels}")
                    }
                    .onFailure { e ->
                        _projectPersonnelState.update { it.copy(paginationState = it.paginationState.copy(isLoading = false)) }

                        ErrorHandler.handle(e, TAG, "getPersonnels")
                    }
            }
        }
    }
}