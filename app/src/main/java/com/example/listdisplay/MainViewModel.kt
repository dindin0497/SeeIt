package com.example.listdisplay

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.listdisplay.data.Result
import com.example.listdisplay.data.model.AnimData
import com.example.listdisplay.data.respoitory.IRepository
import com.example.listdisplay.data.source.local.MyData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class MainViewModel @Inject constructor(
    private val repository: IRepository
): ViewModel() {
    companion object{
        const val TAG="MainViewModel"
    }

    private val _searchText = MutableStateFlow("")
    val searchText: StateFlow<String> = _searchText

    private val _uiState = MutableStateFlow<Result<List<MyData>>>(Result.Loading)
    val uiState: StateFlow<Result<List<MyData>>> = _uiState

    private val _searchState = MutableStateFlow<Result<List<MyData>>>(Result.Loading)
    val searchState: StateFlow<Result<List<MyData>>> = _searchState

    init {
        get()
    }

    fun get(name: String = "naruto") {
        _uiState.value = Result.Loading
        viewModelScope.launch(Dispatchers.IO) {
            _uiState.value = repository.getData(name)
        }
    }

    fun search(name: String) {
        _searchText.value = name

        _searchState.value = Result.Loading
        viewModelScope.launch(Dispatchers.IO) {
            _searchState.value = repository.getData(name)
        }
    }
}