package com.yang.module_login.viewmodel

import android.app.Application
import android.text.TextUtils
import androidx.lifecycle.MutableLiveData
import com.yang.lib_common.base.viewmodel.BaseViewModel
import com.yang.lib_common.constant.AppConstant
import com.yang.lib_common.room.BaseAppDatabase
import com.yang.lib_common.room.entity.UserInfoData
import com.yang.lib_common.util.buildARouter
import com.yang.lib_common.util.getDefaultMMKV
import com.yang.module_login.R
import com.yang.module_login.repository.LoginRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.*
import javax.inject.Inject

/**
 * ClassName: LoginViewModel.
 * Created by Administrator on 2021/4/1_15:15.
 * Describe:
 */
class LoginViewModel @Inject constructor(
    application: Application,
    private val loginRepository: LoginRepository
) : BaseViewModel(application) {

    var mUserInfoData = MutableLiveData<UserInfoData>()

    fun login(userAccount: String, password: String) {
        launch({
            loginRepository.login(userAccount, password)
        }, {
            getDefaultMMKV().encode(AppConstant.Constant.TOKEN, it.data.token)
            mUserInfoData.postValue(it.data)
        },{
            withContext(Dispatchers.IO){
                val queryUserInfoData = BaseAppDatabase.instance.userInfoDao().queryDataByAccount(userAccount)
                if (null != queryUserInfoData){
                    if (!TextUtils.equals(queryUserInfoData.userPassword,password)){
                        withContext(Dispatchers.Main){
                            showDialog("密码错误")
                            delayMissDialog()
                        }
                        return@withContext
                    }
                    val token = UUID.randomUUID().toString()
                    getDefaultMMKV().encode(AppConstant.Constant.TOKEN, token)
                    queryUserInfoData.token = token
                    BaseAppDatabase.instance.userInfoDao().updateData(queryUserInfoData)
                    mUserInfoData.postValue(queryUserInfoData)
                }else{
                    withContext(Dispatchers.Main){
                        showDialog("用户不存在")
                        delayMissDialog()
                    }
                }
            }
        }, messages = *arrayOf(getString(R.string.string_login_ing),getString(R.string.string_login_success)))
    }
    fun splashLogin(userAccount: String, password: String) {
        launch({
            loginRepository.login(userAccount, password)
        }, {
            getDefaultMMKV().encode(AppConstant.Constant.TOKEN, it.data.token)
            mUserInfoData.postValue(it.data)
        },{
            withContext(Dispatchers.IO){
                val queryUserInfoData = BaseAppDatabase.instance.userInfoDao().queryDataByAccount(userAccount)
                if (null != queryUserInfoData){
                    if (!TextUtils.equals(queryUserInfoData.userPassword,password)){
                        withContext(Dispatchers.Main){
                            buildARouter(AppConstant.RoutePath.LOGIN_ACTIVITY).navigation()
                            finishActivity()
                        }
                        return@withContext
                    }
                    val token = UUID.randomUUID().toString()
                    getDefaultMMKV().encode(AppConstant.Constant.TOKEN, token)
                    queryUserInfoData.token = token
                    BaseAppDatabase.instance.userInfoDao().updateData(queryUserInfoData)
                    mUserInfoData.postValue(queryUserInfoData)
                }else{
                    withContext(Dispatchers.Main){
                        buildARouter(AppConstant.RoutePath.LOGIN_ACTIVITY).navigation()
                        finishActivity()
                    }
                }
            }
        })
    }

    fun register(userInfoData: UserInfoData) {
        launch({
            loginRepository.register(userInfoData)
        }, {
            mUserInfoData.postValue(it.data)
        },{
          withContext(Dispatchers.IO){
              val queryUserInfoData = BaseAppDatabase.instance.userInfoDao().queryDataByAccount(userInfoData.userAccount)
              if (null == queryUserInfoData){
                  BaseAppDatabase.instance.userInfoDao().insertData(userInfoData)
                  mUserInfoData.postValue(queryUserInfoData)
              }else{
                  withContext(Dispatchers.Main){
                      showDialog("用户已经存在")
                      delayMissDialog()
                  }
              }
          }
        }, messages = *arrayOf(getString(R.string.string_register_ing),getString(R.string.string_register_success)))
    }

}

