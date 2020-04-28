package com.example.flex

import android.app.Application
import android.content.Context
import android.widget.ImageView
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.flex.POJO.Comment
import com.example.flex.POJO.Post
import com.example.flex.POJO.PostAccount
import com.example.flex.POJO.User
import com.example.flex.Requests.PhotoRequests
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import java.io.File

class AccountViewModel(private val app: Application) : AndroidViewModel(app) {
    private val mRepository: Repository = Repository(app)
    val allPosts: LiveData<List<Post>>
    private val mMainUser:LiveData<User>
    val isPasswordCanBeChanged: MutableLiveData<Boolean?>
    val isMustSignIn: MutableLiveData<Boolean?>

    init {
        allPosts = mRepository.getAllPosts()
        mMainUser = mRepository.mainUser
        isPasswordCanBeChanged = mRepository.isPasswordCanBeChanged
        isMustSignIn = mRepository.isMustSignIn
    }

    suspend fun getAccountUser(userId: Long): LiveData<User> {
        return mRepository.getAccountUser(userId)
    }
    fun getAllPostsAccount(userId: Long):LiveData<List<PostAccount>>{
        return mRepository.getPostsForAccount(userId)
    }

    suspend fun getCommentsForPost(postid: Long): LiveData<List<Comment>> {
        return mRepository.getCommentsForPost(postid)
    }
    fun uploadUserAvatar(file: File){
        mRepository.uploadUserAvatar(file)
    }
    fun follow(userId: Long){
        mRepository.followUser(userId)
    }
    fun unfollow(userId: Long){
        mRepository.unfollowUser(userId)

    }
    fun insertUser(user:User){
        CoroutineScope(IO).launch {
            mRepository.insertUser(user)
        }
    }

    fun refreshUser(user: User?) {
        CoroutineScope(IO).launch {
            if (user == null) {
                mRepository.refreshMainUser()
            } else {
                mRepository.refreshUser(user)
            }
        }
    }

    fun unLikePost(post: PostAccount) {
        mRepository.unLikePostAccount(post)
    }

    fun likePost(post: PostAccount) {
        mRepository.likePostAccount(post)
    }

    fun downloadPhoto(link: String, photo: ImageView) {
        mRepository.downloadPhoto(link, photo)
    }

    fun getMainUser(): LiveData<User> {
        return mMainUser
    }

    fun getMiniPostsForAcc(id: Long, currentUser: User?) {
        mRepository.getMiniPostsForAcc(id, currentUser)
    }

    fun getPostsForAcc(id: Long) {
        mRepository.getPostsForAcc(id)
    }

    fun uploadPost(file: File, description: String) {
        mRepository.uploadPost(file, description)
    }

    fun checkLog() {
        mRepository.checkLog()
    }

    fun logout() {
        mRepository.logout()
    }

    fun login(login: String, password: String) {
        mRepository.login(login, password)
    }

    fun register(email: String, login: String, password: String) {
        mRepository.register(email, login, password)
    }

    fun forgotPassword(email: String) {
        mRepository.forgotPassword(email)
    }

    fun changePassword(email: String, newPassword: String, checkCode: String) {
        mRepository.changePassword(email, newPassword, checkCode)
    }
}