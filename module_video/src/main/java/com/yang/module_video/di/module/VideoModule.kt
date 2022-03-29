package com.yang.module_video.di.module
import android.app.Application
import androidx.lifecycle.ViewModelStoreOwner
import com.yang.lib_common.scope.ActivityScope
import com.yang.lib_common.util.getViewModel
import com.yang.module_video.api.VideoApiService
import com.yang.module_video.di.factory.VideoViewModelFactory
import com.yang.module_video.repository.VideoRepository
import com.yang.module_video.viewmodel.VideoViewModel
import dagger.Module
import dagger.Provides
import retrofit2.Retrofit


@Module
class VideoModule(private val viewModelStoreOwner: ViewModelStoreOwner) {


    @ActivityScope
    @Provides
    fun provideVideoApi(retrofit: Retrofit): VideoApiService = retrofit.create(
        VideoApiService::class.java)


    @ActivityScope
    @Provides
    fun provideVideoRepository(mainApiService: VideoApiService): VideoRepository =
        VideoRepository(mainApiService)

    @ActivityScope
    @Provides
    fun provideVideoViewModelFactory(
        application: Application,
        videoRepository: VideoRepository
    ): VideoViewModelFactory =
        VideoViewModelFactory(
            application,
            videoRepository
        )
    @ActivityScope
    @Provides

    fun provideModelWithFactory(
        videoViewModelFactory: VideoViewModelFactory
    ): VideoViewModel =
        getViewModel(viewModelStoreOwner, videoViewModelFactory, VideoViewModel::class.java)

//    @ActivityScope
//    @Provides
//    @ModelNoFactory
//    fun provideModelNoFactory(
//    ): VideoViewModel = getViewModel(viewModelStoreOwner,VideoViewModel::class.java)

}