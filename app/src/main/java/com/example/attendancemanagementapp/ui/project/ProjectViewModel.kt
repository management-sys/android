package com.example.attendancemanagementapp.ui.project

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.attendancemanagementapp.data.dto.ProjectDTO
import com.example.attendancemanagementapp.data.repository.DepartmentRepository
import com.example.attendancemanagementapp.data.repository.EmployeeRepository
import com.example.attendancemanagementapp.data.repository.ProjectRepository
import com.example.attendancemanagementapp.ui.base.UiEffect
import com.example.attendancemanagementapp.ui.hr.department.DepartmentViewModel
import com.example.attendancemanagementapp.ui.hr.employee.EmployeeViewModel
import com.example.attendancemanagementapp.ui.hr.employee.EmployeeViewModel.EmployeeScreenType
import com.example.attendancemanagementapp.ui.hr.employee.add.EmployeeAddEvent
import com.example.attendancemanagementapp.ui.hr.employee.add.EmployeeAddReducer
import com.example.attendancemanagementapp.ui.project.add.ProjectAddEvent
import com.example.attendancemanagementapp.ui.project.add.ProjectAddReducer
import com.example.attendancemanagementapp.ui.project.add.ProjectAddState
import com.example.attendancemanagementapp.util.ErrorHandler
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProjectViewModel @Inject constructor(private val projectRepository: ProjectRepository, private val employeeRepository: EmployeeRepository, private val departmentRepository: DepartmentRepository) : ViewModel() {
    companion object {
        private const val TAG = "ProjectViewModel"
    }

    private val _uiEffect = MutableSharedFlow<UiEffect>(extraBufferCapacity = 1)
    val uiEffect = _uiEffect.asSharedFlow()

    private val _projectAddState = MutableStateFlow(ProjectAddState())
    val projectAddState = _projectAddState.asStateFlow()

    fun onAddEvent(e: ProjectAddEvent) {
        _projectAddState.update { ProjectAddReducer.reduce(it, e) }

        when (e) {
            ProjectAddEvent.Init -> {
                getEmployees()
                getDepartments()
            }
            ProjectAddEvent.ClickedAdd -> addProject()
            else -> Unit
        }
    }

    /* 프로젝트 등록 */
    fun addProject() {
        val assignedPersonnels = projectAddState.value.checkedAssignedPersonnel.map { employee ->
            ProjectDTO.AssignedPersonnelRequestInfo(
                chargerId = employee.id,
                type = "개발" // TODO: 웹 확인 필요, 임시 데이터 넣어둠
            )
        }
        _projectAddState.update { it.copy(inputData = it.inputData.copy(assignedPersonnels = assignedPersonnels)) }

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

    /* 직원 목록 조회 */
    fun getEmployees() {
        viewModelScope.launch {
            employeeRepository.getEmployees("").collect { result ->
                result
                    .onSuccess { employees ->
                        _projectAddState.update { it.copy(employees = employees) }

                        Log.d(TAG, "[getEmployees] 직원 목록 조회 성공\n${employees}")
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
            departmentRepository.getAllDepartments().collect { result ->
                result
                    .onSuccess { departments ->
                        _projectAddState.update { it.copy(departments = departments) }
                        Log.d(TAG, "[getDepartments] 부서 목록 조회 성공\n${departments}")
                    }
                    .onFailure { e ->
                        ErrorHandler.handle(e, TAG, "getDepartments")
                    }
            }
        }
    }
}