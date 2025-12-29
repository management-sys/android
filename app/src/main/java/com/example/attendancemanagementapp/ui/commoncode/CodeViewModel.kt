package com.example.attendancemanagementapp.ui.commoncode

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.attendancemanagementapp.data.dto.CommonCodeDTO
import com.example.attendancemanagementapp.data.repository.CommonCodeRepository
import com.example.attendancemanagementapp.ui.base.UiEffect
import com.example.attendancemanagementapp.ui.commoncode.add.CodeAddEvent
import com.example.attendancemanagementapp.ui.commoncode.add.CodeAddReducer
import com.example.attendancemanagementapp.ui.commoncode.add.CodeAddState
import com.example.attendancemanagementapp.ui.commoncode.add.CodeSearchState
import com.example.attendancemanagementapp.ui.commoncode.detail.CodeDetailEvent
import com.example.attendancemanagementapp.ui.commoncode.detail.CodeDetailState
import com.example.attendancemanagementapp.ui.commoncode.edit.CodeEditEvent
import com.example.attendancemanagementapp.ui.commoncode.edit.CodeEditReducer
import com.example.attendancemanagementapp.ui.commoncode.edit.CodeEditState
import com.example.attendancemanagementapp.ui.commoncode.manage.CodeManageEvent
import com.example.attendancemanagementapp.ui.commoncode.manage.CodeManageReducer
import com.example.attendancemanagementapp.ui.commoncode.manage.CodeManageState
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
class CodeViewModel @Inject constructor(private val repository: CommonCodeRepository) : ViewModel() {
    companion object {
        private const val TAG = "CodeViewModel"
    }

    enum class CodeTarget {
        ADD, EDIT, MANAGE
    }

    private val _uiEffect = MutableSharedFlow<UiEffect>(extraBufferCapacity = 1)
    val uiEffect = _uiEffect.asSharedFlow()

    private val _codeManageState = MutableStateFlow(CodeManageState())
    val codeManageState = _codeManageState.asStateFlow()
    private val _codeDetailState = MutableStateFlow(CodeDetailState())
    val codeDetailState = _codeDetailState.asStateFlow()
    private val _codeEditState = MutableStateFlow(CodeEditState())
    val codeEditState = _codeEditState.asStateFlow()
    private val _codeAddState = MutableStateFlow(CodeAddState())
    val codeAddState = _codeAddState.asStateFlow()

    fun onAddEvent(e: CodeAddEvent) {
        _codeAddState.update { CodeAddReducer.reduce(it, e) }

        when (e) {
            CodeAddEvent.Init -> { _codeAddState.value = CodeAddState() }
            is CodeAddEvent.ChangedCategoryWith -> getCodes(CodeTarget.ADD)
            CodeAddEvent.ClickedAdd -> addCode()
            CodeAddEvent.ClickedInitSearch -> getCodes(CodeTarget.ADD)
            CodeAddEvent.ClickedSearch -> getCodes(CodeTarget.ADD)
            else -> Unit
        }
    }

    fun onDetailEvent(e: CodeDetailEvent) {
        when (e) {
            CodeDetailEvent.ClickedDelete -> deleteCode()
        }
    }

    fun onEditEvent(e: CodeEditEvent) {
        _codeEditState.update { CodeEditReducer.reduce(it, e) }

        when (e) {
            CodeEditEvent.Init -> _codeEditState.update { CodeEditReducer.reduce(it, CodeEditEvent.InitWith(codeDetailState.value.codeInfo)) }
            is CodeEditEvent.ChangedCategoryWith -> getCodes(CodeTarget.EDIT)
            CodeEditEvent.ClickedEdit -> updateCode()
            CodeEditEvent.ClickedInitSearch -> getCodes(CodeTarget.EDIT)
            CodeEditEvent.ClickedSearch -> getCodes(CodeTarget.EDIT)
            else -> Unit
        }
    }

    fun onManageEvent(e: CodeManageEvent) {
        _codeManageState.update { CodeManageReducer.reduce(it, e) }

        when (e) {
            CodeManageEvent.InitSearch -> getCodes(CodeTarget.MANAGE)
            is CodeManageEvent.ChangedCategoryWith -> getCodes(CodeTarget.MANAGE)
            CodeManageEvent.ClickedInitSearch -> getCodes(CodeTarget.MANAGE)
            CodeManageEvent.ClickedSearch -> getCodes(CodeTarget.MANAGE)
            is CodeManageEvent.SelectedCode -> getCodeInfo(e.code)
            else -> Unit
        }
    }

    /* 검색어, 카테고리 상태 초기화 */
    fun initSearchState(type: CodeTarget) {
        _codeManageState.value = CodeManageState()
        getCodes(type)  // 전체 코드 목록 가져옴
    }

    /* 공통코드 등록 */
    fun addCode() {
        val inputData = _codeAddState.value.inputData
        val commonCodeData = CommonCodeDTO.AddUpdateCommonCodeRequest(
            code = inputData.code,
            codeName = inputData.codeName,
            upperCode = inputData.upperCode,
            codeValue = inputData.codeValue,
            description = inputData.description
        )

        viewModelScope.launch {
            repository.addCommonCode(commonCodeData).collect { result ->
                result
                    .onSuccess { code ->
                        getCodeInfo(code)
                        initSearchState(CodeTarget.MANAGE)

                        _uiEffect.emit(UiEffect.NavigateBack)
                        _uiEffect.emit(UiEffect.Navigate("codeDetail"))
                        _uiEffect.emit(UiEffect.ShowToast("등록이 완료되었습니다"))

                        Log.d(TAG, "[addCode] 공통코드 등록 완료: ${code}\n${commonCodeData}")
                    }
                    .onFailure { e ->
                        ErrorHandler.handle(e, TAG, "addCode")
                    }
            }
        }
    }

    /* 공통코드 목록 조회 및 검색 */
    fun getCodes(target: CodeTarget) {
        val state = when (target) {
            CodeTarget.ADD -> codeAddState.value.codeState
            CodeTarget.EDIT -> codeEditState.value.codeState
            CodeTarget.MANAGE -> codeManageState.value.codeState
        }

        val updateState: (CodeSearchState) -> Unit = { newState ->
            when (target) {
                CodeTarget.ADD -> _codeAddState.update { it.copy(codeState = newState) }
                CodeTarget.EDIT -> _codeEditState.update { it.copy(codeState = newState) }
                CodeTarget.MANAGE -> _codeManageState.update { it.copy(codeState = newState) }
            }
        }

        updateState(state.copy(paginationState = state.paginationState.copy(isLoading = true)))

        viewModelScope.launch {
            repository.getCommonCodes(
                searchType = state.selectedCategory,
                searchKeyword = state.searchText,
                page = state.paginationState.currentPage
            ).collect { result ->
                result
                    .onSuccess { data ->
                        val isFirstPage = state.paginationState.currentPage == 0

                        val updatedCodes = if (isFirstPage) data.content else state.codes + data.content

                        updateState(state.copy(
                            codes = updatedCodes,
                            paginationState = state.paginationState.copy(
                                currentPage = state.paginationState.currentPage + 1,
                                totalPage = data.totalPages,
                                isLoading = false
                            )
                        ))

                        Log.d(TAG, "[getCodes-${target}] 공통코드 목록 조회 성공: ${state.paginationState.currentPage + 1}/${data.totalPages}, 검색(${state.selectedCategory}, ${state.searchText})\n${data.content}")
                    }
                    .onFailure { e ->
                        updateState(state.copy(paginationState = state.paginationState.copy(isLoading = false)))

                        ErrorHandler.handle(e, TAG, "getCodes-${target}")
                    }
            }
        }
    }

    /* 공통코드 상세 조회 */
    fun getCodeInfo(code: String) {
        viewModelScope.launch {
            repository.getCommonCodeDetail(code).collect { result ->
                result
                    .onSuccess { codeInfo ->
                        _codeDetailState.update { it.copy(codeInfo = codeInfo) }
                        _codeEditState.update { it.copy(inputData = codeInfo) }
                        Log.d(TAG, "[getCodeInfo] 공통코드 상세 조회 완료: ${code}\n${codeInfo}")
                    }
                    .onFailure { e ->
                        ErrorHandler.handle(e, TAG, "getCodeInfo")
                    }
            }
        }
    }

    /* 공통코드 수정 */
    fun updateCode() {
        val inputData = _codeEditState.value.inputData
        val commoCodeData = CommonCodeDTO.AddUpdateCommonCodeRequest(
            code = inputData.code,
            codeName = inputData.codeName,
            upperCode = inputData.upperCode,
            codeValue = inputData.codeValue,
            description = inputData.description
        )

        viewModelScope.launch {
            repository.updateCommonCode(commoCodeData).collect { result ->
                result
                    .onSuccess { code ->
                        getCodeInfo(code)
//                        initSearchState(CodeScreenType.MANAGE)

                        _uiEffect.emit(UiEffect.NavigateBack)
                        _uiEffect.emit(UiEffect.ShowToast("수정이 완료되었습니다"))

                        Log.d(TAG, "[updateCode] 공통코드 수정 완료: ${code}\n${commoCodeData}")
                    }
                    .onFailure { e ->
                        ErrorHandler.handle(e, TAG, "updateCode")
                    }
            }
        }
    }

    /* 공통코드 삭제 */
    fun deleteCode() {
        val code = _codeDetailState.value.codeInfo.code

        viewModelScope.launch {
            repository.deleteCommonCode(code).collect { result ->
                result
                    .onSuccess { count ->
                        initSearchState(CodeTarget.MANAGE)

                        _uiEffect.emit(UiEffect.NavigateBack)
                        _uiEffect.emit(UiEffect.ShowToast("삭제가 완료되었습니다"))

                        Log.d(TAG, "[deleteCode] 공통코드 삭제 완료: ${code} ${count}")
                    }
                    .onFailure { e ->
                        ErrorHandler.handle(e, TAG, "deleteCode")
                    }
            }
        }
    }
}