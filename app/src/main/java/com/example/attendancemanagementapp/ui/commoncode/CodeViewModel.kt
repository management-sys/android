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
import com.example.attendancemanagementapp.ui.commoncode.detail.CodeDetailEvent
import com.example.attendancemanagementapp.ui.commoncode.detail.CodeDetailState
import com.example.attendancemanagementapp.ui.commoncode.edit.CodeEditEvent
import com.example.attendancemanagementapp.ui.commoncode.edit.CodeEditReducer
import com.example.attendancemanagementapp.ui.commoncode.edit.CodeEditState
import com.example.attendancemanagementapp.ui.commoncode.manage.CodeManageEvent
import com.example.attendancemanagementapp.ui.commoncode.manage.CodeManageReducer
import com.example.attendancemanagementapp.ui.commoncode.manage.CodeManageState
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

    init {
        getCodes()
    }

    fun onAddEvent(e: CodeAddEvent) {
        _codeAddState.update { CodeAddReducer.reduce(it, e) }

        when (e) {
            CodeAddEvent.InitSearch -> onManageEvent(CodeManageEvent.InitSearch)
            is CodeAddEvent.ChangedCategoryWith -> onManageEvent(CodeManageEvent.ChangedCategoryWith(e.category))
            is CodeAddEvent.ChangedSearchWith -> onManageEvent(CodeManageEvent.ChangedSearchWith(e.value))
            CodeAddEvent.ClickedAdd -> addCode()
            CodeAddEvent.ClickedInitSearch -> onManageEvent(CodeManageEvent.ClickedInitSearch)
            CodeAddEvent.ClickedSearch -> getCodes()
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
            CodeEditEvent.InitSearch -> onManageEvent(CodeManageEvent.InitSearch)
            is CodeEditEvent.ChangedCategoryWith -> onManageEvent(CodeManageEvent.ChangedCategoryWith(e.category))
            is CodeEditEvent.ChangedSearchWith -> onManageEvent(CodeManageEvent.ChangedSearchWith(e.value))
            CodeEditEvent.ClickedEdit -> updateCode()
            CodeEditEvent.ClickedInitSearch -> onManageEvent(CodeManageEvent.ClickedInitSearch)
            CodeEditEvent.ClickedSearch -> getCodes()
            else -> Unit
        }
    }

    fun onManageEvent(e: CodeManageEvent) {
        _codeManageState.update { CodeManageReducer.reduce(it, e) }

        when (e) {
            CodeManageEvent.InitSearch -> getCodes()
            is CodeManageEvent.ChangedCategoryWith -> getCodes()
            CodeManageEvent.ClickedInitSearch -> getCodes()
            CodeManageEvent.ClickedSearch -> getCodes()
            is CodeManageEvent.SelectedCode -> getCodeInfo(e.code)
            else -> Unit
        }
    }

    /* 검색어, 카테고리 상태 초기화 */
    fun initSearchState() {
        _codeManageState.value = CodeManageState()
        getCodes()  // 전체 코드 목록 가져옴
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
                        initSearchState()

                        _uiEffect.emit(UiEffect.NavigateBack)
                        _uiEffect.emit(UiEffect.Navigate("codeDetail"))
                        _uiEffect.emit(UiEffect.ShowToast("등록이 완료되었습니다"))

                        Log.d(TAG, "공통코드 등록 완료: ${code}\n${commonCodeData}")
                    }
                    .onFailure { e ->
                        e.printStackTrace()
                    }
            }
        }
    }

    /* 공통코드 목록 조회 및 검색 */
    fun getCodes() {
        val state = _codeManageState.value

        viewModelScope.launch {
            _codeManageState.update { it.copy(paginationState = it.paginationState.copy(isLoading = true)) }

            repository.getCommonCodes(
                state.selectedCategory,
                state.searchText,
                state.paginationState.currentPage
            ).collect { result ->
                result
                    .onSuccess { data ->
                        if (state.paginationState.currentPage == 0) {
                            _codeManageState.update { it.copy(
                                codes = data.content,
                                paginationState = it.paginationState.copy(currentPage = it.paginationState.currentPage + 1, totalPage = data.totalPages, isLoading = false)
                            ) }
                        }
                        else {
                            _codeManageState.update { it.copy(
                                codes = it.codes + data.content,
                                paginationState = it.paginationState.copy(currentPage = it.paginationState.currentPage + 1, isLoading = false)
                            ) }
                        }
                        Log.d(TAG, "공통코드 목록 조회 성공: ${state.paginationState.currentPage + 1}/${data.totalPages}, 검색(${state.selectedCategory}, ${state.searchText})\n${data.content}")
                    }
                    .onFailure { e ->
                        e.printStackTrace()
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
                        Log.d(TAG, "공통코드 상세 조회 완료: ${code}\n${codeInfo}")
                    }
                    .onFailure { e ->
                        e.printStackTrace()
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
                        initSearchState()

                        _uiEffect.emit(UiEffect.NavigateBack)
                        _uiEffect.emit(UiEffect.ShowToast("수정이 완료되었습니다"))

                        Log.d(TAG, "공통코드 수정 완료: ${code}\n${commoCodeData}")
                    }
                    .onFailure { e ->
                        e.printStackTrace()
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
                        initSearchState()

                        _uiEffect.emit(UiEffect.NavigateBack)
                        _uiEffect.emit(UiEffect.ShowToast("삭제가 완료되었습니다"))

                        Log.d(TAG, "공통코드 삭제 완료: ${code} ${count}")
                    }
                    .onFailure { e ->
                        e.printStackTrace()
                    }
            }
        }
    }
}