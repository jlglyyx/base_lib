package com.yang.module_main.di.component

import com.yang.lib_common.remote.di.component.RemoteComponent
import com.yang.lib_common.scope.ActivityScope
import com.yang.module_main.di.factory.MainViewModelFactory
import com.yang.module_main.di.module.MainModule
import com.yang.module_main.ui.main.activity.AddDynamicActivity
import com.yang.module_main.ui.main.activity.DynamicDetailActivity
import com.yang.module_main.ui.main.activity.MainActivity
import com.yang.module_main.ui.main.fragment.MainFragment
import com.yang.module_main.ui.menu.activity.AccountSettingActivity
import com.yang.module_main.ui.menu.activity.MyPushActivity
import com.yang.module_main.ui.menu.activity.WebViewActivity
import com.yang.module_main.ui.menu.fragment.LeftFragment
import com.yang.module_main.ui.menu.fragment.MyCollectionFragment
import com.yang.module_main.ui.menu.fragment.MyDownLoadFragment
import dagger.Component


@ActivityScope
@Component(modules = [MainModule::class] ,dependencies = [RemoteComponent::class])
interface MainComponent {
    fun provideMainViewModelFactory(): MainViewModelFactory
    fun inject(mainActivity: MainActivity)
    fun inject(mainFragment: MainFragment)
    fun inject(myPushActivity: MyPushActivity)
    fun inject(addDynamicActivity: AddDynamicActivity)
    fun inject(dynamicDetailActivity: DynamicDetailActivity)
    fun inject(myCollectionFragment: MyCollectionFragment)
    fun inject(myDownLoadFragment: MyDownLoadFragment)
    fun inject(webViewActivity: WebViewActivity)
    fun inject(accountSettingActivity: AccountSettingActivity)

    fun inject(leftFragment: LeftFragment)
}