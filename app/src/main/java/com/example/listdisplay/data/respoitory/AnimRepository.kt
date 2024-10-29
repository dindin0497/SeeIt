package com.example.listdisplay.data.respoitory

import com.example.android.architecture.blueprints.todoapp.data.source.local.MyDao
import com.example.listdisplay.data.Result
import com.example.listdisplay.data.model.AnimData
import com.example.listdisplay.data.source.local.MyData
import com.example.listdisplay.data.source.local.toLocal
import com.example.listdisplay.data.source.network.INetworkDataSource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject

class AnimRepository @Inject constructor(
    private val networkDataSource: INetworkDataSource,
    private val dispatcher: CoroutineDispatcher,
    private val dao: MyDao
): IRepository {

    override suspend fun getData(name: String): Result<List<MyData>> {
        return withContext(dispatcher) {
             try {
                val data = networkDataSource.get(name).data.toLocal()
                 saveToLocal(data)
                 Result.Success(data)
            } catch (e: Exception) {
                 e.printStackTrace()
                 val localData = dao.getAll()
                 if (localData.isNotEmpty())
                     Result.Success(localData)
                  else
                      Result.Error("${e.message}", e)
            }
        }
    }

    private suspend fun saveToLocal(data: List<MyData>) {
        dao.insertAll(data)
    }
}