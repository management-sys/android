package com.example.attendancemanagementapp.ui.commoncode

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.attendancemanagementapp.data.repository.CommonCodeRepository
import com.example.attendancemanagementapp.ui.commoncode.add.CodeAddUiState
import com.example.attendancemanagementapp.ui.commoncode.detail.CodeDetailUiState
import com.example.attendancemanagementapp.ui.commoncode.edit.CodeEditUiState
import com.example.attendancemanagementapp.ui.commoncode.list.CodeListUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class FieldState(
    val value: String = "",
    val error: String? = null
)

enum class SearchField { SEARCHTEXT, FILTER }
enum class CodeInfoField { CODENAME, CODEVALUE, DESCRIPTION }

@HiltViewModel
class CodeViewModel @Inject constructor(private val repository: CommonCodeRepository) : ViewModel() {
    companion object {
        private const val TAG = "CodeViewModel"
    }

    private val _codeListUiState = MutableStateFlow(CodeListUiState())
    val codeListUiState = _codeListUiState.asStateFlow()
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
            SearchField.SEARCHTEXT -> _codeListUiState.update { state ->
                state.copy(searchText = state.searchText.copy(value = input))
            }
            SearchField.FILTER -> _codeListUiState.update { state ->
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

    /* 전체 공통코드 조회 */
    fun getCodes() {
        viewModelScope.launch {
            repository.getCommonCodes().collect() { result ->
                result
                    .onSuccess { commonCodes ->
                        _codeListUiState.update { it.copy(codes = commonCodes) }
                        Log.d(TAG, "전체 공통코드 조회 성공\n${commonCodes}")
                    }
                    .onFailure { e ->
                        e.printStackTrace()
                    }
            }
        }
    }

    /* 공통코드 검색 */
    fun getFilteredCode() {

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