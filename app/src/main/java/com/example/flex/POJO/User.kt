package com.example.flex.POJO

import android.os.Parcel
import android.os.Parcelable

data class User(
    val id: Long,
    val name: String,
    val imageUrl: String,
    val followersCount: Long,
    val followingCount: Long
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readLong(),
        parcel.readString(),
        parcel.readString(),
        parcel.readLong(),
        parcel.readLong()
    ) {
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as User

        if (id != other.id) return false
        if (name != other.name) return false
        if (imageUrl != other.imageUrl) return false
        if (followersCount != other.followersCount) return false
        if (followingCount != other.followingCount) return false

        return true
    }

    override fun writeToParcel(p0: Parcel?, p1: Int) {
        p0!!.writeLong(id)
        p0.writeString(name)
        p0.writeString(imageUrl)
        p0.writeLong(followersCount)
        p0.writeLong(followingCount)
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + name.hashCode()
        result = 31 * result + imageUrl.hashCode()
        result = 31 * result + followersCount.hashCode()
        result = 31 * result + followingCount.hashCode()
        return result
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<User> {
        override fun createFromParcel(parcel: Parcel): User {
            return User(parcel)
        }

        override fun newArray(size: Int): Array<User?> {
            return arrayOfNulls(size)
        }
    }
}