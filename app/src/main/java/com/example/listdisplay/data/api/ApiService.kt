package com.example.listdisplay.data.api

import com.example.listdisplay.data.model.AnimResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {
    @GET("anime")
    suspend fun getAnime( @Query("q") query: String): AnimResponse
}