package com.ukv.assignmentproject.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ukv.assignmentproject.data.model.ItemEntity
import com.ukv.assignmentproject.data.repository.ItemRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ItemViewModel(
    private val repository: ItemRepository
) : ViewModel() {

    private val _items = MutableStateFlow<List<ItemEntity>>(emptyList())
    val items: StateFlow<List<ItemEntity>> = _items.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    init {
        refreshData()
    }

    private fun refreshData() {
        viewModelScope.launch {
            try {
                repository.fetchAndSaveItems()
                _items.value = repository.getItems()
            } catch (e: Exception) {
                _error.value = "Failed to load data: ${e.message}"
            }
        }
    }

    fun updateItem(item: ItemEntity) = viewModelScope.launch {
        try {
            repository.updateItem(item)
            _items.value = repository.getItems()
        } catch (e: Exception) {
            _error.value = "Update failed: ${e.message}"
        }
    }

    fun deleteItem(item: ItemEntity) = viewModelScope.launch {
        try {
            repository.deleteItem(item)
            _items.value = repository.getItems()
        } catch (e: Exception) {
            _error.value = "Delete failed: ${e.message}"
        }
    }
}
