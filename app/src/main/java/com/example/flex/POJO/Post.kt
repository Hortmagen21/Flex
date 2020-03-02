package com.example.flex.POJO

class Post {
    var id:Long
    private set
    var countOfFires:Long

    constructor(
        id: Long,
        countOfFires: Long,
        countOfComments: Long,
        countOfShares: Long,
        mainUser: User,
        imageUrl: String,
        commentUser: User,
        commentText: String
    ) {
        this.id = id
        this.countOfFires = countOfFires
        this.countOfComments = countOfComments
        this.countOfShares = countOfShares
        this.mainUser = mainUser
        this.imageUrl = imageUrl
        this.commentUser = commentUser
        this.commentText = commentText
    }

    var countOfComments:Long
    var countOfShares:Long
    var mainUser:User
    var imageUrl:String
    var commentUser:User
    var commentText:String
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Post

        if (id != other.id) return false
        if (countOfFires != other.countOfFires) return false
        if (countOfComments != other.countOfComments) return false
        if (countOfShares != other.countOfShares) return false
        if (mainUser != other.mainUser) return false
        if (imageUrl != other.imageUrl) return false
        if (commentUser != other.commentUser) return false
        if (commentText != other.commentText) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + countOfFires.hashCode()
        result = 31 * result + countOfComments.hashCode()
        result = 31 * result + countOfShares.hashCode()
        result = 31 * result + mainUser.hashCode()
        result = 31 * result + imageUrl.hashCode()
        result = 31 * result + commentUser.hashCode()
        result = 31 * result + commentText.hashCode()
        return result
    }

}