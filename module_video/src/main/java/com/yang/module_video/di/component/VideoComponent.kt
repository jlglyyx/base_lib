package com.yang.module_video.di.component

import com.yang.lib_common.remote.di.component.RemoteComponent
import com.yang.lib_common.scope.ActivityScope
import com.yang.module_video.di.factory.VideoViewModelFactory
import com.yang.module_video.di.module.VideoModule
import com.yang.module_video.ui.activity.*
import com.yang.module_video.ui.fragment.VideoFragment
import com.yang.module_video.ui.fragment.VideoItemFragment
import dagger.Component


@ActivityScope
@Component(modules = [VideoModule::class] ,dependencies = [RemoteComponent::class])
interface VideoComponent {

    fun provideVideoViewModelFactory(): VideoViewModelFactory
    fun inject(videoFragment: VideoFragment)
    fun inject(videoItemFragment: VideoItemFragment)
    fun inject(videoItemActivity: VideoItemActivity)
    fun inject(videoSearchActivity: VideoSearchActivity)
    fun inject(videoUploadActivity: VideoUploadActivity)
    fun inject(videoUploadTaskActivity: VideoUploadTaskActivity)
    fun inject(videoScreenActivity: VideoScreenActivity)
}