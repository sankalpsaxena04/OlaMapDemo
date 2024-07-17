package com.appscrip.olamapdemo.repository

import com.appscrip.olamapdemo.model.response.OlaMapAccessTokenResponse
import com.appscrip.olamapdemo.remote.ApiServiceWithoutToken
import com.appscrip.olamapdemo.util.NetworkResult

class OlaMapOlaMapRepositoryImpl(
    private val apiService: ApiServiceWithoutToken
) : OlaMapRepository {

    override suspend fun getAccessToken(
        clientId: String,
        clientSecret: String
    ): NetworkResult<OlaMapAccessTokenResponse> = try {
        NetworkResult.Success(apiService.getAccessToken(clientId=clientId, clientSecret = clientSecret))
    } catch (e: Exception) {
        NetworkResult.Error(errorMsg = e.message.toString())
    }
}