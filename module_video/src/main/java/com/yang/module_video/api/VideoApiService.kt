package com.yang.module_video.api

import com.yang.lib_common.api.BaseApiService
import com.yang.lib_common.remote.di.response.MResult
import com.yang.lib_common.room.entity.VideoData
import com.yang.lib_common.room.entity.VideoDataItem
import com.yang.lib_common.room.entity.VideoTypeData
import retrofit2.http.POST
import retrofit2.http.Query
import retrofit2.http.QueryMap

interface VideoApiService : BaseApiService {


    @POST("video/queryVideo")
    suspend fun getVideoInfo(@QueryMap map: MutableMap<String, Any>): MResult<VideoData>

    @POST("video/queryVideoItem")
    suspend fun getVideoItemData(@Query("sid") sid: String): MResult<MutableList<VideoDataItem>>

    @POST("video/queryVideoType")
    suspend fun getVideoTypeData(): MResult<MutableList<VideoTypeData>>

    @POST("user/dynamic/insertViewHistory")
    suspend fun insertViewHistory(@Query("id") id: String, @Query("type") type: String): MResult<String>

    @POST("user/insertComment")
    suspend fun insertComment(@QueryMap params: Map<String, String>): MResult<String>
}