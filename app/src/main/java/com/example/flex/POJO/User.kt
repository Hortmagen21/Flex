package com.example.flex.POJO

data class User(
    val id: Long,
    val name: String
) {
    var imageUrl: String = ""
    var followersCount: Long = 0
    var followingCount: Long = 0

    constructor(
        id: Long,
        name: String,
        imageUrl: String,
        followersCount: Long,
        followedCount: Long
    ) : this(id, name) {
        this.imageUrl = imageUrl
        this.followersCount = followersCount
        this.followingCount = followedCount
    }

}