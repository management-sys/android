package com.example.attendancemanagementapp.ui.hr.department

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.attendancemanagementapp.data.dto.DepartmentDTO
import com.example.attendancemanagementapp.data.repository.DepartmentRepository
import com.example.attendancemanagementapp.data.repository.EmployeeRepository
import com.example.attendancemanagementapp.ui.base.UiEffect
import com.example.attendancemanagementapp.ui.base.UiEffect.Navigate
import com.example.attendancemanagementapp.ui.hr.department.add.DepartmentAddEvent
import com.example.attendancemanagementapp.ui.hr.department.add.DepartmentAddReducer
import com.example.attendancemanagementapp.ui.hr.department.add.DepartmentAddState
import com.example.attendancemanagementapp.ui.hr.department.detail.DepartmentDetailEvent
import com.example.attendancemanagementapp.ui.hr.department.detail.DepartmentDetailReducer
import com.example.attendancemanagementapp.ui.hr.department.detail.DepartmentDetailState
import com.example.attendancemanagementapp.ui.hr.department.manage.DepartmentManageEvent
import com.example.attendancemanagementapp.ui.hr.department.manage.DepartmentManageReducer
import com.example.attendancemanagementapp.ui.hr.department.manage.DepartmentManageState
import com.example.attendancemanagementapp.util.ErrorHandler
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import retrofit2.HttpException
import javax.inject.Inject

@HiltViewModel
class DepartmentViewModel @Inject constructor(private val departmentRepository: DepartmentRepository, private val employeeRepository: EmployeeRepository) : ViewModel() {
    companion object {
        private const val TAG = "DepartmentViewModel"
    }

    private val _uiEffect = MutableSharedFlow<UiEffect>(extraBufferCapacity = 1)
    val uiEffect = _uiEffect.asSharedFlow()

    private val _departmentAddState = MutableStateFlow(DepartmentAddState())
    val departmentAddState = _departmentAddState.asStateFlow()
    private val _departmentDetailState = MutableStateFlow(DepartmentDetailState())
    val departmentDetailState = _departmentDetailState.asStateFlow()
    private val _departmentManageState = MutableStateFlow(DepartmentManageState())
    val departmentManageState = _departmentManageState.asStateFlow()

    fun onAddEvent(e: DepartmentAddEvent) {
        _departmentAddState.update { DepartmentAddReducer.reduce(it, e) }
    }

    fun onDetailEvent(e: DepartmentDetailEvent) {
        _departmentDetailState.update { DepartmentDetailReducer.reduce(it, e) }

        when (e) {
            DepartmentDetailEvent.ClickedInitSearch -> getEmployees()
            DepartmentDetailEvent.ClickedSearch -> getEmployees()
            DepartmentDetailEvent.ClickedAddDepartment -> {
                _departmentAddState.update { it.copy(
                    inputData = _departmentAddState.value.inputData.copy(
                        upperId = departmentDetailState.value.info.id
                    ),
                    upperName = departmentDetailState.value.info.name
                ) }
            }
            DepartmentDetailEvent.ClickedAddEmployee -> getEmployees()
            else -> Unit
        }
    }

    fun onManageEvent(e: DepartmentManageEvent) {
        _departmentManageState.update { DepartmentManageReducer.reduce(it, e) }

        when (e) {
            is DepartmentManageEvent.Init -> getAllDepartments()
            is DepartmentManageEvent.SelectedDepartmentWith -> {
                getDepartmentDetail(e.departmentId)
                _uiEffect.tryEmit(Navigate("departmentDetail"))
            }
            is DepartmentManageEvent.MoveDepartmentWith -> {
                Log.d("부서 이동", "시작: ${e.fromDepartment}\n끝: ${e.endDepartment}")
                updatePosition(e.fromDepartment.id, e.endDepartment.order, e.endDepartment.upperId)
            }
        }
    }

    /* 스낵바 출력 */
    fun showSnackBar(message: String) {
        _uiEffect.tryEmit(UiEffect.ShowToast(message))
    }

    /* 직원 목록 조회 및 검색 */
    fun getEmployees() {
        viewModelScope.launch {
            employeeRepository.getEmployees(_departmentDetailState.value.searchText).collect { result ->
                result
                    .onSuccess { employeesData ->
                        val employees: List<DepartmentDTO.DepartmentUserInfo> = employeesData.map {
                            DepartmentDTO.DepartmentUserInfo(
                                id = it.id,
                                name = it.name,
                                grade = it.department,
                                title = it.grade,
                                isHead = it.title ?: ""
                            )
                        }
                        _departmentDetailState.update { it.copy(employees = employees) }
                        Log.d(TAG, "[getEmployees] 직원 목록 조회 성공: 검색(${_departmentDetailState.value.searchText})\n${employees}")
                    }
                    .onFailure { e ->
                        ErrorHandler.handle(e, TAG, "getEmployees")
                    }
            }
        }
    }

    /* 전체 부서 목록 조회 */
    fun getAllDepartments() {
        viewModelScope.launch {
            departmentRepository.getAllDepartments().collect { result ->
                result
                    .onSuccess { departments ->
                        val map = departments.groupBy { it.upperId }

                        _departmentManageState.update { it.copy(departments = departments, departmentMap = map) }
                        Log.d(TAG, "[getAllDepartments] 전체 부서 목록 조회 성공\n${departments}")
                    }
                    .onFailure { e ->
                        ErrorHandler.handle(e, TAG, "getAllDepartments")
                    }
            }
        }
    }

    /* 부서 정보 상세 조회 */
    fun getDepartmentDetail(departmentId: String) {
        viewModelScope.launch {
            departmentRepository.getDepartmentDetail(departmentId = departmentId).collect { result ->
                result
                    .onSuccess { departmentInfo ->
                        val headUsers = departmentInfo.users    // 부서장인 사용자의 아이디, 이름 저장
                            .filter { it.isHead == "Y" }
                            .map { it.id to it.name }

                        _departmentDetailState.update { it.copy(
                            info = departmentInfo,
                            updateInfo = departmentInfo,
                            users = departmentInfo.users,
//                            selectedHead = headUsers.toSet()
                        ) }
                        Log.d(TAG, "[getDepartmentDetail] 부서 목록 상세 조회 성공\n${departmentInfo}")
                    }
                    .onFailure { e ->
                        ErrorHandler.handle(e, TAG, "getDepartmentDetail")
                    }
            }
        }
    }

    /* 부서 수정 */
    fun updateDepartment() {
        val data = departmentDetailState.value.updateInfo
        val request = DepartmentDTO.UpdateDepartmentRequest(
            name = data.name,
            description = data.description ?: ""
        )

        viewModelScope.launch {
            departmentRepository.updateDepartment(departmentId = data.id, request = request).collect { result ->
                result.onSuccess { data ->
                    _departmentDetailState.update { it.copy(info = data) }
                    _uiEffect.emit(UiEffect.ShowToast("수정이 완료되었습니다"))
                    Log.d(TAG, "[updateDepartment] 부서 수정 성공\n${data}")
                }
                result.onFailure { e ->
                    ErrorHandler.handle(e, TAG, "updateDepartment")
                }
            }
        }
    }

    /* 부서 위치 변경 */
    fun updatePosition(departmentId: String, newOrder: Int, newUpperId: String?) {
        val request = DepartmentDTO.UpdatePositionRequest(
            newOrder = newOrder,
            newUpperId = newUpperId
        )
        
        viewModelScope.launch { 
            departmentRepository.updatePosition(
                departmentId = departmentId,
                request = request
            ).collect { result ->
                result
                    .onSuccess { departments ->
                        _departmentManageState.update { it.copy(departments = departments) }

                        Log.d(TAG, "[updatePosition] 부서 위치 변경 성공\n${departments}")
                    }
                    .onFailure { e ->
                        ErrorHandler.handle(e, TAG, "updatePosition")
                    }
            }
        }
    }

    /* 부서 삭제 */
    fun deleteDepartment() {
        viewModelScope.launch {
            departmentRepository.deleteDepartment(
                departmentId = departmentDetailState.value.info.id
            ).collect { result ->
                result
                    .onSuccess {
                        getAllDepartments()

                        _uiEffect.emit(UiEffect.ShowToast("부서가 성공적으로 삭제되었습니다."))
                        _uiEffect.emit(UiEffect.NavigateBack)

                        Log.d(TAG, "[deleteDepartment] 부서 삭제 성공")
                    }
                    .onFailure { e ->
                        ErrorHandler.handle(e, TAG, "deleteDepartment")
                    }
            }
        }
    }

    /* 부서 사용자 정보 저장 */
    fun saveDepartmentUser() {
        val request = DepartmentDTO.UpdateDepartmentUserRequest(
            id = departmentDetailState.value.info.id,
            users = departmentDetailState.value.selectedSave
        )

        viewModelScope.launch {
            departmentRepository.updateDepartmentUser(
                request = request
            ).collect { result ->
                result
                    .onSuccess { departmentInfo ->
                        _departmentDetailState.update { it.copy(info = departmentInfo, selectedSave = emptyList()) }
                        _uiEffect.emit(UiEffect.ShowToast("부서 사용자 정보가 수정되었습니다."))
                        Log.d(TAG, "[saveDepartmentUser] 부서 사용자 정보 수정 성공\n${departmentInfo}")
                    }
                    .onFailure { e ->
                        ErrorHandler.handle(e, TAG, "saveDepartmentUser")
                    }
            }
        }
    }

    /* 부서 등록 */
    fun addDepartment() {
        viewModelScope.launch {
            departmentRepository.addDepartment(
                request = departmentAddState.value.inputData
            ).collect { result ->
                result
                    .onSuccess { departments ->
                        _departmentManageState.update { it.copy(departments = departments) }

                        _uiEffect.emit(UiEffect.ShowToast("부서가 성공적으로 등록되었습니다."))
                        _uiEffect.emit(UiEffect.TargetDeleteNavigate("departmentManage"))

                        Log.d(TAG, "[addDepartment] 부서 등록 성공")
                    }
                    .onFailure { e ->
                        ErrorHandler.handle(e, TAG, "addDepartment")
                    }
            }
        }
    }
}