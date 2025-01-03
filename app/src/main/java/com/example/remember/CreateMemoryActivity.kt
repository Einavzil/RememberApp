package com.example.remember

import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.widget.AdapterView.OnItemClickListener
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.remember.models.CategoryModel
import com.example.remember.models.MemoryModel
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.textfield.MaterialAutoCompleteTextView
import com.google.android.material.textfield.TextInputEditText
import java.io.File
import java.io.FileOutputStream


private const val IMAGE_PICK_CODE = 12

class CreateMemoryActivity : AppCompatActivity() {

    private var memoryId: Int = -1
    private var imagePath: String? = null
    protected  lateinit var topAppBar: MaterialToolbar
    protected lateinit var dbHelper: DatabaseHelper

    protected lateinit var titleInput: TextInputEditText
    protected  lateinit var descriptionInput: TextInputEditText
    protected  lateinit var categories: List<CategoryModel>
    protected  lateinit var currentCategorySelected: CategoryModel
    protected  lateinit var categoryDropDown: MaterialAutoCompleteTextView
    protected  lateinit var imageView: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_memory)

        topAppBar = findViewById(R.id.topAppBar)
        topAppBar.title = getString(R.string.create_memory_title)

        //exit button click listener
        topAppBar.setNavigationOnClickListener {
            finish()
        }

        memoryId = intent.getIntExtra("memoryId", -1)

        dbHelper = DatabaseHelper(this,getString(R.string.database_name))
        categories = dbHelper.getCategories()
        categoryDropDown = findViewById(R.id.category_dropdown)

        val categoryNames = ArrayList<String>()
        for (category in categories) {
            categoryNames.add(category.name)
        }
        if (categories.isNotEmpty()) {
            val adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, categories)
            categoryDropDown.setAdapter(adapter)
            categoryDropDown.onItemClickListener =
                OnItemClickListener { adapterView, _, position, _ ->
                    currentCategorySelected  = adapterView.adapter.getItem(position) as CategoryModel
                }
        } else {
            categoryDropDown.error = getString(R.string.category_list_empty)
        }

        titleInput = findViewById(R.id.title_input)
        descriptionInput = findViewById(R.id.description_input)
        imageView = findViewById(R.id.selected_image)

        findViewById<Button>(R.id.upload_image_button).setOnClickListener {
            pickImage()
        }

        if (memoryId != -1) {
            topAppBar.title = getString(R.string.update_memory_title)
            populateMemoryDetails(memoryId)
        }

        findViewById<Button>(R.id.save_button).setOnClickListener {
            val success = saveButtonClick()
            if (!success) {
            return@setOnClickListener
            } else {
                finish()
            }
        }
    }

    protected  fun saveButtonClick() : Boolean{
        //several checks to ensure fields are not empty
        if (titleInput.text.toString().trim().isEmpty()) {
            titleInput.error = getString(R.string.title_empty)
            return false
        }
        if (descriptionInput.text.toString().trim().isEmpty()) {
            descriptionInput.error = getString(R.string.description_empty)
            return false
        }
        if (categoryDropDown.text.toString().trim().isEmpty()) {
            categoryDropDown.error = getString(R.string.category_not_selected)
            return false
        }

        val memory = retrieveMemoryInput()

        if (memory != null) {
            //If memoryId exists, update the memory
            if (memoryId != -1) {
                dbHelper.updateMemory(memory)
                Toast.makeText(this, "Memory updated!", Toast.LENGTH_SHORT).show()
            }
            //else, create a new memory
            else {
                dbHelper.insertNewMemory(memory)
                Toast.makeText(this, "Memory saved!", Toast.LENGTH_SHORT).show()
            }
        }
        return true
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == IMAGE_PICK_CODE && resultCode == RESULT_OK) {
            val imageUri = data?.data
            //display the image chosen in the image view
            imageView.setImageURI(imageUri)
            // Save the image path for later use
            imagePath = imageUri?.let { saveImageToInternalStorage(this, it) }
        }
    }

    private fun pickImage() {
        //Open the image picker to allow user to choose a photo
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, IMAGE_PICK_CODE)
    }

    //saving an internal copy of image to load later
    private fun saveImageToInternalStorage(context: Context, uri: Uri): String? {
        return try {
            val inputStream = context.contentResolver.openInputStream(uri)
            val file = File(context.filesDir, "saved_image_${System.currentTimeMillis()}.jpg")
            val outputStream = FileOutputStream(file)

            val buffer = ByteArray(1024)
            var bytesRead: Int
            while (inputStream!!.read(buffer).also { bytesRead = it } != -1) {
                outputStream.write(buffer, 0, bytesRead)
            }

            inputStream.close()
            outputStream.close()

            file.absolutePath // Return the saved file path
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private fun retrieveMemoryInput(): MemoryModel? {
        //Get text from views
        val title = titleInput.text.toString().trim()
        val description = descriptionInput.text.toString().trim()

        return MemoryModel(
            id = memoryId,
            title = title,
            description = description,
            category = currentCategorySelected,
            image = imagePath
        )
    }

    private fun populateMemoryDetails (memoryId: Int) {
        val memory = dbHelper.getOneMemory(memoryId)
        if (memory != null) {

            titleInput.setText(memory.title)
            descriptionInput.setText(memory.description)

            currentCategorySelected = memory.category
            categoryDropDown.setText(memory.category.name, false)

            imagePath = memory.image

            val bitmap = BitmapFactory.decodeFile(memory.image)
            if (bitmap != null) {
                imageView.setImageBitmap(bitmap)
            }
        }
    }
}
