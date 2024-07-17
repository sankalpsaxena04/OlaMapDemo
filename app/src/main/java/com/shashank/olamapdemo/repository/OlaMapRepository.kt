package com.appscrip.olamapdemo.repository

import com.appscrip.olamapdemo.util.NetworkResult
import com.appscrip.olamapdemo.model.response.OlaMapAccessTokenResponse

interface OlaMapRepository {
    suspend fun getAccessToken(clientId: String, clientSecret: String): NetworkResult<OlaMapAccessTokenResponse>
}