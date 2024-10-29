package com.example.listdisplay.data.source.network

import com.example.listdisplay.data.api.ApiService
import com.example.listdisplay.data.model.AnimResponse
import retrofit2.Response
import javax.inject.Inject

class NetworkDataSource @Inject constructor(private val apiService: ApiService): INetworkDataSource {
    override suspend fun get(query: String): AnimResponse {
        return apiService.getAnime(query)
    }
}