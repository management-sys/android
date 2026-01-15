package com.example.attendancemanagementapp.ui.asset.card

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.attendancemanagementapp.data.repository.CardRepository
import com.example.attendancemanagementapp.data.repository.EmployeeRepository
import com.example.attendancemanagementapp.ui.asset.car.CarTarget
import com.example.attendancemanagementapp.ui.asset.car.CarViewModel
import com.example.attendancemanagementapp.ui.asset.car.detail.CarDetailEvent
import com.example.attendancemanagementapp.ui.asset.car.detail.CarDetailState
import com.example.attendancemanagementapp.ui.asset.car.edit.CarEditEvent
import com.example.attendancemanagementapp.ui.asset.car.edit.CarEditReducer
import com.example.attendancemanagementapp.ui.asset.card.add.CardAddEvent
import com.example.attendancemanagementapp.ui.asset.card.add.CardAddReducer
import com.example.attendancemanagementapp.ui.asset.card.add.CardAddState
import com.example.attendancemanagementapp.ui.asset.card.detail.CardDetailEvent
import com.example.attendancemanagementapp.ui.asset.card.detail.CardDetailState
import com.example.attendancemanagementapp.ui.asset.card.edit.CardEditEvent
import com.example.attendancemanagementapp.ui.asset.card.edit.CardEditReducer
import com.example.attendancemanagementapp.ui.asset.card.edit.CardEditState
import com.example.attendancemanagementapp.ui.asset.card.manage.CardManageEvent
import com.example.attendancemanagementapp.ui.asset.card.manage.CardManageReducer
import com.example.attendancemanagementapp.ui.asset.card.manage.CardManageState
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

enum class CardTarget {
    ADD, EDIT
}

@HiltViewModel
class CardViewModel @Inject constructor(private val cardRepository: CardRepository, private val employeeRepository: EmployeeRepository) : ViewModel() {
    companion object {
        private const val TAG = "CardViewModel"
    }

    private val _uiEffect = MutableSharedFlow<UiEffect>(extraBufferCapacity = 1)
    val uiEffect = _uiEffect.asSharedFlow()

    private val _cardAddState = MutableStateFlow(CardAddState())
    val cardAddState = _cardAddState.asStateFlow()
    private val _cardDetailState = MutableStateFlow(CardDetailState())
    val cardDetailState = _cardDetailState.asStateFlow()
    private val _cardEditState = MutableStateFlow(CardEditState())
    val cardEditState = _cardEditState.asStateFlow()
    private val _cardManageState = MutableStateFlow(CardManageState())
    val cardManageState = _cardManageState.asStateFlow()

    fun onAddEvent(e: CardAddEvent) {
        _cardAddState.update { CardAddReducer.reduce(it, e) }

        when (e) {
            is CardAddEvent.InitWith -> getEmployees(CardTarget.ADD)
            CardAddEvent.ClickedSearch -> getEmployees(CardTarget.ADD)
            CardAddEvent.ClickedSearchInit -> getEmployees(CardTarget.ADD)
            CardAddEvent.LoadNextPage -> getEmployees(CardTarget.ADD)
            CardAddEvent.ClickedAdd -> addCard()
            else -> Unit
        }
    }

    fun onDetailEvent(e: CardDetailEvent) {
        when (e) {
            CardDetailEvent.ClickedDelete -> deleteCard()
            CardDetailEvent.ClickedUpdate -> {
                _uiEffect.tryEmit(UiEffect.Navigate("cardEdit"))
            }
            else -> Unit
        }
    }

    fun onEditEvent(e: CardEditEvent) {
        _cardEditState.update { CardEditReducer.reduce(it, e) }

        when (e) {
            is CardEditEvent.InitWith -> getEmployees(CardTarget.EDIT)
            CardEditEvent.ClickedSearch -> getEmployees(CardTarget.EDIT)
            CardEditEvent.ClickedSearchInit -> getEmployees(CardTarget.EDIT)
            CardEditEvent.LoadNextPage -> getEmployees(CardTarget.EDIT)
            CardEditEvent.ClickedUpdate -> updateCard()
            else -> Unit
        }
    }

    fun onManageEvent(e: CardManageEvent) {
        _cardManageState.update { CardManageReducer.reduce(it, e) }

        when (e) {
            CardManageEvent.Init -> getCards()
            CardManageEvent.ClickedSearch -> getCards()
            CardManageEvent.ClickedInitSearchText -> getCards()
            is CardManageEvent.SelectedTypeWith -> getCards()
            is CardManageEvent.ClickedCarWith -> {
                getCard(e.id)
                _uiEffect.tryEmit(UiEffect.Navigate("cardDetail"))
            }
            else -> Unit
        }
    }

    /* 카드 목록 조회 및 검색 */
    fun getCards() {
        val state = cardManageState.value

        viewModelScope.launch {
            cardRepository.getCards(
                keyword = state.searchText,
                type = state.type
            ).collect { result ->
                result
                    .onSuccess { data ->
                        _cardManageState.update { it.copy(cards = data.cards) }

                        Log.d(TAG, "[getCards] 카드 목록 조회 및 검색 성공 (${state.type}-${state.searchText})\n${data}")
                    }
                    .onFailure { e ->
                        ErrorHandler.handle(e, TAG, "getCards")
                    }
            }
        }
    }

    /* 카드 등록 */
    fun addCard() {
        viewModelScope.launch {
            cardRepository.addCard(
                request = cardAddState.value.inputData
            ).collect { result ->
                result
                    .onSuccess { data ->
                        _uiEffect.emit(UiEffect.ShowToast("등록이 완료되었습니다"))
                        _uiEffect.emit(UiEffect.NavigateBack)

                        Log.d(TAG, "[addCard] 카드 등록 성공\n${data}")
                    }
                    .onFailure { e ->
                        ErrorHandler.handle(e, TAG, "addCard")
                    }
            }
        }
    }

    /* 카드 정보 상세 조회 */
    fun getCard(id: String) {
        viewModelScope.launch {
            cardRepository.getCard(
                id = id
            ).collect { result ->
                result
                    .onSuccess { data ->
                        _cardDetailState.update { it.copy(cardInfo = data) }

                        Log.d(TAG, "[getCard] 카드 정보 상세 조회 성공\n${data}")
                    }
                    .onFailure { e ->
                        ErrorHandler.handle(e, TAG, "getCard")
                    }
            }
        }
    }

    /* 카드 정보 수정 */
    fun updateCard() {
        val state = cardEditState.value

        viewModelScope.launch {
            cardRepository.updateCard(
                id = state.id,
                request = state.inputData
            ).collect { result ->
                result
                    .onSuccess { data ->
                        _cardDetailState.update { it.copy(cardInfo = data) }

                        _uiEffect.emit(UiEffect.ShowToast("수정이 완료되었습니다"))
                        _uiEffect.emit(UiEffect.NavigateBack)

                        Log.d(TAG, "[updateCard] 카드 정보 수정 성공\n${data}")
                    }
                    .onFailure { e ->
                        ErrorHandler.handle(e, TAG, "updateCard")
                    }
            }
        }
    }

    /* 카드 정보 삭제 */
    fun deleteCard() {
        viewModelScope.launch {
            cardRepository.deleteCard(
                id = cardDetailState.value.cardInfo.id
            ).collect { result ->
                result
                    .onSuccess {
                        _uiEffect.emit(UiEffect.ShowToast("삭제가 완료되었습니다"))
                        _uiEffect.emit(UiEffect.NavigateBack)

                        Log.d(TAG, "[deleteCard] 카드 정보 삭제 성공")
                    }
                    .onFailure { e ->
                        ErrorHandler.handle(e, TAG, "deleteCard")
                    }
            }
        }
    }

    /* 직원 목록 조회 */
    fun getEmployees(target: CardTarget) {
        val state = when (target) {
            CardTarget.ADD -> cardAddState.value.employeeState
            CardTarget.EDIT -> cardEditState.value.employeeState
            else -> EmployeeSearchState()
        }

        val updateState: (EmployeeSearchState) -> Unit = { newState ->
            when (target) {
                CardTarget.ADD -> _cardAddState.update { it.copy(employeeState = newState) }
                CardTarget.EDIT -> _cardEditState.update { it.copy(employeeState = newState) }
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