package com.example.attendancemanagementapp.ui.hr.employee

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.attendancemanagementapp.data.dto.DepartmentDTO
import com.example.attendancemanagementapp.data.dto.EmployeeDTO
import com.example.attendancemanagementapp.data.repository.AuthorRepository
import com.example.attendancemanagementapp.data.repository.CommonCodeRepository
import com.example.attendancemanagementapp.data.repository.DepartmentRepository
import com.example.attendancemanagementapp.data.repository.EmployeeRepository
import com.example.attendancemanagementapp.retrofit.param.SearchType
import com.example.attendancemanagementapp.ui.base.UiEffect
import com.example.attendancemanagementapp.ui.base.UiEffect.ShowToast
import com.example.attendancemanagementapp.ui.hr.employee.add.EmployeeAddEvent
import com.example.attendancemanagementapp.ui.hr.employee.add.EmployeeAddReducer
import com.example.attendancemanagementapp.ui.hr.employee.add.EmployeeAddState
import com.example.attendancemanagementapp.ui.hr.employee.detail.EmployeeDetailEvent
import com.example.attendancemanagementapp.ui.hr.employee.detail.EmployeeDetailReducer
import com.example.attendancemanagementapp.ui.hr.employee.detail.EmployeeDetailState
import com.example.attendancemanagementapp.ui.hr.employee.edit.EmployeeEditEvent
import com.example.attendancemanagementapp.ui.hr.employee.edit.EmployeeEditReducer
import com.example.attendancemanagementapp.ui.hr.employee.edit.EmployeeEditState
import com.example.attendancemanagementapp.ui.hr.employee.manage.EmployeeManageEvent
import com.example.attendancemanagementapp.ui.hr.employee.manage.EmployeeManageReducer
import com.example.attendancemanagementapp.ui.hr.employee.manage.EmployeeManageState
import com.example.attendancemanagementapp.ui.hr.employee.search.EmployeeSearchEvent
import com.example.attendancemanagementapp.ui.hr.employee.search.EmployeeSearchReducer
import com.example.attendancemanagementapp.ui.hr.employee.search.EmployeeSearchState
import com.example.attendancemanagementapp.ui.util.formatPhone
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

enum class EmployeeTarget { MANAGE, SEARCH }

@HiltViewModel
class EmployeeViewModel @Inject constructor(
    private val employeeRepository: EmployeeRepository,
    private val departmentRepository: DepartmentRepository,
    private val commonCodeRepository: CommonCodeRepository,
    private val authorRepository: AuthorRepository
) : ViewModel() {
    companion object {
        private const val TAG = "HrViewModel"
    }

    private val _uiEffect = MutableSharedFlow<UiEffect>(extraBufferCapacity = 1)
    val uiEffect = _uiEffect.asSharedFlow()

    private val _employeeAddState = MutableStateFlow(EmployeeAddState())
    val employeeAddState = _employeeAddState.asStateFlow()
    private val _employeeDetailState = MutableStateFlow(EmployeeDetailState())
    val employeeDetailState = _employeeDetailState.asStateFlow()
    private val _employeeEditState = MutableStateFlow(EmployeeEditState())
    val employeeEditState = _employeeEditState.asStateFlow()
    private val _employeeSearchState = MutableStateFlow(EmployeeSearchState())
    val employeeSearchState = _employeeSearchState.asStateFlow()
    private val _employeeManageState = MutableStateFlow(EmployeeManageState())
    val employeeManageState = _employeeManageState.asStateFlow()

    private val _currentPage = MutableStateFlow(0) // 현재 탭 페이지 번호
    val currentPage = _currentPage.asStateFlow()

    init {
        getEmployees()
        getManageEmployees()
        getAllDepartments()
        getAuthors()
        getGradeTitle()
    }

    fun onAddEvent(e: EmployeeAddEvent) {
        _employeeAddState.update { EmployeeAddReducer.reduce(it, e) }

        when (e) {
            is EmployeeAddEvent.Init -> {
                val departments = employeeManageState.value.dropDownMenu.departmentMenu.filter { it.name != "부서" }
                _employeeAddState.update { EmployeeAddReducer.reduce(it, EmployeeAddEvent.InitWith(departments)) }
            }
            is EmployeeAddEvent.ClickedInitSearch -> searchDepartment(isEdit = false)
            is EmployeeAddEvent.ClickedSearch -> searchDepartment(isEdit = false)
            is EmployeeAddEvent.ClickedAdd -> addEmployee()
            is EmployeeAddEvent.LoadNextPage -> searchDepartment(isEdit = false)
            else -> Unit
        }
    }

    fun onDetailEvent(e: EmployeeDetailEvent) {
        _employeeDetailState.update { EmployeeDetailReducer.reduce(it, e) }

        when (e) {
            is EmployeeDetailEvent.ClickedResetPassword -> resetPassword()
            EmployeeDetailEvent.ClickedDeactivate -> setDeactivate()
            EmployeeDetailEvent.ClickedDismissDeactivate -> _uiEffect.tryEmit(ShowToast("사용자 탈퇴가 취소되었습니다."))
            EmployeeDetailEvent.ClickedActivate -> setActivate()
            EmployeeDetailEvent.ClickedDismissActivate -> _uiEffect.tryEmit(ShowToast("사용자 복구가 취소되었습니다."))
            is EmployeeDetailEvent.ChangedPage -> _currentPage.value = e.page
        }
    }

    fun onEditEvent(e: EmployeeEditEvent) {
        _employeeEditState.update { EmployeeEditReducer.reduce(it, e) }

        when (e) {
            is EmployeeEditEvent.Init -> {
                val employeeInfo = employeeDetailState.value.employeeInfo
                val departments = employeeManageState.value.dropDownMenu.departmentMenu.filter { it.name != "부서" }
                _employeeEditState.update { EmployeeEditReducer.reduce(it, EmployeeEditEvent.InitWith(employeeInfo, departments)) }
            }
            is EmployeeEditEvent.ClickedInitSearch -> searchDepartment(isEdit = true)
            is EmployeeEditEvent.ClickedSearch -> searchDepartment(isEdit = true)
            is EmployeeEditEvent.ClickedUpdate -> updateEmployee()
            is EmployeeEditEvent.ChangedPage -> _currentPage.value = e.page
            is EmployeeEditEvent.LoadNextPage -> searchDepartment(isEdit = true)
            else -> Unit
        }
    }

    fun onManageEvent(e: EmployeeManageEvent) {
        _employeeManageState.update { EmployeeManageReducer.reduce(it, e) }

        when (e) {
            is EmployeeManageEvent.ClickedSearch -> getManageEmployees()
            is EmployeeManageEvent.ClickedInitSearch -> getManageEmployees()
            is EmployeeManageEvent.SelectedEmployeeWith -> getEmployeeDetail(e.target, e.userId)
            is EmployeeManageEvent.SelectedDropDownWith -> getManageEmployees()
            else -> Unit
        }
    }

    fun onSearchEvent(e: EmployeeSearchEvent) {
        _employeeSearchState.update { EmployeeSearchReducer.reduce(it, e) }

        when (e) {
            EmployeeSearchEvent.ClickedSearch -> getEmployees()
            EmployeeSearchEvent.ClickedInitSearch -> getEmployees()
            is EmployeeSearchEvent.SelectedEmployeeWith -> getEmployeeDetail(e.target, e.userId)
            else -> Unit
        }
    }

    /* 직원 목록 조회 */
    fun getEmployees() {
        viewModelScope.launch {
            employeeRepository.getEmployees(_employeeSearchState.value.searchText).collect { result ->
                result
                    .onSuccess { employees ->
                        _employeeSearchState.update { it.copy(employees = employees) }
                        Log.d(TAG, "직원 목록 조회 성공: 검색(${_employeeSearchState.value.searchText})\n${employees}")
                    }
                    .onFailure { e ->
                        e.printStackTrace()
                    }
            }
        }
    }

    /* 직원 상세 조회 */
    fun getEmployeeDetail(employeeTarget: EmployeeTarget, userId: String) {
        viewModelScope.launch {
            employeeRepository.getEmployeeDetail(userId).collect { result ->
                result
                    .onSuccess { employeeInfo ->
                        when (employeeTarget) {
                            EmployeeTarget.SEARCH -> { _employeeSearchState.update { it.copy(employeeInfo = employeeInfo) } }
                            EmployeeTarget.MANAGE -> { _employeeDetailState.update { it.copy(employeeInfo = employeeInfo) } }
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
        val state = employeeManageState.value

        viewModelScope.launch {
            _employeeManageState.update { it.copy(paginationState = it.paginationState.copy(isLoading = true)) }

            employeeRepository.getManageEmployees(
                department = state.dropDownState.department,
                grade = state.dropDownState.grade,
                title = state.dropDownState.title,
                name = state.searchText,
                page = state.paginationState.currentPage
            ).collect { result ->
                result
                    .onSuccess { data ->
                        if (state.paginationState.currentPage == 0) {
                            _employeeManageState.update { it.copy(employees = data.content, paginationState = it.paginationState.copy(currentPage = it.paginationState.currentPage + 1, totalPage = data.totalPages, isLoading = false)) }
                        }
                        else {
                            _employeeManageState.update { it.copy(employees = it.employees + data.content, paginationState = it.paginationState.copy(currentPage = it.paginationState.currentPage + 1, isLoading = false)) }
                        }
                        Log.d(TAG, "직원 관리 목록 조회 성공: ${state.paginationState.currentPage + 1}/${data.totalPages}, 검색(${state.dropDownState.department}, ${state.dropDownState.grade}, ${state.dropDownState.title}, ${state.searchText})\n${data.content}")
                    }
                    .onFailure { e ->
                        e.printStackTrace()
                    }
            }
        }
    }

    /* 새로운 직원 등록 */
    fun addEmployee() {
        viewModelScope.launch {
            val inputData = employeeAddState.value.inputData
            val request = EmployeeDTO.AddEmployeeRequest(
                id = inputData.loginId,
                name = inputData.name,
                departmentId = employeeAddState.value.selectDepartmentId, // 부서 아이디
                grade = inputData.grade,
                title = if (inputData.title == "직책") "" else inputData.title!!,
                phone = formatPhone(inputData.phone ?: ""), // 전화번호 형식으로 포맷팅 (000-0000-0000)
                birthDate = if (inputData.birthDate.isNullOrBlank()) "" else inputData.birthDate + "T00:00:00",
                hireDate = inputData.hireDate + "T00:00:00",
                authors = employeeAddState.value.selectAuthor.map { it.code }, // 권한 코드
                salaries = inputData.salaries.filter { it.year != "" && it.amount != 0 } // 연봉 정보를 입력하지 않았으면 제거
            )

            Log.d(TAG, "직원 등록 요청\n${request}")
            viewModelScope.launch {
                employeeRepository.addEmployee(request).collect { result ->
                    result
                        .onSuccess { data ->
                            _employeeDetailState.update { it.copy(employeeInfo = data) }
                            Log.d(TAG, "직원 등록 성공: ${data}")
                            _uiEffect.emit(ShowToast("등록이 완료되었습니다."))
                            _uiEffect.emit(UiEffect.NavigateBack)
                            _uiEffect.emit(UiEffect.Navigate("employeeDetail")) // 등록한 직원 상세 조회 화면으로 이동
                        }
                        .onFailure { e ->
                            e.printStackTrace()
                        }
                }
            }
        }
    }

    /* 직원 정보 수정 */
    fun updateEmployee() {
        val inputData = employeeEditState.value.inputData
        val request = EmployeeDTO.UpdateEmployeeRequest(
            userId = inputData.userId,
            name = inputData.name,
            departmentId = employeeEditState.value.selectDepartmentId, // 부서 아이디
            grade = inputData.grade,
            title = if (inputData.title == "직책") "" else inputData.title!!,
            phone = formatPhone(inputData.phone ?: ""), // 전화번호 형식으로 포맷팅 (000-0000-0000)
            birthDate = if (inputData.birthDate.isNullOrBlank()) "" else inputData.birthDate + "T00:00:00",
            hireDate = inputData.hireDate + "T00:00:00",
            authors = employeeEditState.value.selectAuthor.map { it.code },
            salaries = inputData.salaries.filter { it.year != "" && it.amount != 0 } // 연봉 정보를 입력하지 않았으면 제거
        )

        viewModelScope.launch {
            employeeRepository.updateEmployee(request).collect { result ->
                result
                    .onSuccess { data ->
                        _employeeDetailState.update { it.copy(employeeInfo = data) }
                        _uiEffect.emit(ShowToast("수정이 완료되었습니다"))
                        _uiEffect.emit(UiEffect.NavigateBack)
                        Log.d(TAG, "직원 정보 수정 성공: ${data}")
                    }
                    .onFailure { e ->
                        e.printStackTrace()
                    }
            }
        }
    }

    /* 전체 부서 조회 */
    fun getAllDepartments() {
        viewModelScope.launch {
            departmentRepository.getAllDepartments().collect { result ->
                result
                    .onSuccess { departments ->
                        _employeeManageState.update { it.copy(
                            dropDownMenu = it.dropDownMenu.copy(departmentMenu = it.dropDownMenu.departmentMenu + departments)
                        ) }
                        Log.d(TAG, "전체 부서 조회 성공: 검색(${employeeManageState.value.searchText})\n${departments}")
                    }
                    .onFailure { e ->
                        e.printStackTrace()
                    }
            }
        }
    }

    /* 부서 검색 */
    fun searchDepartment(isEdit: Boolean) {
        if (isEdit) {
            val state = employeeEditState.value

            viewModelScope.launch {
                _employeeEditState.update {
                    it.copy(
                        paginationState = it.paginationState.copy(
                            isLoading = true
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
                                _employeeEditState.update {
                                    it.copy(
                                        dropDownMenu = it.dropDownMenu.copy(departmentMenu = data.content),
                                        paginationState = it.paginationState.copy(
                                            currentPage = it.paginationState.currentPage + 1,
                                            totalPage = data.totalPages,
                                            isLoading = false
                                        )
                                    )
                                }
                            } else {
                                _employeeEditState.update {
                                    it.copy(
                                        dropDownMenu = it.dropDownMenu.copy(departmentMenu = data.content),
                                        paginationState = it.paginationState.copy(
                                            currentPage = it.paginationState.currentPage + 1,
                                            isLoading = false
                                        )
                                    )
                                }
                            }
                            Log.d(
                                TAG,
                                "부서 검색 성공: ${state.paginationState.currentPage + 1}/${data.totalPages}, 검색(${state.searchText})\n${data.content}"
                            )
                        }
                        .onFailure { e ->
                            e.printStackTrace()
                        }
                }
            }
        }
        else {
            val state = employeeAddState.value

            viewModelScope.launch {
                _employeeAddState.update { it.copy(paginationState = it.paginationState.copy(isLoading = true)) }

                departmentRepository.getDepartments(
                    searchName = state.searchText,
                    page = state.paginationState.currentPage
                ).collect { result ->
                    result
                        .onSuccess { data ->
                            if (state.paginationState.currentPage == 0) {
                                _employeeAddState.update { it.copy(
                                    dropDownMenu = it.dropDownMenu.copy(departmentMenu = data.content),
                                    paginationState = it.paginationState.copy(currentPage = it.paginationState.currentPage + 1, totalPage = data.totalPages, isLoading = false)
                                ) }
                            }
                            else {
                                _employeeAddState.update { it.copy(
                                    dropDownMenu = it.dropDownMenu.copy(departmentMenu = data.content),
                                    paginationState = it.paginationState.copy(currentPage = it.paginationState.currentPage + 1, isLoading = false)
                                ) }
                            }
                            Log.d(TAG, "부서 검색 성공: ${state.paginationState.currentPage + 1}/${data.totalPages}, 검색(${state.searchText})\n${data.content}")
                        }
                        .onFailure { e ->
                            e.printStackTrace()
                        }
                }
            }
        }
    }

    /* 공통코드 목록에서 직급, 직책 조회 */
    fun getGradeTitle() {
        viewModelScope.launch {
            commonCodeRepository.getCommonCodes(
                searchType = SearchType.UPPER_CODE_NM,
                searchKeyword = "직급",
                page = 0    // 개수가 더 늘어나면 페이지 관리도 해야함 지금은 필요 없음
            ).collect { result ->
                result
                    .onSuccess { data ->
                        val gradeNames: List<String> = data.content.map { it.codeName }
                        _employeeManageState.update { it.copy(dropDownMenu = it.dropDownMenu.copy(gradeMenu = gradeNames)) }
                        _employeeEditState.update { it.copy(dropDownMenu = it.dropDownMenu.copy(gradeMenu = gradeNames)) }
                        _employeeAddState.update { it.copy(dropDownMenu = it.dropDownMenu.copy(gradeMenu = gradeNames)) }
                        Log.d(TAG, "직급 목록 조회 성공\n${gradeNames}")
                    }
                    .onFailure { e ->
                        e.printStackTrace()
                    }
            }

            commonCodeRepository.getCommonCodes(
                searchType = SearchType.UPPER_CODE_NM,
                searchKeyword = "직책",
                page = 0    // 개수가 더 늘어나면 페이지 관리도 해야함 지금은 필요 없음
            ).collect { result ->
                result
                    .onSuccess { data ->
                        val titleNames: List<String> = data.content.map { it.codeName }
                        _employeeManageState.update { it.copy(dropDownMenu = it.dropDownMenu.copy(titleMenu = titleNames)) }
                        _employeeEditState.update { it.copy(dropDownMenu = it.dropDownMenu.copy(titleMenu = titleNames)) }
                        _employeeAddState.update { it.copy(dropDownMenu = it.dropDownMenu.copy(titleMenu = titleNames)) }
                        Log.d(TAG, "직책 목록 조회 성공\n${titleNames}")
                    }
                    .onFailure { e ->
                        e.printStackTrace()
                    }
            }
        }
    }

    /* 권한 목록 조회 */
    fun getAuthors() {
        viewModelScope.launch {
            authorRepository.getAuthors().collect { result ->
                result
                    .onSuccess { authors ->
                        _employeeEditState.update { it.copy(authors = authors) }
                        _employeeAddState.update { it.copy(authors = authors) }
                        Log.d(TAG, "권한 목록 조회 성공\n${authors}")
                    }
                    .onFailure { e ->
                        e.printStackTrace()
                    }
            }
        }
    }

    /* 비밀번호 초기화 */
    fun resetPassword() {
        viewModelScope.launch {
            val request = EmployeeDTO.ResetPasswordRequest(id = employeeDetailState.value.employeeInfo.userId)
            employeeRepository.resetPassword(request).collect { result ->
                result
                    .onSuccess { message ->
                        _uiEffect.emit(ShowToast("비밀번호가 초기화되었습니다."))
                        Log.d(TAG, "비밀번호 초기화 성공\n${message}")
                    }
                    .onFailure { e ->
                        e.printStackTrace()
                    }
            }
        }
    }

    /* 직원 탈퇴 */
    fun setDeactivate() {
        viewModelScope.launch {
            employeeRepository.setDeactivate(employeeDetailState.value.employeeInfo.userId).collect { result ->
                result
                    .onSuccess { data ->
                        _employeeDetailState.update { it.copy(employeeInfo = data) }
                        _uiEffect.emit(ShowToast("사용자가 탈퇴되었습니다."))
                        Log.d(TAG, "직원 탈퇴 성공\n${data}")
                    }
                    .onFailure { e ->
                        e.printStackTrace()
                    }
            }
        }
    }

    /* 직원 복구 */
    fun setActivate() {
        viewModelScope.launch {
            employeeRepository.setActivate(employeeDetailState.value.employeeInfo.userId).collect { result ->
                result
                    .onSuccess { data ->
                        _employeeDetailState.update { it.copy(employeeInfo = data) }
                        _uiEffect.emit(ShowToast("사용자가 복구되었습니다."))
                        Log.d(TAG, "직원 복구 성공\n${data}")
                    }
                    .onFailure { e ->
                        e.printStackTrace()
                    }
            }
        }
    }
}