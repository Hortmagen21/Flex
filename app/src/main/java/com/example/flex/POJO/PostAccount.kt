package com.example.flex.POJO

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey

@Entity(tableName = "posts_account_database")
data class PostAccount(
    @PrimaryKey val id: Long
) {
    @Ignore
    var mainUser: User= User(0, "")
    var imageUrl: String=""
    var imageUrlMini:String=""
    var postText: String=""
    var date: Long = 0
    var countOfFires: Long = 0
    var countOfComments: Long = 0
    var countOfShares: Long = 0

    @Ignore
    var comment: Comment? = null
    var isLiked: Boolean = false
    @ColumnInfo(name = "belongs")
    var belongsTo:Long=0

    constructor(
        id: Long = 0,
        mainUser: User = User(0, ""),
        imageUrl: String = "",
        imageUrlMini: String="",
        postText: String = "",
        date: Long = 0,
        countOfFires: Long = 0,
        countOfComments: Long = 0,
        countOfShares: Long = 0,
        comment: Comment? = null,
        isLiked: Boolean = false,
        belongsTo:Long=0
    ) : this(id){
        this.mainUser=mainUser
        this.imageUrl=imageUrl
        this.postText=postText
        this.date=date
        this.countOfFires=countOfFires
        this.countOfComments=countOfComments
        this.countOfShares=countOfShares
        this.comment=comment
        this.isLiked=isLiked
        this.imageUrlMini=imageUrlMini
        this.belongsTo=belongsTo
    }
    fun toPost():Post{
        return Post(
            postText =postText,
            mainUser = mainUser,
            imageUrl = imageUrl,
            imageUrlMini = imageUrlMini,
            isLiked = isLiked,
            date = date,
            id = id,
            countOfComments = countOfComments,
            countOfFires = countOfFires,
            comment = comment,
            countOfShares = countOfShares
        )
    }
}