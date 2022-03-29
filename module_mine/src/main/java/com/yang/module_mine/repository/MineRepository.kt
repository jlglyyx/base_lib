package com.yang.module_mine.repository

import com.yang.lib_common.base.repository.BaseRepository
import com.yang.lib_common.room.entity.UserInfoData
import com.yang.lib_common.remote.di.response.MResult
import com.yang.lib_common.room.entity.MineGoodsDetailData
import com.yang.module_mine.api.MineApiService
import com.yang.module_mine.data.MineExtensionTurnoverData
import com.yang.module_mine.data.MineObtainTurnoverData
import com.yang.module_mine.data.MineSignTurnoverData
import com.yang.module_mine.data.MineViewHistoryData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.RequestBody
import javax.inject.Inject

class MineRepository @Inject constructor(private val mineApiService: MineApiService) : BaseRepository() {

    suspend fun login(userAccount: String, password: String): MResult<UserInfoData> {

        return withContextIO{
            mineApiService.login(userAccount, password)
        }
    }

    suspend fun register(userInfoData: UserInfoData): MResult<UserInfoData> {

        return withContextIO{
            mineApiService.register(userInfoData)
        }

    }

    suspend fun uploadFile(filePaths: MutableMap<String, RequestBody>): MResult<MutableList<String>> {
        return withContext(Dispatchers.IO) {
            mineApiService.uploadFile(filePaths)
        }
    }

    suspend fun changePassword(password: String): MResult<String> {
        return withContextIO{
            mineApiService.changePassword(password)
        }
    }
    suspend fun changeUserInfo(userInfoData: UserInfoData): MResult<UserInfoData> {
        return withContextIO{
            mineApiService.changeUserInfo(userInfoData)
        }
    }
    suspend fun queryViewHistory(): MResult<MutableList<MineViewHistoryData>> {
        return withContextIO{
            mineApiService.queryViewHistory()
        }
    }

    suspend fun queryObtainTurnover(pageNum:Int): MResult<MutableList<MineObtainTurnoverData>> {
        return withContext(Dispatchers.IO) {
            mineApiService.queryObtainTurnover(pageNum)
        }
    }
//    suspend fun queryObtainTurnover(pageNum:Int): MResult<MutableList<MineTurnoverDataa>> {
//        return withContextIO{
//            mineApiService.queryObtainTurnover(pageNum)
//        }
//    }

    suspend fun querySignTurnover(): MResult<MutableList<MineSignTurnoverData>> {
        return withContextIO{
            mineApiService.querySignTurnover()
        }
    }

    suspend fun queryExtensionTurnover(): MResult<MutableList<MineExtensionTurnoverData>> {
        return withContextIO{
            mineApiService.queryExtensionTurnover()
        }
    }
    suspend fun queryGoodsList(): MResult<MutableList<MineGoodsDetailData>> {
        return withContextIO{
            mineApiService.queryGoodsList()
        }
    }
    suspend fun createGoods(): MResult<MineGoodsDetailData> {
        return withContextIO{
            mineApiService.createGoods()
        }
    }
    suspend fun exchangeGoods(): MResult<MineGoodsDetailData> {
        return withContextIO{
            mineApiService.exchangeGoods()
        }
    }

}