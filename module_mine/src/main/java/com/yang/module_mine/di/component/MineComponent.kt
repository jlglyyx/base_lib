package com.yang.module_mine.di.component

import com.yang.lib_common.remote.di.component.RemoteComponent
import com.yang.lib_common.scope.ActivityScope
import com.yang.module_mine.di.factory.MineViewModelFactory
import com.yang.module_mine.di.module.MineModule
import com.yang.module_mine.ui.activity.*
import com.yang.module_mine.ui.fragment.MineFragment
import com.yang.module_mine.ui.obtain.activity.*
import com.yang.module_mine.ui.obtain.fragment.MineExchangeStatusFragment
import dagger.Component


@ActivityScope
@Component(modules = [MineModule::class] ,dependencies = [RemoteComponent::class])
interface MineComponent {
    fun provideMineViewModelFactory(): MineViewModelFactory

    fun inject(mineChangeUserInfoActivity: MineChangeUserInfoActivity)
    fun inject(mineOtherPersonInfoActivity: MineOtherPersonInfoActivity)
    fun inject(mineViewHistoryActivity: MineViewHistoryActivity)
    fun inject(mineObtainTurnoverActivity: MineObtainTurnoverActivity)
    fun inject(mineObtainExchangeActivity: MineObtainExchangeActivity)
    fun inject(mineExchangeActivity: MineExchangeActivity)
    fun inject(mineGoodsDetailActivity: MineGoodsDetailActivity)
    fun inject(mineSignTurnoverActivity: MineSignTurnoverActivity)
    fun inject(mineExtensionTurnoverActivity: MineExtensionTurnoverActivity)
    fun inject(mineExtensionQRCodeActivity: MineExtensionQRCodeActivity)
    fun inject(mineAddAddressActivity: MineAddAddressActivity)
    fun inject(mineChangePasswordActivity: MineChangePasswordActivity)
    fun inject(mineCreateOrderActivity: MineCreateOrderActivity)
    fun inject(mineObtainTaskActivity: MineObtainTaskActivity)


    fun inject(mineFragment: MineFragment)
    fun inject(mineExchangeStatusFragment: MineExchangeStatusFragment)
}