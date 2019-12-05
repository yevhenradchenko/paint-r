package com.paintr

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_paint, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.resetCanvas -> canvasCustomView.resetCanvasDrawing()
            R.id.undoCanvas -> canvasCustomView.undoCanvasDrawing()
            R.id.redoCanvas -> canvasCustomView.redoCanvasDrawing()
        }
        return super.onOptionsItemSelected(item)
    }
}
