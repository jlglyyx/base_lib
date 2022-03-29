package com.yang.module_video.repository

import com.yang.lib_common.base.repository.BaseRepository
import com.yang.lib_common.remote.di.response.MResult
import com.yang.lib_common.room.entity.VideoData
import com.yang.lib_common.room.entity.VideoDataItem
import com.yang.lib_common.room.entity.VideoTypeData
import com.yang.module_video.api.VideoApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class VideoRepository @Inject constructor(private val videoApiService: VideoApiService) :
    BaseRepository() {


    suspend fun getVideoInfo(map: MutableMap<String, Any>): MResult<VideoData> {
        return withContextIO {
            videoApiService.getVideoInfo(map)
        }
    }

    suspend fun getVideoItemData(sid: String): MResult<MutableList<VideoDataItem>> {
        return withContextIO {
            videoApiService.getVideoItemData(sid)
        }
    }

    suspend fun getVideoTypeData(): MResult<MutableList<VideoTypeData>> {
        return withContextIO {
            videoApiService.getVideoTypeData()
        }
    }


    suspend fun insertViewHistory(id: String, type: String): MResult<String> {
        return withContext(Dispatchers.IO) {
            videoApiService.insertViewHistory(id, type)
        }
    }

    suspend fun insertComment(params: Map<String, String>): MResult<String> {
        return withContext(Dispatchers.IO) {
            videoApiService.insertComment(params)
        }
    }

}