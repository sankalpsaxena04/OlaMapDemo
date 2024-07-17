package com.appscrip.olamapdemo.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.appscrip.olamapdemo.model.response.autocompletesearch.OlaSearchAutoCompleteResponse
import com.appscrip.olamapdemo.remote.AutoCompleteRetrofitClient
import com.appscrip.olamapdemo.repository.autocomplete.OlaSearchAutoCompleteRepositoryImpl
import com.appscrip.olamapdemo.util.DataConstants.API_KEY
import com.appscrip.olamapdemo.util.NetworkResult
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class OlaSearchAutoCompleteViewModel : ViewModel() {
    private val olaSearchAutoCompleteRepository = OlaSearchAutoCompleteRepositoryImpl(
        AutoCompleteRetrofitClient.apiService
    )

    private var searchJob: Job? = null
    private val debounceDelay = 300L // 300 milliseconds debounce time
    private val mutableSearchLocation = MutableLiveData<OlaSearchAutoCompleteResponse>()
    val mSearchedLocationLiveData get() = mutableSearchLocation

    fun callSearchAutoCompleteApi(apiKey: String = API_KEY, input: String) {
        searchJob?.cancel() // Cancel any ongoing debounce job
        searchJob = viewModelScope.launch {
            delay(debounceDelay) // Wait for the debounce period
            val result = olaSearchAutoCompleteRepository.getSearchAutoComplete(apiKey, input)
            when (result) {
                is NetworkResult.Error -> {
                    Log.d("SEAR", result.errorMsg)
                }
                is NetworkResult.Loading -> {
                    // Handle loading state if needed
                }
                is NetworkResult.Success -> {
                    mutableSearchLocation.postValue(result.data)
                }
            }
        }
    }

}