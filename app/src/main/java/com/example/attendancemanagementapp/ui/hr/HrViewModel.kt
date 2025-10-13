package com.example.attendancemanagementapp.ui.hr

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.attendancemanagementapp.data.dto.HrDTO
import com.example.attendancemanagementapp.data.repository.HrRepository
import com.example.attendancemanagementapp.ui.hr.employee.detail.EmployeeDetailUiState
import com.example.attendancemanagementapp.ui.hr.employee.edit.EmployeeEditEvent
import com.example.attendancemanagementapp.ui.hr.employee.edit.EmployeeEditReducer
import com.example.attendancemanagementapp.ui.hr.employee.edit.EmployeeEditState
import com.example.attendancemanagementapp.ui.hr.employee.manage.EmployeeManageUiState
import com.example.attendancemanagementapp.ui.hr.employee.search.EmployeeSearchUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

enum class Target { MANAGE, SEARCH }
enum class DropDownMenu { DEPARTMENT, GRADE, TITLE }
enum class DepartmentField { NAME, DESCRIPTION }

sealed interface UiEffect {
    data object NavigateBack: UiEffect
}

@HiltViewModel
class HrViewModel @Inject constructor(private val repository: HrRepository) : ViewModel() {
    companion object {
        private const val TAG = "HrViewModel"
    }

    private val _snackbar = MutableSharedFlow<String>(replay = 0, extraBufferCapacity = 1)
    val snackbar = _snackbar.asSharedFlow()
    private val _uiEffects = MutableSharedFlow<UiEffect>(extraBufferCapacity = 1)
    val uiEffects = _uiEffects.asSharedFlow()

    private val _employeeDetailUiState = MutableStateFlow(EmployeeDetailUiState())
    val employeeDetailUiState = _employeeDetailUiState.asStateFlow()
    private val _employeeEditState = MutableStateFlow(EmployeeEditState())
    val employeeEditUiState = _employeeEditState.asStateFlow()
    private val _employeeSearchUiState = MutableStateFlow(EmployeeSearchUiState())
    val employeeSearchUiState = _employeeSearchUiState.asStateFlow()
    private val _employeeManageUiState = MutableStateFlow(EmployeeManageUiState())
    val employeeManageUiState = _employeeManageUiState.asStateFlow()

    init {
        getEmployees()
        getManageEmployees()
        getDepartments()
        getAuthors()
    }

    fun onEvent(e: EmployeeEditEvent) {
        when (e) {
            is EmployeeEditEvent.Init -> {
                val employeeInfo = employeeDetailUiState.value.employeeInfo
                val departments = employeeManageUiState.value.dropDownMenu.departmentMenu

                _employeeEditState.update { EmployeeEditReducer.reduce(it, EmployeeEditEvent.InitWith(employeeInfo, departments)) }
            }
            is EmployeeEditEvent.ChangedValueWith -> _employeeEditState.update { EmployeeEditReducer.reduce(it, e) }
            is EmployeeEditEvent.ChangedSalaryWith -> _employeeEditState.update { EmployeeEditReducer.reduce(it, e) }
            is EmployeeEditEvent.ChangedSearchWith -> _employeeEditState.update { EmployeeEditReducer.reduce(it, e) }
            is EmployeeEditEvent.ClickedAddSalary -> _employeeEditState.update { EmployeeEditReducer.reduce(it, e) }
            is EmployeeEditEvent.ClickedDeleteSalary -> _employeeEditState.update { EmployeeEditReducer.reduce(it, e) }
            is EmployeeEditEvent.ClickedInitSearch -> {
                _employeeEditState.update { EmployeeEditReducer.reduce(it, e) }
                getDepartments()
            }
            is EmployeeEditEvent.SelectedDepartment -> _employeeEditState.update { EmployeeEditReducer.reduce(it, e) }
            is EmployeeEditEvent.ClickedEditAuth -> _employeeEditState.update { EmployeeEditReducer.reduce(it, e) }
            is EmployeeEditEvent.ClickedInitBrth -> _employeeEditState.update { EmployeeEditReducer.reduce(it, e) }
            is EmployeeEditEvent.ClickedSearch -> searchDepartment()
            is EmployeeEditEvent.ClickedUpdate -> updateEmployee()
            else -> {
                _employeeEditState.update { s ->
                    EmployeeEditReducer.reduce(s, e)
                }
            }
        }
    }

    /* 전화번호 형식 포맷팅 */
    private fun formatPhone(d: String): String {
        val s = d.filter(Char::isDigit).take(11)
        return when {
            s.length <= 3 -> s
            s.length <= 7 -> "${s.substring(0,3)}-${s.substring(3)}"
            else -> "${s.substring(0,3)}-${s.substring(3,7)}-${s.substring(7)}"
        }
    }

    /* 부서, 직급, 직책 출력 포맷팅: 부서/직급/직책 */
    fun formatDeptGradeTitle(department: String, grade: String, title: String?): String {
        if (title != "") {
            return "${department}/${grade}/${title}"
        }
        else {
            return "${department}/${grade}"
        }
    }

    /* 검색어 상태 초기화 */
    fun initSearchState(target: Target) {
        when (target) {
            Target.MANAGE -> {
                _employeeManageUiState.value = EmployeeManageUiState(dropDownMenu = _employeeManageUiState.value.dropDownMenu)
                getManageEmployees()
            }
            Target.SEARCH -> {
                _employeeSearchUiState.value = EmployeeSearchUiState()
                getEmployees()
            }
        }
    }

    /* 검색어 입력 이벤트 */
    fun onSearchTextChange(target: Target, input: String) {
        when (target) {
            Target.MANAGE -> { _employeeManageUiState.update { it.copy(searchText = input) } }
            Target.SEARCH -> { _employeeSearchUiState.update { it.copy(searchText = input) } }
        }
    }

    /* 드롭다운 선택 이벤트 */
    fun onSelectDropDown(menu: DropDownMenu, selected: String) {
        when(menu) {
            DropDownMenu.DEPARTMENT -> _employeeManageUiState.update { state ->
                state.copy(
                    dropDownUiState = state.dropDownUiState.copy(
                        department = selected
                    ), currentPage = 0
                )
            }
            DropDownMenu.GRADE -> _employeeManageUiState.update { state ->
                state.copy(
                    dropDownUiState = state.dropDownUiState.copy(
                        grade = selected
                    ), currentPage = 0
                )
            }
            DropDownMenu.TITLE -> _employeeManageUiState.update { state ->
                state.copy(
                    dropDownUiState = state.dropDownUiState.copy(
                        title = selected
                    ), currentPage = 0
                )
            }
        }

        getManageEmployees()
    }

    /* 직원 목록 조회 */
    fun getEmployees() {
        viewModelScope.launch {
            repository.getEmployees(_employeeSearchUiState.value.searchText).collect { result ->
                result
                    .onSuccess { employees ->
                        _employeeSearchUiState.update { it.copy(employees = employees) }
                        Log.d(TAG, "직원 목록 조회 성공: 검색(${_employeeSearchUiState.value.searchText})\n${employees}")
                    }
                    .onFailure { e ->
                        e.printStackTrace()
                    }
            }
        }
    }

    /* 직원 상세 조회 */
    fun getEmployeeDetail(target: Target, userId: String) {
        viewModelScope.launch {
            repository.getEmployeeDetail(userId).collect { result ->
                result
                    .onSuccess { employeeInfo ->
                        when (target) {
                            Target.SEARCH -> { _employeeSearchUiState.update { it.copy(employeeInfo = employeeInfo) } }
                            Target.MANAGE -> { _employeeDetailUiState.update { it.copy(employeeInfo = employeeInfo) } }
                        }
                        Log.d(TAG, "직원 목록 상세 조회 성공: ${userId}\n${employeeInfo}")
                    }
                    .onFailure { e ->
                        e.printStackTrace()
                    }
            }
        }
    }

    /* 직원 관리 목록 조회 */
    fun getManageEmployees() {
        val state = employeeManageUiState.value

        viewModelScope.launch {
            _employeeManageUiState.update { it.copy(isLoading = true) }

            repository.getManageEmployees(
                department = state.dropDownUiState.department,
                grade = state.dropDownUiState.grade,
                title = state.dropDownUiState.title,
                name = state.searchText,
                page = state.currentPage
            ).collect { result ->
                result
                    .onSuccess { data ->
                        if (state.currentPage == 0) {
                            _employeeManageUiState.update { it.copy(employees = data.content, currentPage = it.currentPage + 1, totalPage = data.totalPages, isLoading = false) }
                        }
                        else {
                            _employeeManageUiState.update { it.copy(employees = it.employees + data.content, currentPage = it.currentPage + 1, isLoading = false) }
                        }
                        Log.d(TAG, "직원 관리 목록 조회 성공: ${state.currentPage + 1}/${data.totalPages}, 검색(${state.dropDownUiState.department}, ${state.dropDownUiState.grade}, ${state.dropDownUiState.title}, ${state.searchText})\n${data.content}")
                    }
                    .onFailure { e ->
                        e.printStackTrace()
                    }
            }
        }
    }

    /* 직원 정보 수정 */
    fun updateEmployee() {
        val inputData = employeeEditUiState.value.inputData
        val request = HrDTO.UpdateEmployeeRequest(
            userId = inputData.userId,
            name = inputData.name,
            departmentId = employeeEditUiState.value.selectDepartmentId, // 부서 아이디
            grade = inputData.grade,
            title = if (inputData.title == "직책") "" else inputData.title!!,
            phone = formatPhone(inputData.phone ?: ""), // 전화번호 형식으로 포맷팅 (000-0000-0000)
            birthDate = if (inputData.birthDate.isNullOrBlank()) "" else inputData.birthDate + "T00:00:00",
            hireDate = inputData.hireDate + "T00:00:00",
            authors = employeeEditUiState.value.selectAuthor.map { it.code },
            salaries = inputData.salaries.filter { it.year != "" && it.amount != 0 } // 연봉 정보를 입력하지 않았으면 제거
        )

        Log.d(TAG, "직원 정보 수정 요청\n${request}")
        viewModelScope.launch {
            repository.updateEmployee(request).collect { result ->
                result
                    .onSuccess { result ->
                        _employeeDetailUiState.update { it.copy(employeeInfo = result) }
                        Log.d(TAG, "직원 정보 수정 성공: ${result}")
                        _snackbar.emit("수정이 완료되었습니다")
                        _uiEffects.emit(UiEffect.NavigateBack)
                    }
                    .onFailure { e ->
                        e.printStackTrace()
                    }
            }
        }
    }

    /* 부서 목록 조회 */
    fun getDepartments() {
        viewModelScope.launch {
            repository.getDepartments().collect { result ->
                result
                    .onSuccess { departments ->
                        _employeeManageUiState.update { state -> state.copy(
                            dropDownMenu = state.dropDownMenu.copy(
                                departmentMenu = state.dropDownMenu.departmentMenu + departments
                            ))
                        }
                        Log.d(TAG, "부서 목록 조회 성공\n${departments}")
                    }
                    .onFailure { e ->
                        e.printStackTrace()
                    }
            }
        }
    }

    /* TODO: 부서 검색 (API 추가 되면 구현) */
    fun searchDepartment() {

    }

    /* 권한 목록 조회 */
    fun getAuthors() {
        viewModelScope.launch {
            repository.getAuthors().collect { result ->
                result
                    .onSuccess { authors ->
                        _employeeEditState.update { it.copy(authors = authors) }
                        Log.d(TAG, "권한 목록 조회 성공\n${authors}")
                    }
                    .onFailure { e ->
                        e.printStackTrace()
                    }
            }
        }
    }
}