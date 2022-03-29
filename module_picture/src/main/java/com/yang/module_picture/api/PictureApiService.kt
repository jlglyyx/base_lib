package com.yang.module_picture.api

import com.yang.lib_common.api.BaseApiService
import com.yang.lib_common.remote.di.response.MResult
import com.yang.lib_common.room.entity.ImageData
import com.yang.lib_common.room.entity.ImageDataItem
import com.yang.lib_common.room.entity.ImageTypeData
import retrofit2.http.POST
import retrofit2.http.Query
import retrofit2.http.QueryMap

interface PictureApiService : BaseApiService {

    @POST("image/queryImage")
    suspend fun getImageInfo(@QueryMap map: MutableMap<String, Any>): MResult<ImageData>

    @POST("image/queryImageItem")
    suspend fun getImageItemData(@Query("sid") sid: String): MResult<MutableList<ImageDataItem>>

    @POST("image/queryImageType")
    suspend fun getImageTypeData(): MResult<MutableList<ImageTypeData>>

    @POST("user/addViewHistory")
    suspend fun addViewHistory(@Query("id") id: String, @Query("type") type: String): MResult<String>

    @POST("user/insertComment")
    suspend fun insertComment(@QueryMap params: Map<String, String>): MResult<String>

}