package com.yang.module_mine.data

import android.os.Parcel
import android.os.Parcelable

/**
 * @Author Administrator
 * @ClassName MineShippingAddressData
 * @Description
 * @Date 2021/9/26 14:21
 */
data class MineShippingAddressData(
    var type: String?,
    var address: String?,
    var name: String?,
    var phone: String?,
    var default:Boolean) :
    Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readByte() != 0.toByte()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(type)
        parcel.writeString(address)
        parcel.writeString(name)
        parcel.writeString(phone)
        parcel.writeByte(if (default) 1 else 0)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<MineShippingAddressData> {
        override fun createFromParcel(parcel: Parcel): MineShippingAddressData {
            return MineShippingAddressData(parcel)
        }

        override fun newArray(size: Int): Array<MineShippingAddressData?> {
            return arrayOfNulls(size)
        }
    }
}