package com.example.flex.POJO

class User {
    var id:Long
    private set
    var name:String
    private set
    var imageUrl:String
    private set

    constructor(id: Long, name: String, imageUrl: String) {
        this.id = id
        this.name = name
        this.imageUrl = imageUrl
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as User

        if (id != other.id) return false
        if (name != other.name) return false
        if (imageUrl != other.imageUrl) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + name.hashCode()
        result = 31 * result + imageUrl.hashCode()
        return result
    }
}