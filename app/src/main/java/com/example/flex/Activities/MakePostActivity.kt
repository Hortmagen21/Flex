package com.example.flex.Activities

import android.app.Activity
import android.content.Intent
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModelProvider
import com.example.flex.AccountViewModel
import com.example.flex.R
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class MakePostActivity : AppCompatActivity() {
    private lateinit var mPathToFile:String
    private val GALLERY_REQUEST_CODE = 200
    private lateinit var mImage:ImageView
    private lateinit var mAccountViewModel: AccountViewModel
    private lateinit var mFile: File
    private lateinit var mTakePictureBtn:Button
    private lateinit var mGetPictureBtn:Button
    private lateinit var mSubmitPostBtn:Button
    private var mDescription:String=""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_make_post)
        mAccountViewModel=ViewModelProvider(this).get(AccountViewModel::class.java)
        setOnClickListeners()
    }
    private fun setOnClickListeners(){
        val postText=findViewById<EditText>(R.id.publish_post_text)
        mImage=findViewById(R.id.publish_post_image)
        mTakePictureBtn=findViewById(R.id.button_take_picture)
        mGetPictureBtn=findViewById(R.id.button_get_picture)
        mSubmitPostBtn=findViewById(R.id.button_submit_post)
        mTakePictureBtn.setOnClickListener {
            takePicture()
        }
        mGetPictureBtn.setOnClickListener {
            getPictureFromGallery()
        }
        mSubmitPostBtn.setOnClickListener {
            mDescription=postText.text.toString()
            mAccountViewModel.uploadPost(mFile,mDescription)
            finish()
        }
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                100 -> {
                    val captureImage = BitmapFactory.decodeFile(mPathToFile)
                    mImage.setImageBitmap(captureImage)
                    mFile = File(mPathToFile)
                    mTakePictureBtn.visibility= View.GONE
                    mGetPictureBtn.visibility=View.GONE
                    mSubmitPostBtn.visibility=View.VISIBLE
                }
                GALLERY_REQUEST_CODE -> {
                    if (data != null) {
                        val selectedImageUri = data.data
                        mImage.setImageURI(selectedImageUri)
                        val pathList = selectedImageUri.pathSegments
                        val imagePath = pathList[1]
                        mFile = File(imagePath)
                        mTakePictureBtn.visibility= View.GONE
                        mGetPictureBtn.visibility=View.GONE
                        mSubmitPostBtn.visibility=View.VISIBLE
                    }
                }
            }
        }
    }

    private fun getPictureFromGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        val mimeTypes = arrayOf("image/jpeg", "image/png")
        intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes)
        startActivityForResult(intent, GALLERY_REQUEST_CODE)
    }

    private fun takePicture() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (intent.resolveActivity(this.packageManager) != null) {
            val photoFile: File? = createPhotoFile()
            if (photoFile != null) {
                mPathToFile = photoFile.absolutePath
                val photoURI = FileProvider.getUriForFile(
                    this,
                    "com.example.flex.fileprovider",
                    photoFile
                )
                intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                startActivityForResult(intent, 100)
            }
        }
    }

    private fun createPhotoFile(): File? {
        val name: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val storageDir =
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
        var file: File? = null
        try {
            file =
                File.createTempFile(name, ".jpg", storageDir)
        } catch (e: Exception) {
            Log.d("asdf", e.toString())
        }
        return file
    }
}
