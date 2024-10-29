package com.example.listdisplay.di



import android.content.Context
import androidx.room.Room
import com.example.android.architecture.blueprints.todoapp.data.source.local.MyDao
import com.example.android.architecture.blueprints.todoapp.data.source.local.MyDatabase
import com.example.listdisplay.data.api.ApiService
import com.example.listdisplay.data.source.network.INetworkDataSource
import com.example.listdisplay.data.source.network.NetworkDataSource
import com.example.listdisplay.data.respoitory.AnimRepository
import com.example.listdisplay.data.respoitory.IRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton
import javax.inject.Qualifier

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class IoDispatcher

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    private const val BASE_URL = "https://api.jikan.moe/v4/"

    @Provides
    @Singleton
    fun provideRetrofit() : Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideAPI(retrofit: Retrofit) : ApiService {
        return retrofit.create(ApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideNetworkDataSource(apiService: ApiService) : INetworkDataSource {
        return NetworkDataSource(apiService)
    }

    @Provides
    @Singleton
    fun provideRepository(
        source: INetworkDataSource,
        @IoDispatcher ioDispatcher: CoroutineDispatcher,
        myDao: MyDao
    ): IRepository {
        return AnimRepository(source, ioDispatcher, myDao)
    }

    @Provides
    @Singleton
    @IoDispatcher
    fun provideIoDispatcher(): CoroutineDispatcher = Dispatchers.IO

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): MyDatabase {
        return Room.databaseBuilder(context, MyDatabase::class.java, "my_database")
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    fun provideDao(database: MyDatabase): MyDao {
        return database.myDao()
    }
}