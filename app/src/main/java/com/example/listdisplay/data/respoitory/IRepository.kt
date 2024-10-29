package com.example.listdisplay.data.respoitory

import com.example.listdisplay.data.Result
import com.example.listdisplay.data.model.AnimData
import com.example.listdisplay.data.source.local.MyData

interface IRepository {
    suspend fun getData(name: String): Result<List<MyData>>
}