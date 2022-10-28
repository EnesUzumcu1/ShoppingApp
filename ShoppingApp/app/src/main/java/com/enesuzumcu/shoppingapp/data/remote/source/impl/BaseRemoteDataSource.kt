package com.enesuzumcu.shoppingapp.data.remote.source.impl

import com.enesuzumcu.shoppingapp.data.remote.utils.DataState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import retrofit2.Response

open class BaseRemoteDataSource {
    suspend fun <T> getResult(call: suspend () -> Response<T>): Flow<DataState<T>> {
        return flow<DataState<T>>{
            val response = call()

            if (response.isSuccessful){
                val body = response.body()

                if(body!= null) emit(DataState.Success(body))
                else{
                    val apiError: String = "Response is not found"
                    emit(DataState.Error(apiError))
                }
            } else {
                val apiError:String = "Response failed"
                emit(DataState.Error(apiError))
            }
        }.catch { e->
            emit(DataState.Error(e.message ?: "Unknown Error"))
        }.onStart { emit(DataState.Loading()) }
            .flowOn(Dispatchers.IO)
    }
}