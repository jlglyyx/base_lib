package com.yang.module_login.di.component

import com.yang.lib_common.remote.di.component.RemoteComponent
import com.yang.lib_common.scope.ActivityScope
import com.yang.module_login.di.factory.LoginViewModelFactory
import com.yang.module_login.di.module.LoginModule
import com.yang.module_login.ui.activity.LoginActivity
import com.yang.module_login.ui.activity.RegisterActivity
import com.yang.module_login.ui.activity.SplashActivity
import dagger.Component


@ActivityScope
@Component(modules = [LoginModule::class] ,dependencies = [RemoteComponent::class])
interface LoginComponent {
    fun provideLoginViewModelFactory(): LoginViewModelFactory
    fun inject(loginActivity: LoginActivity)
    fun inject(registerActivity: RegisterActivity)
    fun inject(splashActivity: SplashActivity)
}