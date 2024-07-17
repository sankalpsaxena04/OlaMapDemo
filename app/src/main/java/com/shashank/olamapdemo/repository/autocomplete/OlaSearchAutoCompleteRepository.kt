package com.appscrip.olamapdemo.repository.autocomplete

import com.appscrip.olamapdemo.model.response.autocompletesearch.OlaSearchAutoCompleteResponse
import com.appscrip.olamapdemo.util.NetworkResult

interface OlaSearchAutoCompleteRepository {
    suspend fun getSearchAutoComplete(apiKey:String,input:String):NetworkResult<OlaSearchAutoCompleteResponse>
}