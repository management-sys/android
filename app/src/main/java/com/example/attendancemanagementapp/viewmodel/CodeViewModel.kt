package com.example.attendancemanagementapp.viewmodel

import androidx.lifecycle.ViewModel
import com.example.attendancemanagementapp.model.CodeDTO
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

data class FieldState(
    val value: String = "",
    val error: String? = null
)

data class CodeManageUiState(
    val codes: List<CodeDTO.CodeListInfo> = emptyList(),        // 공통코드 목록
    val selectedCode: String = "",                              // 선택한 공통코드의 코드
    val searchText: FieldState = FieldState(),                  // 검색어
    val selectedFilter: FieldState = FieldState(value = "전체")   // 선택한 필터
)

data class CodeDetailUiState(
    val codeInfo: CodeDTO.CodeInfo = CodeDTO.CodeInfo() // 공통코드 정보
)

data class CodeEditUiState(
    val codeName: FieldState = FieldState(),    // 코드명
    val codeValue: FieldState = FieldState(),   // 코드값
    val description: FieldState = FieldState()  // 설명
)

data class CodeAddUiState(
    val inputData: CodeDTO.CodeInfo = CodeDTO.CodeInfo() // 입력한 데이터
)

enum class SearchField { SEARCHTEXT, FILTER }
enum class CodeInfoField { CODENAME, CODEVALUE, DESCRIPTION }

@HiltViewModel
class CodeViewModel @Inject constructor() : ViewModel() {
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

    /* 검색 필드 값 변경 */
    fun onSearchFieldChange(field: SearchField, input: String) {
        when (field) {
            SearchField.SEARCHTEXT -> _codeManageUiState.update { state ->
                state.copy(searchText = state.searchText.copy(value = input))
            }
            SearchField.FILTER -> _codeManageUiState.update { state ->
                state.copy(selectedFilter = state.selectedFilter.copy(value = input))
            }
        }
    }

    /* 공통코드 필드 값 변경 */
    fun onCodeInfoFieldChange(field: CodeInfoField, input: String) {
        when (field) {
            CodeInfoField.CODENAME -> _codeEditUiState.update { state ->
                state.copy(codeName = state.codeName.copy(value = input))
            }
            CodeInfoField.CODEVALUE -> _codeEditUiState.update { state ->
                state.copy(codeValue = state.codeValue.copy(value = input))
            }
            CodeInfoField.DESCRIPTION -> _codeEditUiState.update { state ->
                state.copy(description = state.description.copy(value = input))
            }
        }
    }

    /* 공통코드 검색 */
    fun getFilteredCode() {

    }

    /* 공통코드 목록 조회 */
    fun getCodes() {

    }

    /* 공통코드 상세 조회 */
    fun getCodeInfo() {

    }

    /* 공통코드 삭제 */
    fun deleteCode() {

    }

    /* 공통코드 수정 */
    fun updateCode() {

    }
}