package com.yang.module_picture.di.module
import android.app.Application
import androidx.lifecycle.ViewModelStoreOwner
import com.yang.lib_common.scope.ActivityScope
import com.yang.lib_common.util.getViewModel
import com.yang.module_picture.api.PictureApiService
import com.yang.module_picture.di.factory.PictureViewModelFactory
import com.yang.module_picture.repository.PictureRepository
import com.yang.module_picture.viewmodel.PictureViewModel
import dagger.Module
import dagger.Provides
import retrofit2.Retrofit


@Module
class PictureModule(private val viewModelStoreOwner: ViewModelStoreOwner) {


    @ActivityScope
    @Provides
    fun providePictureApi(retrofit: Retrofit): PictureApiService = retrofit.create(
        PictureApiService::class.java)


    @ActivityScope
    @Provides
    fun providePictureRepository(mainApiService: PictureApiService): PictureRepository =
        PictureRepository(mainApiService)

    @ActivityScope
    @Provides
    fun providePictureViewModelFactory(
        application: Application,
        pictureRepository: PictureRepository
    ): PictureViewModelFactory =
        PictureViewModelFactory(
            application,
            pictureRepository
        )

    @ActivityScope
    @Provides

    fun provideModelWithFactory(
        pictureViewModelFactory: PictureViewModelFactory
    ): PictureViewModel =
        getViewModel(viewModelStoreOwner, pictureViewModelFactory, PictureViewModel::class.java)

//    @ActivityScope
//    @Provides
//    @ModelNoFactory
//    fun provideModelNoFactory(
//    ): PictureViewModel = getViewModel(viewModelStoreOwner,PictureViewModel::class.java)
}