package com.tutushubham.habittrainer

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.tutushubham.habittrainer.db.HabitDbTable
import kotlinx.android.synthetic.main.activity_create_habit.*

class CreateHabitActivity : AppCompatActivity() {

    private val CHOOSE_IMAGE_REQUEST = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_habit)
    }

    private var imageBitmap: Bitmap? = null

    fun storeHabit(view: View) {
        if (et_title.text.toString().isBlank() || et_descr.text.toString().isBlank()) {
            Log.e("Error ", "Text input me gadbad")
            displayError("Text input me gadbad")
            return
        } else if (imageBitmap == null) {
            Log.e("Error ", "Image input me gadbad")
            displayError("Image input me gadbad")
            return
        }

        tv_error.visibility = View.INVISIBLE

        val title = et_title.text.toString()
        val descr = et_descr.text.toString()

        val habit = Habit(title, descr, imageBitmap!!)

        val id = HabitDbTable(this).store(habit)

        if (id == -1L) {
            displayError("Error in storing")
        } else {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }



    private fun displayError(s: String) {
        tv_error.text = s
        tv_error.visibility = View.VISIBLE
    }

    fun chooseImage(view: View) {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT

        val chooser = Intent.createChooser(intent, "Choose image for habit")
        startActivityForResult(chooser, CHOOSE_IMAGE_REQUEST)

        Log.e("image selection", "hehe I know this")

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == CHOOSE_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.data != null) {
            Log.e("image chosen", "hehe user chose an image")
            val bitmapUri = data.data

            bitmapUri?.let {
                if (Build.VERSION.SDK_INT < 28) {
                    val bitmap = MediaStore.Images.Media.getBitmap(
                        this.contentResolver,
                        bitmapUri
                    )
                    this.imageBitmap = bitmap
                    iv_image.setImageBitmap(bitmap)
                } else {
                    val source = ImageDecoder.createSource(this.contentResolver, bitmapUri)
                    val bitmap = ImageDecoder.decodeBitmap(source)
                    this.imageBitmap = bitmap
                    iv_image.setImageBitmap(bitmap)
                }

            }
        }

    }


}
