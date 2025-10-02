package com.example.attendancemanagementapp.ui.hr.department

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.attendancemanagementapp.data.repository.HrRepository
import com.example.attendancemanagementapp.ui.hr.DepartmentField
import com.example.attendancemanagementapp.ui.hr.department.detail.DepartmentDetailUiState
import com.example.attendancemanagementapp.ui.hr.department.detail.DepartmentUserInfo
import com.example.attendancemanagementapp.ui.hr.department.manage.DepartmentManageUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.collections.plus

@HiltViewModel
class DepartmentViewModel @Inject constructor(private val repository: HrRepository) : ViewModel() {
    companion object {
        private const val TAG = "DepartmentViewModel"
    }

    private val _snackbar = MutableSharedFlow<String>(replay = 0, extraBufferCapacity = 1)
    val snackbar = _snackbar.asSharedFlow()
    private val _departmentManageUiState = MutableStateFlow(DepartmentManageUiState())
    val departmentManageUiState = _departmentManageUiState.asStateFlow()
    private val _departmentDetailUiState = MutableStateFlow(DepartmentDetailUiState())
    val departmentDetailUiState = _departmentDetailUiState.asStateFlow()

    /* 스낵바 출력 */
    fun showSnackBar(message: String) {
        _snackbar.tryEmit(message)
    }

    /* 검색어 입력 이벤트 */
    fun onSearchTextChange(input: String) {
        _departmentDetailUiState.update { it.copy(searchText = input) }
    }

    /* 부서 정보 입력 이벤트 */
    fun onFieldChange(field: DepartmentField, input: String) {
        when (field) {
            DepartmentField.NAME -> {
                _departmentDetailUiState.update { state ->
                    state.copy(updateInfo = state.info.copy(name = input))
                }
            }
            DepartmentField.DESCRIPTION -> {
                _departmentDetailUiState.update { state ->
                    state.copy(updateInfo = state.info.copy(description = input))
                }
            }
        }
    }

    /* 검색어 상태 초기화 */
    fun initSearchState() {
        _departmentDetailUiState.update { it.copy(searchText = "", employees = emptyList()) }
        getEmployees()
    }

    /* 직원 추가 목록 상태 초기화 */
    fun initAddEmployee() {
        _departmentDetailUiState.update { it.copy(searchText = "", selectedEmployees = emptyList()) }
    }

    /* 저장 목록 체크박스 선택 이벤트 */
    fun onSaveChecked(isChecked: Boolean, id: String) {
        _departmentDetailUiState.update { state ->
            state.copy(selectedSave = if (isChecked) state.selectedSave - id else state.selectedSave + id)
        }
    }

    /* 직원 추가 목록 체크박스 선택 이벤트 */
    fun onAddChecked(isChecked: Boolean, user: DepartmentUserInfo) {
        _departmentDetailUiState.update { it.copy(selectedEmployees = if (isChecked) it.selectedEmployees - user else it.selectedEmployees + user) }
    }

    /* 직원 추가 저장 */
    fun onSaveAddChecked() {
        _departmentDetailUiState.update { state ->
            state.copy(users = (state.users + state.selectedEmployees), selectedEmployees = emptyList(), selectedSave = state.selectedEmployees.map { it.id }.toSet())
        }
        _departmentDetailUiState.update { it.copy(selectedEmployees = emptyList()) }
    }

    /* 부서장 선택 이벤트 */
    fun onSelectHead(isHead: Boolean, idName: Pair<String, String>) {
        _departmentDetailUiState.update { state ->
            state.copy(selectedHead = if (isHead) state.selectedHead.filterNot { it.first == idName.first }.toSet() else state.selectedHead + idName)
        }
    }

    /* 직원 목록 조회 및 검색 */
    fun getEmployees() {
        viewModelScope.launch {
            repository.getEmployees(_departmentDetailUiState.value.searchText).collect { result ->
                result
                    .onSuccess { employeesData ->
                        val employees: List<DepartmentUserInfo> = employeesData.map {
                            DepartmentUserInfo(it.id, it.name, it.department, it.grade, it.title ?: "")
                        }
                        _departmentDetailUiState.update { it.copy(employees = employees) }
                        Log.d(TAG, "직원 목록 조회 성공: 검색(${_departmentDetailUiState.value.searchText})\n${employees}")
                    }
                    .onFailure { e ->
                        e.printStackTrace()
                    }
            }
        }
    }

    /* TODO: 부서 수정 */
    fun updateDepartment() {

        _snackbar.tryEmit("부서가 성공적으로 수정되었습니다.") // 수정 성공 시 출력
    }

    /* TODO: 부서 삭제 */
    fun deleteDepartment() {

    }

    /* TODO: 부서 사용자 저장 */
    fun saveDepartmentUser() {

    }
}