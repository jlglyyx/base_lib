@file:JvmName("PictureDaggerHelp")
package com.yang.module_picture.helper

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.yang.lib_common.helper.getRemoteComponent
import com.yang.module_picture.di.component.DaggerPictureComponent
import com.yang.module_picture.di.component.PictureComponent
import com.yang.module_picture.di.module.PictureModule


private const val TAG = "PictureDaggerHelp.kt"

fun getPictureComponent(activity: AppCompatActivity): PictureComponent {
    return DaggerPictureComponent.builder().remoteComponent(getRemoteComponent())
        .pictureModule(PictureModule(activity)).build()
}
fun getPictureComponent(fragment: Fragment): PictureComponent {
    return DaggerPictureComponent.builder().remoteComponent(getRemoteComponent())
        .pictureModule(PictureModule(fragment)).build()
}

