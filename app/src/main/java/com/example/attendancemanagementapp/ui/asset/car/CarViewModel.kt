package com.example.attendancemanagementapp.ui.asset.car

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.attendancemanagementapp.data.dto.CarDTO
import com.example.attendancemanagementapp.data.repository.CarRepository
import com.example.attendancemanagementapp.data.repository.EmployeeRepository
import com.example.attendancemanagementapp.ui.asset.car.add.CarAddEvent
import com.example.attendancemanagementapp.ui.asset.car.add.CarAddReducer
import com.example.attendancemanagementapp.ui.asset.car.add.CarAddState
import com.example.attendancemanagementapp.ui.asset.car.detail.CarDetailEvent
import com.example.attendancemanagementapp.ui.asset.car.detail.CarDetailState
import com.example.attendancemanagementapp.ui.asset.car.edit.CarEditEvent
import com.example.attendancemanagementapp.ui.asset.car.edit.CarEditReducer
import com.example.attendancemanagementapp.ui.asset.car.edit.CarEditState
import com.example.attendancemanagementapp.ui.asset.car.manage.CarManageEvent
import com.example.attendancemanagementapp.ui.asset.car.manage.CarManageReducer
import com.example.attendancemanagementapp.ui.asset.car.manage.CarManageState
import com.example.attendancemanagementapp.ui.base.UiEffect
import com.example.attendancemanagementapp.ui.project.add.EmployeeSearchState
import com.example.attendancemanagementapp.util.ErrorHandler
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

enum class CarTarget {
    ADD, EDIT
}

@HiltViewModel
class CarViewModel @Inject constructor(private val carRepository: CarRepository, private val employeeRepository: EmployeeRepository) : ViewModel() {
    companion object {
        private const val TAG = "CarViewModel"
    }

    private val _uiEffect = MutableSharedFlow<UiEffect>(extraBufferCapacity = 1)
    val uiEffect = _uiEffect.asSharedFlow()

    private val _carAddState = MutableStateFlow(CarAddState())
    val carAddState = _carAddState.asStateFlow()
    private val _carDetailState = MutableStateFlow(CarDetailState())
    val carDetailState = _carDetailState.asStateFlow()
    private val _carEditState = MutableStateFlow(CarEditState())
    val carEditState = _carEditState.asStateFlow()
    private val _carManageState = MutableStateFlow(CarManageState())
    val carManageState = _carManageState.asStateFlow()

    fun onAddEvent(e: CarAddEvent) {
        _carAddState.update { CarAddReducer.reduce(it, e) }

        when (e) {
            is CarAddEvent.InitWith -> getEmployees(CarTarget.ADD)
            CarAddEvent.ClickedSearch -> getEmployees(CarTarget.ADD)
            CarAddEvent.ClickedSearchInit -> getEmployees(CarTarget.ADD)
            CarAddEvent.LoadNextPage -> getEmployees(CarTarget.ADD)
            CarAddEvent.ClickedAdd -> addCar()
            else -> Unit
        }
    }

    fun onDetailEvent(e: CarDetailEvent) {
        when (e) {
            CarDetailEvent.ClickedDelete -> deleteCar()
            CarDetailEvent.ClickedUpdate -> {
                _uiEffect.tryEmit(UiEffect.Navigate("carEdit"))
            }
            else -> Unit
        }
    }

    fun onEditEvent(e: CarEditEvent) {
        _carEditState.update { CarEditReducer.reduce(it, e) }

        when (e) {
            is CarEditEvent.InitWith -> getEmployees(CarTarget.EDIT)
            CarEditEvent.ClickedSearch -> getEmployees(CarTarget.EDIT)
            CarEditEvent.ClickedSearchInit -> getEmployees(CarTarget.EDIT)
            CarEditEvent.LoadNextPage -> getEmployees(CarTarget.EDIT)
            CarEditEvent.ClickedUpdate -> updateCar()
            else -> Unit
        }
    }

    fun onManageEvent(e: CarManageEvent) {
        _carManageState.update { CarManageReducer.reduce(it, e) }

        when (e) {
            CarManageEvent.Init -> getCars()
            CarManageEvent.ClickedSearch -> getCars()
            CarManageEvent.ClickedInitSearchText -> getCars()
            is CarManageEvent.SelectedTypeWith -> getCars()
            is CarManageEvent.ClickedCarWith -> {
                getCar(e.id)
                _uiEffect.tryEmit(UiEffect.Navigate("carDetail"))
            }
            else -> Unit
        }
    }

    /* 차량 목록 조회 및 검색 */
    fun getCars() {
        val state = carManageState.value

        viewModelScope.launch {
            carRepository.getCars(
                keyword = state.searchText,
                type = state.type
            ).collect { result ->
                result
                    .onSuccess { data ->
                        _carManageState.update { it.copy(cars = data.cars) }

                        Log.d(TAG, "[getCars] 차량 목록 조회 및 검색 성공 (${state.type}-${state.searchText})\n${data}")
                    }
                    .onFailure { e ->
                        ErrorHandler.handle(e, TAG, "getCars")
                    }
            }
        }
    }

    /* 차량 등록 */
    fun addCar() {
        viewModelScope.launch {
            carRepository.addCar(
                request = carAddState.value.inputData
            ).collect { result ->
                result
                    .onSuccess { data ->
                        _uiEffect.emit(UiEffect.ShowToast("등록이 완료되었습니다"))
                        _uiEffect.emit(UiEffect.NavigateBack)

                        Log.d(TAG, "[addCar] 차량 등록 성공\n${data}")
                    }
                    .onFailure { e ->
                        ErrorHandler.handle(e, TAG, "addCar")
                    }
            }
        }
    }

    /* 차량 정보 상세 조회 */
    fun getCar(id: String) {
        viewModelScope.launch {
            carRepository.getCar(
                id = id
            ).collect { result ->
                result
                    .onSuccess { data ->
                        _carDetailState.update { it.copy(carInfo = data) }

                        Log.d(TAG, "[addCar] 차량 정보 상세 조회 성공\n${data}")
                    }
                    .onFailure { e ->
                        ErrorHandler.handle(e, TAG, "getCar")
                    }
            }
        }
    }

    /* 차량 정보 수정 */
    fun updateCar() {
        val state = carEditState.value
        Log.d(TAG, "[요청] ${state.inputData}")

        viewModelScope.launch {
            carRepository.updateCar(
                id = state.id,
                request = state.inputData
            ).collect { result ->
                result
                    .onSuccess { data ->
                        _carDetailState.update { it.copy(carInfo = data) }

                        _uiEffect.emit(UiEffect.ShowToast("수정이 완료되었습니다"))
                        _uiEffect.emit(UiEffect.NavigateBack)

                        Log.d(TAG, "[updateCar] 차량 정보 수정 성공\n${data}")
                    }
                    .onFailure { e ->
                        ErrorHandler.handle(e, TAG, "updateCar")
                    }
            }
        }
    }

    /* 차량 정보 삭제 */
    fun deleteCar() {
        viewModelScope.launch {
            carRepository.deleteCar(
                id = carDetailState.value.carInfo.id
            ).collect { result ->
                result
                    .onSuccess {
                        _uiEffect.emit(UiEffect.ShowToast("삭제가 완료되었습니다"))
                        _uiEffect.emit(UiEffect.NavigateBack)

                        Log.d(TAG, "[deleteCar] 차량 정보 삭제 성공")
                    }
                    .onFailure { e ->
                        ErrorHandler.handle(e, TAG, "deleteCar")
                    }
            }
        }
    }

    /* 직원 목록 조회 */
    fun getEmployees(target: CarTarget) {
        val state = when (target) {
            CarTarget.ADD -> carAddState.value.employeeState
            CarTarget.EDIT -> carEditState.value.employeeState
            else -> EmployeeSearchState()
        }

        val updateState: (EmployeeSearchState) -> Unit = { newState ->
            when (target) {
                CarTarget.ADD -> _carAddState.update { it.copy(employeeState = newState) }
                CarTarget.EDIT -> _carEditState.update { it.copy(employeeState = newState) }
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

                        Log.d(TAG, "[getEmployees-${target}] 직원 목록 조회 성공: ${state.paginationState.currentPage + 1}/${data.totalPages}, 검색(${state.searchText})\n${data.content}")
                    }
                    .onFailure { e ->
                        updateState(state.copy(paginationState = state.paginationState.copy(isLoading = false)))

                        ErrorHandler.handle(e, TAG, "getEmployees-${target}")
                    }
            }
        }
    }
}