package com.example.attendancemanagementapp.ui.hr

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.attendancemanagementapp.data.repository.HrRepository
import com.example.attendancemanagementapp.ui.hr.department.manage.DepartmentManageUiState
import com.example.attendancemanagementapp.ui.hr.employee.detail.EmployeeDetailUiState
import com.example.attendancemanagementapp.ui.hr.employee.manage.EmployeeManageUiState
import com.example.attendancemanagementapp.ui.hr.employee.search.EmployeeSearchUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

enum class Target { MANAGE, SEARCH }
enum class DropDownMenu { DEPARTMENT, GRADE, TITLE }

@HiltViewModel
class HrViewModel @Inject constructor(private val repository: HrRepository) : ViewModel() {
    companion object {
        private const val TAG = "HrViewModel"
    }

    private val _employeeDetailUiState = MutableStateFlow(EmployeeDetailUiState())
    val employeeDetailUiState = _employeeDetailUiState.asStateFlow()
    private val _employeeSearchUiState = MutableStateFlow(EmployeeSearchUiState())
    val employeeSearchUiState = _employeeSearchUiState.asStateFlow()
    private val _employeeManageUiState = MutableStateFlow(EmployeeManageUiState())
    val employeeManageUiState = _employeeManageUiState.asStateFlow()
    private val _departmentManageUiState = MutableStateFlow(DepartmentManageUiState())
    val departmentManageUiState = _departmentManageUiState.asStateFlow()

    init {
        getEmployees()
        getManageEmployees()
        getDepartments()
    }

    /* 부서, 직급, 직책 출력 포맷팅 */
    fun formatDeptGradeTitle(department: String, grade: String, title: String?): String {
        if (title != null) {
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
                _employeeManageUiState.value = EmployeeManageUiState()
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
        val state = _employeeManageUiState.value

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

    /* 부서 목록 조회 */
    fun getDepartments() {
        viewModelScope.launch {
            repository.getDepartments().collect { result ->
                result
                    .onSuccess { departments ->
                        _employeeManageUiState.update { state ->
                            state.copy(
                                dropDownMenu = state.dropDownMenu.copy(
                                    departmentMenu = state.dropDownMenu.departmentMenu + departments
                                )
                            )
                        }
                        _departmentManageUiState.update { it.copy(departments = it.departments + departments) }
                        Log.d(TAG, "부서 목록 조회 성공\n${departments}")
                    }
                    .onFailure { e ->
                        e.printStackTrace()
                    }
            }
        }
    }
}