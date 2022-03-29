package com.yang.module_mine.di.module

import android.app.Application
import androidx.lifecycle.ViewModelStoreOwner
import com.yang.lib_common.scope.ActivityScope
import com.yang.module_mine.api.MineApiService
import com.yang.module_mine.di.factory.MineViewModelFactory
import com.yang.module_mine.repository.MineRepository
import dagger.Module
import dagger.Provides
import retrofit2.Retrofit


@Module
class MineModule(private val viewModelStoreOwner: ViewModelStoreOwner) {


    @ActivityScope
    @Provides
    fun provideMineApiService(retrofit: Retrofit): MineApiService = retrofit.create(
        MineApiService::class.java
    )


    @ActivityScope
    @Provides
    fun provideMineRepository(mineApiService: MineApiService): MineRepository =
        MineRepository(mineApiService)

    @ActivityScope
    @Provides
    fun provideMineViewModelFactory(
        application: Application,
        mineRepository: MineRepository
    ): MineViewModelFactory =
        MineViewModelFactory(
            application,
            mineRepository
        )

}