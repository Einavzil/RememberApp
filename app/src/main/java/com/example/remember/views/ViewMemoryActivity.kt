package com.example.remember.views

import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.remember.DatabaseHelper
import com.example.remember.R
import com.example.remember.models.MemoryModel
import com.google.android.material.appbar.MaterialToolbar

class ViewMemoryActivity : AppCompatActivity() {
    private lateinit var topAppBar: MaterialToolbar
    private lateinit var dbHelper: DatabaseHelper
    private var memory: MemoryModel? = null
    private var memoryId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_memory)

        //defining the close button to finish activity
        topAppBar = findViewById(R.id.topAppBar)
        setSupportActionBar(topAppBar)

        topAppBar.setNavigationOnClickListener {
            finish()
        }

        //get the memoryId from the card clicked
        memoryId = intent.getIntExtra("memoryId", -1)
        dbHelper = DatabaseHelper(this, getString(R.string.database_name))

        loadMemoryDetails()
    }

    override fun onResume() {
        super.onResume()
        loadMemoryDetails()
    }

    private fun loadMemoryDetails() {
        if (memoryId != -1) {
            memory = dbHelper.getOneMemory(memoryId)

            if (memory != null) {
                //memory details added to the layout
                findViewById<TextView>(R.id.title_text).text = memory!!.title
                findViewById<TextView>(R.id.description_text).text = memory!!.description
                findViewById<TextView>(R.id.category_text).text = memory!!.category.name

                //load image into the layout
                val imageView = findViewById<ImageView>(R.id.selected_image)
                val bitmap = BitmapFactory.decodeFile(memory!!.image)
                if (bitmap != null) {
                    imageView.setImageBitmap(bitmap)
                } else {
                    //set a default image if the bitmap is null
                    imageView.setImageResource(R.drawable.default_memory_photo)
                }
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.view_memory_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        //if edit button clicked, start createMemoryActivity
        return if (item.itemId == R.id.action_edit) {
            val intent = Intent(this, CreateMemoryActivity::class.java).apply {
                putExtra("memoryId", memoryId)
            }
            startActivity(intent)
            true
        //else if delete icon clicked, show confirmation dialog
        } else if (item.itemId == R.id.action_delete) {
            showDeleteDialog()
            true
        } else {
            false
        }
    }

    private fun showDeleteDialog() {
        AlertDialog.Builder(this)
            .setTitle(getString(R.string.confirm_delete_dialog))
            .setMessage(getString(R.string.confirm_delete_message))
            .setPositiveButton(getString(R.string.positive_button_delete)) { _, _ ->
                if (dbHelper.deleteMemory(memoryId)) {
                    Toast.makeText(this, getString(R.string.memory_deleted_toast), Toast.LENGTH_SHORT).show()
                    finish()
                } else {
                    Toast.makeText(this, getString(R.string.delete_failed_toast), Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton(getString(R.string.negative_button_delete)) { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }
}