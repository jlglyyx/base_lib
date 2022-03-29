@file:JvmName("LoginDaggerHelp")
package com.yang.module_login.helper

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.yang.lib_common.helper.getRemoteComponent
import com.yang.module_login.di.component.DaggerLoginComponent
import com.yang.module_login.di.component.LoginComponent
import com.yang.module_login.di.module.LoginModule


private const val TAG = "LoginDaggerHelp.kt"

fun getLoginComponent(activity: AppCompatActivity): LoginComponent {
    return DaggerLoginComponent.builder().remoteComponent(getRemoteComponent())
        .loginModule(LoginModule()).build()
}
fun getLoginComponent(fragment: Fragment): LoginComponent {
    return DaggerLoginComponent.builder().remoteComponent(getRemoteComponent())
        .loginModule(LoginModule()).build()
}
