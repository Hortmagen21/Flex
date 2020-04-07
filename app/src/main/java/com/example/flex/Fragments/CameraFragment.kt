package com.example.flex.Fragments

import android.app.Activity.RESULT_OK
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.Environment.getExternalStoragePublicDirectory
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import com.example.flex.*
import com.example.flex.Requests.PhotoRequests
import com.example.flex.Requests.RegistRequests
import com.example.flex.Requests.UploadFileRequests
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class CameraFragment : Fragment() {
    lateinit var v: View
    private val GALLERY_REQUEST_CODE = 200
    lateinit var image: ImageView
    private lateinit var pathToFile: String
    private var request:PhotoRequests?=null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        v = inflater.inflate(R.layout.fragment_photo, container, false)
        if (Build.VERSION.SDK_INT >= 23) {
            requestPermissions(
                arrayOf(
                    android.Manifest.permission.CAMERA,
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE
                ), 201
            )
        }
        addActionListener()
        return v
    }
    override fun onDestroyView() {
        super.onDestroyView()
        if (request != null) {
            request!!.stopRequests()
        }
    }
    override fun onDestroy() {
        super.onDestroy()
        if (request != null) {
            request!!.stopRequests()
        }
    }

    override fun onPause() {
        super.onPause()
        if (request != null) {
            request!!.stopRequests()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        if (resultCode == RESULT_OK) {
            when (requestCode) {
                100 -> {
                    val captureImage = BitmapFactory.decodeFile(pathToFile)
                    image.setImageBitmap(captureImage)
                    val imageFile = File(pathToFile)
                    uploadFile(imageFile)
                }
                GALLERY_REQUEST_CODE -> {
                    if (data != null) {
                        val selectedImageUri = data.data
                        image.setImageURI(selectedImageUri)
                        val pathList = selectedImageUri.pathSegments
                        val imagePath = pathList[1]
                        val imageFile = File(imagePath)
                        uploadFile(imageFile)
                    }
                }
            }
        }
    }

    fun addActionListener() {
        image = v.findViewById(R.id.imageView)

        val btnRegistration = v.findViewById<Button>(R.id.button_registration)
        val btnLogIn = v.findViewById<Button>(R.id.button_login)
        val btnLogout = v.findViewById<Button>(R.id.button_logout)
        val btnCheckLog = v.findViewById<Button>(R.id.button_checkLog)
        val changePassBtn = v.findViewById<Button>(R.id.button_change_pass)
        val takePicture = v.findViewById<Button>(R.id.button_take_picture)
        val getPicture = v.findViewById<Button>(R.id.button_get_picture)
        if (this.context?.let {
                ContextCompat.checkSelfPermission(
                    it,
                    android.Manifest.permission.CAMERA
                )
            } != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                this.activity as AppCompatActivity,
                arrayOf(android.Manifest.permission.CAMERA),
                100
            )
        }
        getPicture.setOnClickListener {
            getPictureFromGallery()
        }
        takePicture.setOnClickListener {
            takePicture()
        }
        btnCheckLog.setOnClickListener {
            val request=makePostRequest()
            request.callCheckLog()
        }
        btnLogout.setOnClickListener {
            val request=makePostRequest()
            request.logout()
        }
        changePassBtn.setOnClickListener {
            val intent = Intent(v.context, ForgotPass().javaClass)
            startActivity(intent)
        }
        btnRegistration.setOnClickListener {
            val intent = Intent(v.context, Registration().javaClass)
            startActivity(intent)
        }
        btnLogIn.setOnClickListener {
            val intent = Intent(v.context, SignIn().javaClass)
            startActivity(intent)
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
        if (intent.resolveActivity(this.context?.packageManager) != null) {
            val photoFile: File? = createPhotoFile()
            if (photoFile != null) {
                pathToFile = photoFile.absolutePath
                val photoURI = FileProvider.getUriForFile(
                    this.context!!,
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
        val storageDir = getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
        var file: File? = null
        try {
            file =
                File.createTempFile(name, ".jpg", storageDir)
        } catch (e: Exception) {
            Log.d("asdf", e.toString())
        }
        return file
    }

    private fun uploadFile(file: File) {
        val sharedPreferences =
            activity!!.getSharedPreferences("shared prefs", Context.MODE_PRIVATE)
        val sessionId = sharedPreferences.getString(MainData.SESION_ID, "")
        val csrftoken = sharedPreferences.getString(MainData.CRSFTOKEN, "")
        val request = UploadFileRequests(this,csrftoken, sessionId)
        request.uploadRequest(file)
    }
    private fun makePostRequest():RegistRequests{
        val activity = v.context as AppCompatActivity
        val sharedPreferences =
            activity.getSharedPreferences("shared prefs", Context.MODE_PRIVATE)
        val sessionId = sharedPreferences.getString(MainData.SESION_ID, "")
        val csrftoken = sharedPreferences.getString(MainData.CRSFTOKEN, "")
        val request = RegistRequests(
            "https://${MainData.BASE_URL}/${MainData.URL_PREFIX_ACC_BASE}/${MainData.LOGOUT}",
            "", "",
            "",
            v.context as AppCompatActivity,csrftoken, sessionId
        )
        return request
    }
    fun setImage(link:String){
        request=makePhotoRequest()
        request!!.viewPhoto(link)
    }
    private fun makePhotoRequest(): PhotoRequests {
        val activity = v.context as AppCompatActivity
        val sharedPreferences =
            activity.getSharedPreferences("shared prefs", Context.MODE_PRIVATE)
        val sessionId = sharedPreferences.getString(MainData.SESION_ID, "")
        val csrftoken = sharedPreferences.getString(MainData.CRSFTOKEN, "")
        return PhotoRequests(this,csrftoken, sessionId)
    }
}