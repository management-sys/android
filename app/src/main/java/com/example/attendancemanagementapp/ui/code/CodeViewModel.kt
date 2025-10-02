package com.example.attendancemanagementapp.ui.code

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.attendancemanagementapp.data.dto.CommonCodeDTO
import com.example.attendancemanagementapp.data.repository.CommonCodeRepository
import com.example.attendancemanagementapp.retrofit.param.SearchType
import com.example.attendancemanagementapp.ui.code.add.CodeAddUiState
import com.example.attendancemanagementapp.ui.code.detail.CodeDetailUiState
import com.example.attendancemanagementapp.ui.code.edit.CodeEditUiState
import com.example.attendancemanagementapp.ui.code.manage.CodeManageUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

enum class Target { ADD, EDIT }
enum class CodeInfoField { CODE, CODENAME, CODEVALUE, DESCRIPTION }

@HiltViewModel
class CodeViewModel @Inject constructor(private val repository: CommonCodeRepository) : ViewModel() {
    companion object {
        private const val TAG = "CodeViewModel"
    }

    private val _snackbar = MutableSharedFlow<String>(replay = 0, extraBufferCapacity = 1)
    val snackbar = _snackbar.asSharedFlow()

    private val _codeManageUiState = MutableStateFlow(CodeManageUiState())
    val codeManageUiState = _codeManageUiState.asStateFlow()
    private val _codeDetailUiState = MutableStateFlow(CodeDetailUiState())
    val codeDetailUiState = _codeDetailUiState.asStateFlow()
    private val _codeEditUiState = MutableStateFlow(CodeEditUiState())
    val codeEditUiState = _codeEditUiState.asStateFlow()
    private val _codeAddUiState = MutableStateFlow(CodeAddUiState())
    val codeAddUiState = _codeAddUiState.asStateFlow()

    init {
        getCodes()
    }

    /* 공통코드 등록 UI 상태 초기화 */
    fun initCodeAddUiState() {
        _codeAddUiState.value = CodeAddUiState()
    }

    /* 공통코드 수정 UI 상태 초기화 */
    fun initCodeEditUiState() {
        _codeEditUiState.update { it.copy(inputData = _codeDetailUiState.value.codeInfo) }
    }

    /* 검색어, 카테고리 상태 초기화 */
    fun initSearchState() {
        _codeManageUiState.value = CodeManageUiState()
        getCodes()  // 전체 코드 목록 가져옴
    }

    /* 검색어 입력 필드 값 변경 */
    fun onSearchTextChange(input: String) {
        _codeManageUiState.update { it.copy(searchText = input, currentPage = 0) }
    }

    /* 검색 카테고리 값 변경 */
    fun onSearchTypeChange(selected: SearchType) {
        _codeManageUiState.update { it.copy(selectedCategory = selected, currentPage = 0) }
        if (_codeManageUiState.value.searchText != "") {  // 검색어가 빈 값이면 목록 조회 안 함
            getCodes()
        }
    }

    /* 입력 필드 값 변경 헬퍼 */
    private inline fun updateForm(target: Target, crossinline transform: (CommonCodeDTO.CommonCodeInfo) -> CommonCodeDTO.CommonCodeInfo) {
        when (target) {
            Target.ADD -> _codeAddUiState.update { state ->
                state.copy(
                    inputData = transform(state.inputData)
                )
            }
            Target.EDIT -> _codeEditUiState.update { state ->
                state.copy(
                    inputData = transform(state.inputData)
                )
            }
        }
    }

    /* 공통코드 입력 필드 값 변경 */
    fun onFieldChange(target: Target, field: CodeInfoField, input: String) {
        if (target == Target.EDIT && field == CodeInfoField.CODE) return

        updateForm(target) { info ->
            when (field) {
                CodeInfoField.CODE        -> info.copy(code = input)
                CodeInfoField.CODENAME    -> info.copy(codeName = input)
                CodeInfoField.CODEVALUE   -> info.copy(codeValue = input)
                CodeInfoField.DESCRIPTION -> info.copy(description = input)
            }
        }
    }

    /* 상위코드 선택 처리 */
    fun onUpperCodeChange(target: Target, selectedItem: CommonCodeDTO.CommonCodesInfo) {
        val upperCode = selectedItem.upperCode.orEmpty()
        val upperCodeName = selectedItem.upperCodeName.orEmpty()

        when (target) {
            Target.ADD -> _codeAddUiState.update { state ->
                state.copy(
                    inputData = state.inputData.copy(
                        upperCode = upperCode,
                        upperCodeName = upperCodeName
                    )
                )
            }
            Target.EDIT -> _codeEditUiState.update { state ->
                state.copy(
                    inputData = state.inputData.copy(
                        upperCode = upperCode,
                        upperCodeName = upperCodeName
                    )
                )
            }
        }

        Log.d(TAG, "상위코드 선택: ${_codeAddUiState.value}")
    }

    /* 공통코드 목록 조회 */
    fun getCodes() {
        val state = _codeManageUiState.value

        viewModelScope.launch {
            _codeManageUiState.update { it.copy(isLoading = true) }

            repository.getCommonCodes(
                state.selectedCategory,
                state.searchText,
                state.currentPage
            ).collect { result ->
                result
                    .onSuccess { data ->
                        if (state.currentPage == 0) {
                            _codeManageUiState.update { it.copy(codes = data.content, currentPage = it.currentPage + 1, totalPage = data.totalPages, isLoading = false) }
                        }
                        else {
                            _codeManageUiState.update { it.copy(codes = it.codes + data.content, currentPage = it.currentPage + 1, isLoading = false) }
                        }
                        Log.d(TAG, "공통코드 목록 조회 성공: ${state.currentPage + 1}/${data.totalPages}, 검색(${state.selectedCategory}, ${state.searchText})\n${data.content}")
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
                        _codeDetailUiState.update { it.copy(codeInfo = codeInfo) }
                        _codeEditUiState.update { it.copy(inputData = codeInfo) }
                        Log.d(TAG, "공통코드 상세 조회 완료: ${code}\n${codeInfo}")
                    }
                    .onFailure { e ->
                        e.printStackTrace()
                    }
            }
        }
    }

    /* 공통코드 등록 */
    fun addCode(isSuccess: () -> Unit) {
        val inputData = _codeAddUiState.value.inputData
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
                        Log.d(TAG, "공통코드 등록 완료: ${code}\n${commonCodeData}")
                        _snackbar.emit("등록이 완료되었습니다")
                        isSuccess()
                    }
                    .onFailure { e ->
                        e.printStackTrace()
                    }
            }
        }
    }

    /* 공통코드 수정 */
    fun updateCode(isSuccess: () -> Unit) {
        val inputData = _codeEditUiState.value.inputData
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
                        Log.d(TAG, "공통코드 수정 완료: ${code}\n${commoCodeData}")
                        _snackbar.emit("수정이 완료되었습니다")
                        isSuccess()
                    }
                    .onFailure { e ->
                        e.printStackTrace()
                    }
            }
        }
    }

    /* 공통코드 삭제 */
    fun deleteCode(isSuccess: () -> Unit) {
        val code = _codeDetailUiState.value.codeInfo.code

        viewModelScope.launch {
            repository.deleteCommonCode(code).collect { result ->
                result
                    .onSuccess { count ->
                        Log.d(TAG, "공통코드 삭제 완료: ${code} ${count}")
                        _snackbar.emit("삭제가 완료되었습니다")
                        isSuccess()
                    }
                    .onFailure { e ->
                        e.printStackTrace()
                    }
            }
        }
    }
}