package vn.linhpv.miniblogapp.datasource

import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import retrofit2.Call
import retrofit2.CallAdapter
import retrofit2.Retrofit
import retrofit2.Retrofit.Builder
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException
import java.lang.reflect.Type


class RetrofitAPI {

    companion object {
        const val BASE_URL = "https://dummyjson.com"

        var instance: RetrofitAPI = RetrofitAPI()
    }

    var userDataSource: UserDataSource? = null
    var postDataSource: PostDataSource? = null

    init {
        val retrofit: Retrofit = Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(CustomCallAdapter.Factory())
            .build()
        userDataSource = retrofit.create(UserDataSource::class.java)
        postDataSource = retrofit.create(PostDataSource::class.java)
    }

}

class CustomCallAdapter<T>(returnType: Type) : CallAdapter<T, T> {
    private val returnType: Type = returnType

    override fun responseType(): Type {
        return returnType
    }

    override fun adapt(call: Call<T>): T? {
        try {
            return call.execute().body()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return null
    }

    class Factory : CallAdapter.Factory() {
        override fun get(
            returnType: Type,
            annotations: Array<out Annotation>,
            retrofit: Retrofit
        ): CallAdapter<*, *> {
            return CustomCallAdapter<Any>(returnType)
        }

    }
}