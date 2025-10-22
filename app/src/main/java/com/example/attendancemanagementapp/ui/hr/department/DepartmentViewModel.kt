package com.example.attendancemanagementapp.ui.hr.department

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.attendancemanagementapp.data.dto.DepartmentDTO
import com.example.attendancemanagementapp.data.repository.DepartmentRepository
import com.example.attendancemanagementapp.data.repository.EmployeeRepository
import com.example.attendancemanagementapp.ui.base.UiEffect
import com.example.attendancemanagementapp.ui.base.UiEffect.Navigate
import com.example.attendancemanagementapp.ui.hr.department.detail.DepartmentDetailEvent
import com.example.attendancemanagementapp.ui.hr.department.detail.DepartmentDetailReducer
import com.example.attendancemanagementapp.ui.hr.department.detail.DepartmentDetailState
import com.example.attendancemanagementapp.ui.hr.department.manage.DepartmentManageEvent
import com.example.attendancemanagementapp.ui.hr.department.manage.DepartmentManageReducer
import com.example.attendancemanagementapp.ui.hr.department.manage.DepartmentManageState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DepartmentViewModel @Inject constructor(private val departmentRepository: DepartmentRepository, private val employeeRepository: EmployeeRepository) : ViewModel() {
    companion object {
        private const val TAG = "DepartmentViewModel"
    }

    private val _uiEffect = MutableSharedFlow<UiEffect>(extraBufferCapacity = 1)
    val uiEffect = _uiEffect.asSharedFlow()

    private val _departmentManageState = MutableStateFlow(DepartmentManageState())
    val departmentManageState = _departmentManageState.asStateFlow()
    private val _departmentDetailState = MutableStateFlow(DepartmentDetailState())
    val departmentDetailState = _departmentDetailState.asStateFlow()

    init {
        getDepartments()
        getEmployees()
    }

    fun onDetailEvent(e: DepartmentDetailEvent) {
        _departmentDetailState.update { DepartmentDetailReducer.reduce(it, e) }

        when (e) {
            DepartmentDetailEvent.ClickedInitSearch -> getEmployees()
            DepartmentDetailEvent.ClickedSearch -> getEmployees()
            else -> Unit
        }
    }

    fun onManageEvent(e: DepartmentManageEvent) {
        _departmentManageState.update { DepartmentManageReducer.reduce(it, e) }

        when (e) {
            is DepartmentManageEvent.SelectedDepartmentWith -> {
                getDepartmentDetail(e.departmentId)
                _uiEffect.tryEmit(Navigate("departmentDetail"))
            }
            is DepartmentManageEvent.MoveDepartmentWith -> {
                Log.d("부서 이동", "시작: ${e.fromDepartment}\n끝: ${e.endDepartment}")
                // TODO: 근데 그냥 부서 위치 수정 API 보내고 다시 부서 목록 조회하면 되지 않나?
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
                        Log.d(TAG, "직원 목록 조회 성공: 검색(${_departmentDetailState.value.searchText})\n${employees}")
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
            departmentRepository.getDepartments().collect { result ->
                result
                    .onSuccess { departments ->
                        _departmentManageState.update { it.copy(departments = departments) }
                        Log.d(TAG, "부서 목록 조회 성공\n${departments}")
                    }
                    .onFailure { e ->
                        e.printStackTrace()
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
                            selectedHead = headUsers.toSet()
                        ) }
                        Log.d(TAG, "부서 목록 상세 조회 성공\n${departmentInfo}")
                    }
                    .onFailure { e ->
                        e.printStackTrace()
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
                    Log.d(TAG, "부서 수정 성공\n${data}")
                }
                result.onFailure { e ->
                    e.printStackTrace()
                }
            }
        }
    }

    /* TODO: 부서 삭제 */
    fun deleteDepartment() {

    }

    /* TODO: 부서 사용자 저장 */
    fun saveDepartmentUser() {

    }
}