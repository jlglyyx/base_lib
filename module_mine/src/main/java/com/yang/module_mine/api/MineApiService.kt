package com.yang.module_mine.api

import com.yang.lib_common.api.BaseApiService
import com.yang.lib_common.room.entity.UserInfoData
import com.yang.lib_common.remote.di.response.MResult
import com.yang.lib_common.room.entity.MineGoodsDetailData
import com.yang.module_mine.data.MineExtensionTurnoverData
import com.yang.module_mine.data.MineObtainTurnoverData
import com.yang.module_mine.data.MineSignTurnoverData
import com.yang.module_mine.data.MineViewHistoryData
import okhttp3.RequestBody
import retrofit2.http.*

interface MineApiService : BaseApiService {

    @POST("user/login")
    suspend fun login(@Query("userAccount") userAccount:String, @Query("password") password:String): MResult<UserInfoData>

    @POST("user/register")
    suspend fun register(@Body userInfoData: UserInfoData): MResult<UserInfoData>

    @Multipart
    @POST("/uploadFile")
    suspend fun uploadFile(@PartMap file: MutableMap<String, RequestBody>): MResult<MutableList<String>>

    @POST("user/changePassword")
    suspend fun changePassword(@Query("password") password:String): MResult<String>

    @POST("user/changeUserInfo")
    suspend fun changeUserInfo(@Body userInfoData: UserInfoData): MResult<UserInfoData>

    @POST("user/queryViewHistory")
    suspend fun queryViewHistory(): MResult<MutableList<MineViewHistoryData>>

    @GET("article/list/{pageNum}/json?page_size=40")
    suspend fun queryObtainTurnover(@Path("pageNum") pageNum:Int): MResult<MutableList<MineObtainTurnoverData>>

    @POST("user/querySignTurnover")
    suspend fun querySignTurnover(): MResult<MutableList<MineSignTurnoverData>>

    @POST("user/queryExtensionTurnover")
    suspend fun queryExtensionTurnover(): MResult<MutableList<MineExtensionTurnoverData>>

    @POST("user/queryGoodsList")
    suspend fun queryGoodsList(): MResult<MutableList<MineGoodsDetailData>>

    @POST("user/createGoods")
    suspend fun createGoods(): MResult<MineGoodsDetailData>

    @POST("user/exchangeGoods")
    suspend fun exchangeGoods(): MResult<MineGoodsDetailData>

}