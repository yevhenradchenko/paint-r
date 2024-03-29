package com.paintr

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    companion object {
        private const val PERMISSIONS_REQUEST_CODE = 101
        private const val GALLERY_REQUEST_CODE = 102

        private lateinit var managePermissions: ManagePermissions

        private val PERMISSIONS = listOf<String>(
            Manifest.permission.INTERNET,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setSupportActionBar(canvasToolbar)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_paint, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        managePermissions = ManagePermissions(this, PERMISSIONS, PERMISSIONS_REQUEST_CODE)

        when (item.itemId) {
            R.id.resetCanvas -> canvasCustomView.resetCanvasDrawing()
            R.id.undoCanvas -> canvasCustomView.undoCanvasDrawing()
            R.id.redoCanvas -> canvasCustomView.redoCanvasDrawing()
            R.id.shareCanvas -> if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (managePermissions.isPermissionsGranted() == PackageManager.PERMISSION_GRANTED) {
                    shareImage()
                } else {
                    managePermissions.checkPermissions()
                }
            } else {
                shareImage()
            }

            R.id.saveCanvas -> if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (managePermissions.isPermissionsGranted() == PackageManager.PERMISSION_GRANTED) {
                    canvasCustomView.saveCanvasDrawing()
                    toast("Saved!")
                } else {
                    managePermissions.checkPermissions()
                }
            } else {
                canvasCustomView.saveCanvasDrawing()
                toast("Saved!")
            }

            R.id.openCanvas -> {
                pickFromGallery()
            }

            R.id.colorRed -> canvasCustomView.drawingColor = ContextCompat.getColor(this, R.color.colorRed)
            R.id.colorGreen -> canvasCustomView.drawingColor = ContextCompat.getColor(this, R.color.colorGreen)
            R.id.colorBlack -> canvasCustomView.drawingColor = ContextCompat.getColor(this, R.color.colorBlack)
            R.id.colorOrange -> canvasCustomView.drawingColor = ContextCompat.getColor(this, R.color.colorOrange)
            R.id.colorBrown -> canvasCustomView.drawingColor = ContextCompat.getColor(this, R.color.colorBrown)

            R.id.size10 -> canvasCustomView.strokeDrawWidth = 10f
            R.id.size12 -> canvasCustomView.strokeDrawWidth = 12f
            R.id.size14 -> canvasCustomView.strokeDrawWidth = 14f
            R.id.size16 -> canvasCustomView.strokeDrawWidth = 16f
            R.id.size18 -> canvasCustomView.strokeDrawWidth = 18f
            R.id.size20 -> canvasCustomView.strokeDrawWidth = 20f
            R.id.size24 -> canvasCustomView.strokeDrawWidth = 24f
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSIONS_REQUEST_CODE) {
            val managePermissions = ManagePermissions(this, PERMISSIONS, PERMISSIONS_REQUEST_CODE)
            if (managePermissions.isPermissionsGranted() == PackageManager.PERMISSION_GRANTED) {
                canvasCustomView.saveCanvasDrawing()
            }
        }
    }

    private fun pickFromGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        val imageTypes = arrayOf("image/jpeg", "image/png")
        intent.putExtra(Intent.EXTRA_MIME_TYPES, imageTypes)
        startActivityForResult(intent, GALLERY_REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == GALLERY_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                val uri: Uri? = data?.data
                if (uri != null) {
                    val bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, uri)
                    canvasCustomView.loadCanvasBackground(bitmap)
                }
            }
        }
    }

    private fun shareImage() {
        val uriReceiver = Uri.parse(canvasCustomView.saveCanvasDrawing())
        val shareIntent = Intent()
        shareIntent.action = Intent.ACTION_SEND
        shareIntent.putExtra(Intent.EXTRA_STREAM, uriReceiver)
        shareIntent.type = "image/jpeg"
        startActivity(Intent.createChooser(shareIntent, "Share to"))
    }
}

fun Context.toast(message: String) {
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
}
