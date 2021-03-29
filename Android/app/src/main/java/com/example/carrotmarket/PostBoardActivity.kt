package com.example.carrotmarket

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.*
import androidx.core.content.FileProvider
import com.example.carrotmarket.dto.Board
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.TedPermission
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.lang.Exception
import java.security.Permission
import java.text.SimpleDateFormat
import java.util.*

class PostBoardActivity : AppCompatActivity() {

    val board : Board = Board(0,0,"","",0,0F,0F,"", "", "", 0, 0, 0, null)
    private val REQUEST_IMAGE_CAPTURE = 1
    lateinit var curPhotoPath : String
    lateinit var image : ImageView

    var categoryData = arrayOf("1", "2", "3", "4", "5", "6")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_post_board)
        image = findViewById<ImageView>(R.id.imageView)
        val categoryChoice = findViewById<Spinner>(R.id.categoryChoice)
        val postButton = findViewById<Button>(R.id.posting)
        val titleText = findViewById<EditText>(R.id.titleText)
        val textMessage = findViewById<EditText>(R.id.textMessage)
        val pictureButton = findViewById<Button>(R.id.picture)
        val albumButton = findViewById<Button>(R.id.album)

        var adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, categoryData)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        categoryChoice.adapter = adapter
        var listener = SpinnerListener()

        pictureButton.setOnClickListener() {
            setPermission()
            takeCapture()
        }

        albumButton.setOnClickListener() {
            setPermission()
        }
    }

    // 스피너 설정
    inner class SpinnerListener : AdapterView.OnItemSelectedListener {
        override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
            categoryData[position]
        }

        override fun onNothingSelected(parent: AdapterView<*>?) {
        }
    }

    // 카메라 촬영
    private fun takeCapture() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            takePictureIntent.resolveActivity(packageManager)?.also {
                val photoFile : File? = try {
                    createImageFile()
                } catch (e : IOException){
                    null
                }
                photoFile?.also {
                    val photoURI : Uri = FileProvider.getUriForFile(
                        this,
                        "com.example.carrotmarket.fileprovider",
                        it
                    )
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                    startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
                }
            }
        }
    }

    // 이미지 파일 생성
    private fun createImageFile(): File? {
        val timestamp : String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val storageDir : File? = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile("JPEG_${timestamp}_", ".jpg", storageDir)
            .apply { curPhotoPath = absolutePath }
    }

    // 권한 설정
    private fun setPermission() {
        val permission = object : PermissionListener {
            override fun onPermissionGranted() {
                Toast.makeText(this@PostBoardActivity, "권한 허용", Toast.LENGTH_SHORT).show()
            }

            override fun onPermissionDenied(deniedPermissions: MutableList<String>?) {
                Toast.makeText(this@PostBoardActivity, "권한 거부", Toast.LENGTH_SHORT).show()
            }
        }

        TedPermission.with(this)
            .setPermissionListener(permission)
            .setRationaleMessage("카메라 사용 권한 허용")
            .setDeniedMessage("권한을 거부하셨습니다.")
            .setPermissions(android.Manifest.permission.WRITE_EXTERNAL_STORAGE, android.Manifest.permission.READ_EXTERNAL_STORAGE, android.Manifest.permission.CAMERA)
            .check()
    }


    // 카메라에서 사진 가져온 후 실행
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {
            val bitmap : Bitmap
            val file = File(curPhotoPath)

            if(Build.VERSION.SDK_INT < 28) {
                bitmap = MediaStore.Images.Media.getBitmap(contentResolver, Uri.fromFile(file))
                image.setImageBitmap(bitmap)
            } else {
                val decode = ImageDecoder.createSource(
                    this.contentResolver,
                    Uri.fromFile(file)
                )
                bitmap = ImageDecoder.decodeBitmap(decode)
                image.setImageBitmap(bitmap)
            }
            //board.picture = bitmap
            // 굳이 저장할 필요없음
            //savePhoto(bitmap)
        }
    }

    private fun savePhoto(bitmap: Bitmap) {
        val folderPath = Environment.getExternalStorageDirectory().absolutePath + "/Android/data/com.example.carrotmarket/files/Pictures/"
        val timestamp : String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val fileName = "${timestamp}.jpeg"
        val folder = File(folderPath)
        if(!folder.isDirectory) {
            folder.mkdir()
        }

        val out = FileOutputStream(folderPath + fileName)
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out)
        Toast.makeText(this@PostBoardActivity, "앨범 저장", Toast.LENGTH_SHORT).show()
    }
}