package com.appscrip.olamapdemo.repository.autocomplete

import com.appscrip.olamapdemo.model.response.autocompletesearch.OlaSearchAutoCompleteResponse
import com.appscrip.olamapdemo.remote.AutoCompleteApi
import com.appscrip.olamapdemo.util.NetworkResult

class OlaSearchAutoCompleteRepositoryImpl(private val api: AutoCompleteApi) :
    OlaSearchAutoCompleteRepository {
    override suspend fun getSearchAutoComplete(
        apiKey: String,
        input: String
    ): NetworkResult<OlaSearchAutoCompleteResponse> = try {
        NetworkResult.Success(api.getAutoCompleteSearchResult(search = input, apiKey = apiKey))
    } catch (e: Exception) {
        NetworkResult.Error(errorMsg = e.message.toString())
    }
}