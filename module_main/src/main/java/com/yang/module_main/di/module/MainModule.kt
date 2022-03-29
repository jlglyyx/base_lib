package com.yang.module_main.di.module

import android.app.Application
import androidx.lifecycle.ViewModelStoreOwner
import com.yang.lib_common.scope.ActivityScope
import com.yang.module_main.api.MainApiService
import com.yang.module_main.di.factory.MainViewModelFactory
import com.yang.module_main.repository.MainRepository
import dagger.Module
import dagger.Provides
import retrofit2.Retrofit


@Module
class MainModule(private val viewModelStoreOwner: ViewModelStoreOwner) {


    @ActivityScope
    @Provides
    fun provideMainApi(retrofit: Retrofit): MainApiService = retrofit.create(
        MainApiService::class.java
    )


    @ActivityScope
    @Provides
    fun provideMainRepository(mainApiService: MainApiService): MainRepository =
        MainRepository(mainApiService)

    @ActivityScope
    @Provides
    fun provideMainViewModelFactory(
        application: Application,
        mainRepository: MainRepository
    ): MainViewModelFactory =
        MainViewModelFactory(
            application,
            mainRepository
        )

}