package com.example.listdisplay.data.source.network

import com.example.listdisplay.data.model.AnimResponse
import retrofit2.Response

interface INetworkDataSource {
    suspend fun get(query: String): AnimResponse
}