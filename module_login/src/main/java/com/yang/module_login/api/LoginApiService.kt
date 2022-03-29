package com.yang.module_login.api

import com.yang.lib_common.api.BaseApiService
import com.yang.lib_common.room.entity.UserInfoData
import com.yang.lib_common.remote.di.response.MResult
import retrofit2.http.*

interface LoginApiService : BaseApiService {

    @POST("user/login")
    suspend fun login(@Query("userAccount") userAccount:String, @Query("password") password:String): MResult<UserInfoData>

    @POST("user/register")
    suspend fun register(@Body userInfoData: UserInfoData): MResult<UserInfoData>
}