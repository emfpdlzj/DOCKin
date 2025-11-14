package com.project.dockin.ui.ar

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.project.dockin.data.api.ArApi
import com.project.dockin.data.repo.ArRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ArMemoViewModel(
    private val repo: ArRepository
) : ViewModel() {

    private val _memos = MutableStateFlow<List<ArApi.ArMemoDto>>(emptyList())
    val memos: StateFlow<List<ArApi.ArMemoDto>> = _memos

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    fun loadMemos(equipmentId: Int) {
        viewModelScope.launch {
            _loading.value = true
            _error.value = null
            try {
                val list = repo.getMemos(equipmentId)
                _memos.value = list
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _loading.value = false
            }
        }
    }

    fun addMemo(equipmentId: Int, text: String) {
        viewModelScope.launch {
            _loading.value = true
            _error.value = null
            try {
                repo.addMemo(equipmentId, text)
                val updated = repo.getMemos(equipmentId)
                _memos.value = updated
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _loading.value = false
            }
        }
    }

    fun deleteMemo(equipmentId: Int, memoId: Long) {
        viewModelScope.launch {
            _loading.value = true
            _error.value = null
            try {
                repo.deleteMemo(memoId)
                val updated = repo.getMemos(equipmentId)
                _memos.value = updated
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _loading.value = false
            }
        }
    }
}